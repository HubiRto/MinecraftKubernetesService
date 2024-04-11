package pl.pomoku.minecraftkubernetesservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
    @Column(unique = true)
    @NotEmpty(message = "IP Adress cannot be empty or null")
    private String ipAddress;
    private int rconPort;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Server> networkServers;

    @JsonIgnore
    @OneToOne(mappedBy = "server", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ServerResource serverResources;
}
