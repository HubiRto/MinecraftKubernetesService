package pl.pomoku.minecraftkubernetesservice.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NormalServerStatus {
    SERVER_UP("SERVER_UP"),
    SERVER_DOWN("SERVER_DOWN");
    private final String status;
}
