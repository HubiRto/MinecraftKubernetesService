package pl.pomoku.minecraftkubernetesservice.listeners;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import pl.pomoku.minecraftkubernetesservice.events.LogFileChangeEvent;

@Component
@RequiredArgsConstructor
public class LogFileChangeEventListener implements ApplicationListener<LogFileChangeEvent> {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(@NonNull LogFileChangeEvent event) {
        messagingTemplate.convertAndSendToUser(event.getId().toString(),
                "/server/logs",
                event.getNewLines());
        System.out.println("Publish message to user: %s".formatted(event.getId()));
        System.out.println("And channel: /user/%s/server/logs".formatted(event.getId()));
    }
}
