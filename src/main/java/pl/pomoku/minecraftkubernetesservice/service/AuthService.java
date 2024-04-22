package pl.pomoku.minecraftkubernetesservice.service;

import pl.pomoku.minecraftkubernetesservice.dto.request.LoginRequest;
import pl.pomoku.minecraftkubernetesservice.dto.request.RegisterRequest;
import pl.pomoku.minecraftkubernetesservice.dto.response.LoginResponse;
import pl.pomoku.minecraftkubernetesservice.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
}
