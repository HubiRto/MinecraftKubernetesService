package pl.pomoku.minecraftkubernetesservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ServerType type;
    private String ram;
    private String disk;
    private String path;
    private String version;
    @Enumerated(EnumType.STRING)
    private Status status;
    private int port;

    @OneToMany
    @ToString.Exclude
    private List<Server> networkServers;

    @OneToOne(mappedBy = "server", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ServerResource serverResources;
}
