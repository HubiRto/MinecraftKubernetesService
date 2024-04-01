package pl.pomoku.minecraftkubernetesservice.config;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {
    @Bean
    public KubernetesClient client() {
        return new DefaultKubernetesClient(new ConfigBuilder().withNamespace("default").build());
    }
}
