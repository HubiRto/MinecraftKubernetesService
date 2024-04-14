package pl.pomoku.minecraftkubernetesservice.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class LogFileChangeEvent extends ApplicationEvent {
    private final UUID id;
    private final List<String> newLines;

    public LogFileChangeEvent(Object source, UUID id, List<String> newLines) {
        super(source);
        this.id = id;
        this.newLines = newLines;
    }
}
