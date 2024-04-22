package pl.pomoku.minecraftkubernetesservice.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.dto.response.ServerInfoResponse;
import pl.pomoku.minecraftkubernetesservice.model.Response;
import pl.pomoku.minecraftkubernetesservice.service.impl.ServerServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/server")
@RequiredArgsConstructor
public class ServerController {
    private final ModelMapper mapper;
    private final ServerServiceImpl serverService;

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
        return ResponseEntity.ok(serverService.getAll());
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

    @Transactional
    @GetMapping("/logs/{id}")
    public ResponseEntity<?> getLogs(@PathVariable UUID id) {
        return ResponseEntity.ok(serverService.getLogsById(id));
    }

    @GetMapping("/usage/{id}")
    public ResponseEntity<?> getRAMUsage(@PathVariable UUID id) {
        return ResponseEntity.ok(serverService.getRAMUsage(id));
    }

    @PostMapping("/command/exec/{id}")
    public ResponseEntity<?> commandExec(@PathVariable UUID id, @RequestBody String command) throws IOException {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("cmdResult", serverService.execCommand(id, command)))
                        .message("Result of execute command: " + command)
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
}
