package pl.pomoku.minecraftkubernetesservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.entity.ConfirmationAccountToken;
import pl.pomoku.minecraftkubernetesservice.repository.ConfirmationAccountTokenRepository;
import pl.pomoku.minecraftkubernetesservice.service.ConfirmationAccountTokenService;

@Service
@RequiredArgsConstructor
public class ConfirmationAccountTokenServiceImpl implements ConfirmationAccountTokenService {
    private final ConfirmationAccountTokenRepository repository;

    @Override
    public void saveConfirmationToken(ConfirmationAccountToken token) {
        repository.save(token);
    }
}
