package pl.pomoku.minecraftkubernetesservice.controller;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.service.ServerService;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/server")
@RequiredArgsConstructor
public class ServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);
    private static final Duration TIMEOUT = Duration.ofMinutes(2);
    private final ServerService serverService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ServerCreateRequest request) {
        return serverService.create(request);

//        Deployment deployment = new Deployment().getMetadata().set

//        KubernetesClient client = null;
//        try {
//            client = new DefaultKubernetesClient(new ConfigBuilder().withNamespace("default").build());
//            UUID uuid = UUID.randomUUID();
//            Pod pod = new PodBuilder()
//                    .withNewMetadata().withName("minecraft-server-%s".formatted(uuid)).endMetadata()
//                    .withNewSpec()
//                    .addNewContainer()
//                    .withName("minecraft-container-%s".formatted(uuid)).withImage("itzg/minecraft-server:java17")
//                    .addNewPort().withContainerPort(25565).endPort()
//                    .addNewEnv().withName("EULA").withValue("TRUE").endEnv()
//                    .addNewEnv().withName("TYPE").withValue("PAPER").endEnv()
//                    .endContainer()
//                    .endSpec()
//                    .build();
//
//            client.pods().create(pod);
//
//            PodResource podResource = client.pods().inNamespace("default").withName("minecraft-server-%s".formatted(uuid));
//            waitForServerReady("minecraft-server-%s".formatted(uuid), "minecraft-container-%s".formatted(uuid), client);
//
//            String podIP = podResource.get().getStatus().getPodIP();
//
//            return ResponseEntity.ok("Minecraft server created successfully. IP Address: " + podIP);
//        } catch (Exception e) {
//            LOGGER.error("Error while creating or waiting for Minecraft server", e);
//            if (client != null){
//                deletePod(client, "minecraft-server");
//            }
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating Minecraft server");
//        } finally {
//            if (client != null){
//                client.close();
//            }
//        }
    }

    @Transactional
    @GetMapping("/info/{id}")
    public ResponseEntity<?> info(@PathVariable UUID id) {
        return ResponseEntity.ok(serverService.getById(id));
    }

    @GetMapping("/allNames")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(serverService.getAllNames());
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> remove(@PathVariable UUID id) {
        return serverService.remove(id);
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

    private void deletePod(KubernetesClient client, String podName) {
        try {
            client.pods().inNamespace("default").withName(podName).delete();
        } catch (KubernetesClientException e) {
            LOGGER.error("Error deleting Pod", e);
        }
    }

}
