package pl.pomoku.minecraftkubernetesservice.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.pomoku.minecraftkubernetesservice.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getByEmail(String email);
    boolean isExistByEmail(String email);
    User insert(User user);
}
