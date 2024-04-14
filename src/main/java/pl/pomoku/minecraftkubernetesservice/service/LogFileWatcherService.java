package pl.pomoku.minecraftkubernetesservice.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public interface LogFileWatcherService {
    void addFileToMonitor(UUID id, Path path) throws IOException;
    void removeFileFromMonitor(UUID id);
}
