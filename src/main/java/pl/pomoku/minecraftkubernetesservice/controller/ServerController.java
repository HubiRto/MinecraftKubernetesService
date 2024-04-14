package pl.pomoku.minecraftkubernetesservice.controller;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.dto.response.ServerInfoResponse;
import pl.pomoku.minecraftkubernetesservice.entity.Server;
import pl.pomoku.minecraftkubernetesservice.model.Response;
import pl.pomoku.minecraftkubernetesservice.service.impl.ServerServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/server")
@RequiredArgsConstructor
public class ServerController {
    private final ModelMapper mapper;
    private final ServerServiceImpl serverService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

//    @MessageMapping("/logRequest")
//    @SendTo("/topic/randomNumber")
//    public int generateRandomNumber() {
//        Random random = new Random();
//        return random.nextInt(100);
//    }

    @MessageMapping("/logRequest")
    @SendTo("/topic/logs")
    public List<String> generateRandomNumber(@Payload String serverId) throws IOException {
//        Random random = new Random();
//        messagingTemplate.convertAndSend("/topic/logs", String.valueOf(random.nextInt(100)));
//        return String.valueOf(random.nextInt(100));
        return serverService.getLogsById(UUID.fromString(serverId));
//        return "siema beka";
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ServerCreateRequest request) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("id", serverService.create(request)))
                        .message("Server created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    @GetMapping("/info/{id}")
    public ResponseEntity<?> info(@PathVariable UUID id) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("server", mapper.map(serverService.getById(id), ServerInfoResponse.class)))
                        .message("Server retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("servers", serverService.getAll()))
                        .message("Servers retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> remove(@PathVariable UUID id) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("deleted", serverService.deleteById(id)))
                        .message("Server deleted")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<?> getLogs(@PathVariable UUID id) throws IOException {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("logs", serverService.getLogsById(id)))
                        .message("Last server logs")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/usage/ram/{id}")
    public ResponseEntity<?> getRAMUsage(@PathVariable UUID id) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("ramUsage", serverService.getRAMUsage(id)))
                        .message("Last server logs")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private void waitForServerReady(String podName, String podContainer, KubernetesClient client) throws InterruptedException {
        while (true) {
            String logs = client.pods().inNamespace("default")
                    .withName(podName)
                    .inContainer(podContainer)
                    .getLog();

            if (logs.contains("Timings Reset")) {
                LOGGER.info("Minecraft server is ready...");
                break;
            }

            LOGGER.info("Waiting for Minecraft server to start...");
            Thread.sleep(5000);
        }
    }
}
