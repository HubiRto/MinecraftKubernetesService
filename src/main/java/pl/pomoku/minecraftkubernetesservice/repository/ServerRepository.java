package pl.pomoku.minecraftkubernetesservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pomoku.minecraftkubernetesservice.entity.Server;
import pl.pomoku.minecraftkubernetesservice.entity.ServerType;

import java.util.UUID;

@Repository
public interface ServerRepository extends JpaRepository<Server, UUID> {
    Server getById(UUID id);
    boolean existsByPort(int port);
}
