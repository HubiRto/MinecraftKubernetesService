package pl.pomoku.minecraftkubernetesservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.entity.User;
import pl.pomoku.minecraftkubernetesservice.repository.UserRepository;
import pl.pomoku.minecraftkubernetesservice.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean isExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User insert(User user) {
       return userRepository.save(user);
    }
}
