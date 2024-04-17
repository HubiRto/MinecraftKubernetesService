package pl.pomoku.minecraftkubernetesservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.entity.Server;
import pl.pomoku.minecraftkubernetesservice.entity.ServerResource;
import pl.pomoku.minecraftkubernetesservice.entity.ServerType;
import pl.pomoku.minecraftkubernetesservice.entity.Status;
import pl.pomoku.minecraftkubernetesservice.exception.AppException;
import pl.pomoku.minecraftkubernetesservice.repository.ServerRepository;
import pl.pomoku.minecraftkubernetesservice.repository.ServerResourceRepository;
import pl.pomoku.minecraftkubernetesservice.service.LogFileWatcherService;
import pl.pomoku.minecraftkubernetesservice.service.ServerService;
import pl.pomoku.minecraftkubernetesservice.utils.ServerResourceManager;
import pl.pomoku.minecraftkubernetesservice.utils.rcon.Rcon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerServiceImpl implements ServerService {
    private final ServerRepository serverRepository;
    private final ServerResourceRepository serverResourceRepository;
    private final ServerResourceManager serverResourceManager;
    private final LogFileWatcherService logFileWatcherService;
    private final ConcurrentHashMap<UUID, Rcon> rconClients = new ConcurrentHashMap<>();

    @Value("${application.volumes.path}")
    private String PATH;

    @Override
    public String create(ServerCreateRequest request) {
        if (serverRepository.existsByPort(request.port())) {
            throw new AppException("Port %s is already exist".formatted(request.port()), HttpStatus.BAD_REQUEST);
        }

        Server server = Server.builder()
                .name(request.name())
                .type(ServerType.valueOf(request.type().toUpperCase()))
                .ram(request.ram())
                .disk(request.disk())
                .path("/data")
                .version(request.version())
                .status(Status.DISABLE)
                .port(request.port())
                .rconPort(request.rconPort())
                .ipAddress(request.ipAddress())
                .build();
        server = serverRepository.save(server);
        UUID id = server.getId();

        String dataDirectoryPath = PATH + "/" + id.toString() + "/data";
        boolean isCreated = new File(dataDirectoryPath).mkdirs();
        if (!isCreated) throw new AppException(
                "Nie udało się utworzyć folderu: %s".formatted(dataDirectoryPath),
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        server.setPath(PATH + "/" + id);
        serverRepository.save(server);
        serverResourceRepository.save(serverResourceManager.createServerResource(request, server));

        return String.valueOf(id);
    }

    @Override
    public Server getById(UUID id) {
        isExistByIdVoid(id);
        return serverRepository.getById(id);
    }

    @Override
    public List<String> getLogsById(UUID id){
        isExistByIdVoid(id);
        Path logFilePath = getLogsPathById(id);
        logFileWatcherService.addFileToMonitor(id, logFilePath);
        addRCONConnection(id);
        return readLinesFromFile(logFilePath);
    }

    private void addRCONConnection(UUID id) {
        if (rconClients.containsKey(id)) return;
        Server server = serverRepository.getById(id);

        Rcon rcon;
        try {
            rcon = Rcon.open(server.getIpAddress(), server.getRconPort());
        } catch (IOException e) {
            throw new AppException("Nie udało się podłaczyć z RCON", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            rcon.authenticate("1234");
        } catch (IOException e) {
            try {
                rcon.close();
            } catch (IOException ex) {
                throw new AppException("Nie udało się zamknąć połączenia z RCON", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            throw new AppException("Niepoprawne hasło do klienta RCON", HttpStatus.BAD_REQUEST);
        }
        this.rconClients.put(id, rcon);
    }

    private Path getLogsPathById(UUID id) {
        return Paths.get(PATH + "/" + id + "/data/logs/latest.log");
    }

    private static List<String> readLinesFromFile(Path filePath) {
        List<String> lines = new ArrayList<>();
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            try {
                lines = Files.readAllLines(filePath);
            } catch (IOException e) {
                throw new AppException(
                        "Nie udało się odczytać nowych linii",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        }
        return lines;
    }

    @Override
    public String getRAMUsage(UUID id) {
        if (!isExistById(id)) {
            throw new AppException("Not found server with id: %s".formatted(id), HttpStatus.NOT_FOUND);
        }
        ServerResource resource = serverResourceRepository.getByServer(serverRepository.getById(id));
        return serverResourceManager.getServerRamUsage(resource.getDeploymentName());
    }

    @Override
    public String execCommand(UUID id, String command) throws IOException {
        isExistByIdVoid(id);
        return this.rconClients.get(id).sendCommand(command);
    }

    @Override
    public Boolean isExistById(UUID id) {
        return serverRepository.existsById(id);
    }

    public void isExistByIdVoid(UUID id) {
        if (!isExistById(id)) {
            throw new AppException("Not found server with id: %s".formatted(id), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public Boolean deleteById(UUID id) {
        if (isExistById(id)) {
            Server server = serverRepository.getById(id);
            serverResourceManager.deleteServerResources(server.getServerResources());
            serverRepository.delete(server);

            try {
                Path dir = Paths.get(server.getPath());
                Files
                        .walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                log.info("Deleting: {}", path);
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new AppException("Failed to delete path: %s".formatted(path), HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        });
            } catch (IOException e) {
                throw new AppException("Failed to delete server with ID: %s. Please try again later.".formatted(id), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Delete server with ID: {}", id);
            return TRUE;
        }
        throw new AppException("Not found server with ID: %s".formatted(id), HttpStatus.NOT_FOUND);
    }

    @Override
    public List<Server> getAll() {
        List<Server> serversNames = serverRepository.findAll();
        if (serversNames.isEmpty()) {
            throw new AppException("Not found any server", HttpStatus.NOT_FOUND);
        }
        return serversNames;
    }
}
