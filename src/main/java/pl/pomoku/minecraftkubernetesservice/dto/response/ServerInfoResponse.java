package pl.pomoku.minecraftkubernetesservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pomoku.minecraftkubernetesservice.entity.ServerResource;
import pl.pomoku.minecraftkubernetesservice.entity.ServerType;
import pl.pomoku.minecraftkubernetesservice.entity.Status;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ServerInfoResponse {
    private UUID id;
    private String name;
    private ServerType type;
    private String ram;
    private String disk;
    private String path;
    private String version;
    private Status status;
    private int port;
    private List<String> networkServers;
    private ServerResource serverResources;
}
