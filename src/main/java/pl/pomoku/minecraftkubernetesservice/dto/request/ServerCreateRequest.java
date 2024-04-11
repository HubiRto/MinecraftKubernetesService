package pl.pomoku.minecraftkubernetesservice.dto.request;

public record ServerCreateRequest(
        String name,
        String ram,
        String disk,
        String version,
        String type,
        int port,
        String ipAddress,
        int rconPort
) {
}
