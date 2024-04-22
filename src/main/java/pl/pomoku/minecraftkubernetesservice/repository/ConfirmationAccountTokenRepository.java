package pl.pomoku.minecraftkubernetesservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pomoku.minecraftkubernetesservice.entity.ConfirmationAccountToken;

import java.util.Optional;

@Repository
public interface ConfirmationAccountTokenRepository extends JpaRepository<ConfirmationAccountToken, Long> {
    Optional<ConfirmationAccountToken> findByToken(String token);
}
