package pl.pomoku.minecraftkubernetesservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.dto.request.LoginRequest;
import pl.pomoku.minecraftkubernetesservice.dto.request.RegisterRequest;
import pl.pomoku.minecraftkubernetesservice.dto.response.LoginResponse;
import pl.pomoku.minecraftkubernetesservice.dto.response.RegisterResponse;
import pl.pomoku.minecraftkubernetesservice.entity.ConfirmationAccountToken;
import pl.pomoku.minecraftkubernetesservice.entity.User;
import pl.pomoku.minecraftkubernetesservice.entity.UserRole;
import pl.pomoku.minecraftkubernetesservice.exception.AppException;
import pl.pomoku.minecraftkubernetesservice.mappers.UserMapper;
import pl.pomoku.minecraftkubernetesservice.service.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EmailValidator emailValidator;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final ConfirmationAccountTokenService accountTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.email.token.expiration}")
    private long emailTokenExpirationSeconds;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userService.getByEmail(request.email()).orElseThrow(() ->
                new AppException("Nie znaleziono użytkownika", HttpStatus.BAD_REQUEST));
        return new LoginResponse(jwtService.generateToken(user));
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (emailValidator.test(request.email())) {
            throw new AppException("Nieprawidłowy adres email", HttpStatus.BAD_REQUEST);
        }

        if(userService.isExistByEmail(request.email())){
            throw new AppException("Adres email jest zajęty", HttpStatus.BAD_REQUEST);
        }

        var user = userMapper.registerRequestToUser(request);

        user.setPassword(encoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setEnabled(false);
        user.setNotLocked(false);
        user.setUsingMfa(false);
        user.setCreatedAt(LocalDateTime.now());

        user = userService.insert(user);

        String token = UUID.randomUUID().toString();

        ConfirmationAccountToken confirmationToken = ConfirmationAccountToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(emailTokenExpirationSeconds))
                .user(user)
                .build();

        accountTokenService.saveConfirmationToken(confirmationToken);
        return new RegisterResponse(String.format("http://localhost:8080/api/v1/auth/confirm?token=%s", token));
    }
}
