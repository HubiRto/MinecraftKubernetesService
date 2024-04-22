package pl.pomoku.minecraftkubernetesservice.service;

import pl.pomoku.minecraftkubernetesservice.entity.ConfirmationAccountToken;

public interface ConfirmationAccountTokenService {
    void saveConfirmationToken(ConfirmationAccountToken token);
}
