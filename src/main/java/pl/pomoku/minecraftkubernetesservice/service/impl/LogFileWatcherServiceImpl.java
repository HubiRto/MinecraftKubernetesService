package pl.pomoku.minecraftkubernetesservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.events.LogFileChangeEvent;
import pl.pomoku.minecraftkubernetesservice.service.LogFileWatcherService;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class LogFileWatcherServiceImpl implements LogFileWatcherService {
    private final ApplicationEventPublisher eventPublisher;
    private final Map<UUID, WatchService> watchServices = new HashMap<>();
    private final Map<UUID, Path> filePaths = new HashMap<>();
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
                        System.out.println("zmiana");
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("zmiana2");
                            Path modifiedFile = (Path) event.context();
                            String modifiedFilePath = modifiedFile.toString();
                            System.out.println("modifiedFilePath: " + modifiedFilePath);
                            System.out.println("path.toString(): " + path.toString());
                            if (path.endsWith(modifiedFilePath)) {
                                System.out.println("zmiana3");
//                                List<String> newLines = readNewLines(id);
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

    /**
     * Odczytuje nowe linie z monitorowanego pliku.
     *
     * @param id identyfikator serwera
     * @return lista nowych linii z pliku
     * @throws IOException gdy wystąpi błąd wejścia/wyjścia
     */
    private List<String> readNewLines(UUID id) throws IOException {
//        Path path = filePaths.get(id);
//        RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
//        long pointer = file.length();
//        List<String> newLines = new ArrayList<>();
//
//        StringBuilder lineBuilder = new StringBuilder();
//        while (pointer > 0) {
//            pointer--;
//            file.seek(pointer);
//            int c = file.read();
//            if (c == '\n' || c == '\r') {
//                if (!lineBuilder.isEmpty()) {
//                    newLines.add(0, lineBuilder.toString());
//                    lineBuilder.setLength(0);
//                }
//                if (pointer == 0) {
//                    break;
//                }
//            } else {
//                lineBuilder.insert(0, (char) c);
//            }
//        }
//
//        if (!lineBuilder.isEmpty()) {
//            newLines.add(0, lineBuilder.toString());
//        }
//
//        file.close();
//        return newLines;

        RandomAccessFile file = new RandomAccessFile(filePaths.get(id).toFile(), "r");

        long fileLength = file.length();
        long pointer = fileLength;
//        StringBuilder newLines = new StringBuilder();

        List<String> lines = new ArrayList<>();

        while (pointer > 0) {
            pointer--;
            file.seek(pointer);
            int c = file.read();
            if (c == '\n' || c == '\r') {
                String line = file.readLine();
                if (line != null) {
                    lines.add(line);
//                    newLines.insert(0, line + "\n");
                }
                if (pointer == 0) {
                    break;
                }
            }
        }

//        System.out.println(newLines.toString());

        file.close();

        for (String line : lines) {
            System.out.println(line);
        }

        return lines;
    }
}
