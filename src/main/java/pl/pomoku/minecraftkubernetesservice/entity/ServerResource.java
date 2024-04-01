package pl.pomoku.minecraftkubernetesservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ServerResource {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;
    private String pvName;
    private String pvcName;
    private String serviceName;
    private String deploymentName;
}
