package pl.pomoku.minecraftkubernetesservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.events.LogFileChangeEvent;
import pl.pomoku.minecraftkubernetesservice.exception.AppException;
import pl.pomoku.minecraftkubernetesservice.service.LogFileWatcherService;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogFileWatcherServiceImpl implements LogFileWatcherService {
    private final ApplicationEventPublisher eventPublisher;
    private final Map<UUID, Path> filePaths = new HashMap<>();
    private final Map<UUID, WatchService> watchServices = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Dodaje plik do monitorowania zmian w logach serwera.
     *
     * @param id   identyfikator serwera
     * @param path ścieżka do pliku
     */
    @Override
    public void addFileToMonitor(UUID id, Path path) {
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new AppException(
                    "Plik " + path + " nie istnieje lub nie jest plikiem regularnym.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        if (filePaths.containsValue(path)) return;

        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new AppException(
                    "Nie udało się uruchomić nasłuchiwania pliku",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        watchServices.put(id, watchService);
        filePaths.put(id, path);

        executorService.execute(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path modifiedFile = (Path) event.context();
                            String modifiedFilePath = modifiedFile.toString();
                            if (path.endsWith(modifiedFilePath)) {
                                List<String> newLines = new ArrayList<>();
                                Files.lines(path).reduce((first, second) -> second).ifPresent(newLines::add);
                                eventPublisher.publishEvent(new LogFileChangeEvent(this, id, newLines));
                            }
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (InterruptedException | IOException e) {
                throw new AppException(
                        "Błąd podczas nasłuchiwania pliku",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        });
    }

    /**
     * Usuwa plik z monitorowania zmian w logach serwera.
     *
     * @param id identyfikator serwera
     */
    @Override
    public void removeFileFromMonitor(UUID id) {
        WatchService watchService = watchServices.get(id);
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                throw new AppException("Nie udało się wyłączyć nasłuchiwacza pliku", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        watchServices.remove(id);
        filePaths.remove(id);
    }
}
