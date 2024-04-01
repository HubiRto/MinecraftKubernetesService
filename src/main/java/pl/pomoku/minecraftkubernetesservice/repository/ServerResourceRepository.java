package pl.pomoku.minecraftkubernetesservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pomoku.minecraftkubernetesservice.entity.ServerResource;

public interface ServerResourceRepository extends JpaRepository<ServerResource, Long> {
}
