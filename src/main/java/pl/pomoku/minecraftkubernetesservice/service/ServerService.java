package pl.pomoku.minecraftkubernetesservice.service;


import org.springframework.http.ResponseEntity;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.dto.response.ServerUsageResponse;
import pl.pomoku.minecraftkubernetesservice.entity.Server;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ServerService {
    String create(ServerCreateRequest request);
    Boolean deleteById(UUID id);
    Boolean isExistById(UUID id);
    List<Server> getAll();
    Server getById(UUID id);
    List<String> getLogsById(UUID id) throws IOException;
    ServerUsageResponse getRAMUsage(UUID id);
    String execCommand(UUID id, String command) throws IOException;
}
