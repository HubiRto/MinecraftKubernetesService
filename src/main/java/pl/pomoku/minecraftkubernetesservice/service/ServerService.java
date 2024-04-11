package pl.pomoku.minecraftkubernetesservice.service;


import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.entity.Server;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ServerService {
    String create(ServerCreateRequest request);
    Boolean deleteById(UUID id);
    Boolean isExistById(UUID id);
    List<Server> getAll();
    Server getById(UUID id);
    String getLogsById(UUID id);
}
