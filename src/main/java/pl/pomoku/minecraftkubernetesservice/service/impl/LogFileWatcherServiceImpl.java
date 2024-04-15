package pl.pomoku.minecraftkubernetesservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.events.LogFileChangeEvent;
import pl.pomoku.minecraftkubernetesservice.service.LogFileWatcherService;

import java.io.IOException;
import java.io.RandomAccessFile;
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
     * @throws IOException gdy wystąpi błąd wejścia/wyjścia
     */
    @Override
    public void addFileToMonitor(UUID id, Path path) throws IOException {
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Plik " + path + " nie istnieje lub nie jest plikiem regularnym.");
        }

        if (filePaths.containsValue(path)) return;

        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        watchServices.put(id, watchService);
        filePaths.put(id, path);

        System.out.println("dodano plik");

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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        watchServices.remove(id);
        filePaths.remove(id);
    }
}
