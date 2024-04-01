package pl.pomoku.minecraftkubernetesservice;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.*;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.rmi.server.LogStream.log;

@SpringBootApplication
public class MinecraftKubernetesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinecraftKubernetesServiceApplication.class, args);
    }

    @RequiredArgsConstructor
    @RestController
    @RequestMapping("/api/v1/test")
    static class Controller {
        @GetMapping("/test")
        public ResponseEntity<?> test() {
            try (KubernetesClient client = new DefaultKubernetesClient()) {

                PodMetricsList podMetricList = client.top().pods().metrics();
                for(PodMetrics metrics: podMetricList.getItems())
                {
                    for(ContainerMetrics containerMetric : metrics.getContainers())
                    {
                        System.out.println(containerMetric.getName() +
                            " " + containerMetric.getUsage().get("cpu") +
                            " " + containerMetric.getUsage().get("memory"));
//                        Quantity quantity = containerMetric.getUsage().get("cpu");
//                        String amount = quantity.getAmount();
//                        System.out.println(amount);
                    }
                }

//                NodeMetricsList nodeMetricList = client.top().nodes().metrics();
//
//                System.out.println("name CPU(cores) CPU(%) Memory(Bytes) Memory(%)");
//                for (NodeMetrics nodeMetrics : nodeMetricList.getItems()) {
//                    System.out.println(nodeMetrics.toString());
//                    System.out.println(nodeMetrics.getUsage().toString());
//                    System.out.println(nodeMetrics.getMetadata().getName() +
//                            " " + nodeMetrics.getUsage().get("cpu") +
//                            " " + nodeMetrics.getUsage().get("memory"));
//                }

            } catch (KubernetesClientException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok("Siema");
        }
    }

}
