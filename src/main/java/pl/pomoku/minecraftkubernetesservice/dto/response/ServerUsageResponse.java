package pl.pomoku.minecraftkubernetesservice.dto.response;

import java.time.LocalDateTime;

public record ServerUsageResponse(
        LocalDateTime timeStamp,
        long cpuUsage,
        long ramUsage
) {
}
