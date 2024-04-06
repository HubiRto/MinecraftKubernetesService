package pl.pomoku.minecraftkubernetesservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pomoku.minecraftkubernetesservice.entity.ServerResource;

@Repository
public interface ServerResourceRepository extends JpaRepository<ServerResource, Long> {
}
