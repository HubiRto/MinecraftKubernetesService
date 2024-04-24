package pl.pomoku.minecraftkubernetesservice.controller;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomoku.minecraftkubernetesservice.dto.request.LoginRequest;
import pl.pomoku.minecraftkubernetesservice.dto.request.RegisterRequest;
import pl.pomoku.minecraftkubernetesservice.service.AuthService;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final KubernetesClient client;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws URISyntaxException {
        return ResponseEntity.created(new URI(authService.register(request).url())).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(client.pods().inNamespace("default"));
    }
}
