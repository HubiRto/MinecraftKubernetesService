package pl.pomoku.minecraftkubernetesservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    @ToString.Exclude
    @OneToOne
    @JsonIgnore
    private Server server;

    private String pvName;
    private String pvcName;
    private String serviceName;
    private String deploymentName;
}
