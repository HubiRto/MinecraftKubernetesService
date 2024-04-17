package pl.pomoku.minecraftkubernetesservice.utils;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.ServiceResource;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.entity.Server;
import pl.pomoku.minecraftkubernetesservice.entity.ServerResource;
import pl.pomoku.minecraftkubernetesservice.entity.ServerType;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServerResourceManager {
    private final KubernetesClient client;
    @Value("${application.volumes.path}")
    private String PATH;
    private static final String MINECRAFT_LABEL_PREFIX = "minecraft-";

    public String getServerLogsByPodName(String podName) {
        Pod pod = getPodByDeploymentName(podName);
        return client.pods().inNamespace("default").withName(pod.getMetadata().getName()).getLog(true);
    }

    public String getServerRamUsage(String podName) {
        Pod pod = getPodByDeploymentName(podName);
        PodMetrics podMetrics = client.top().pods().metrics("default", pod.getMetadata().getName());
        return podMetrics.getContainers().get(0).getUsage().get("memory").getAmount();
    }

    public Pod getPodByDeploymentName(String deploymentName) {
        List<Pod> pods = client
                .pods()
                .list()
                .getItems();

        List<Pod> matchingPods = pods.stream()
                .filter(pod -> pod.getMetadata().getName().startsWith(deploymentName))
                .collect(Collectors.toList());

        if (matchingPods.isEmpty()) {
            log.error("Nie znaleziono pody dla deployments: {}", deploymentName);
            return null;
        }

        return matchingPods.get(0);
    }

    public ServerResource createServerResource(ServerCreateRequest request, Server server) {
        //create persistent volume
        PersistentVolume pv = client.persistentVolumes().createOrReplace(createPV(PATH + "/" + server.getId() + "/data", request));

        //create persistent volume claim
        PersistentVolumeClaim pvc = client.persistentVolumeClaims().createOrReplace(createPVC(request));

        //create service
        Service service = client.services().createOrReplace(createService(request));

        //create deployments
        Deployment deployment = client.apps().deployments().createOrReplace(createDeployment(request, pvc));

        System.out.println(deployment.getMetadata().getName());
        System.out.println(deployment.getSpec().getTemplate().getMetadata().getName());

        ServerResource serverResource = ServerResource.builder()
                .server(server)
                .pvName(pv.getMetadata().getName())
                .pvcName(pvc.getMetadata().getName())
                .serviceName(service.getMetadata().getName())
                .deploymentName(deployment.getMetadata().getName())
                .build();
        return serverResource;
    }

    public void deleteServerResources(ServerResource resource) {
        Resource<PersistentVolumeClaim> pvcResource = client.persistentVolumeClaims().withName(resource.getPvcName());
        deleteResource(pvcResource, resource.getPvcName());

        Resource<PersistentVolume> pvResource = client.persistentVolumes().withName(resource.getPvName());
        deleteResource(pvResource, resource.getPvName());

        ServiceResource<Service> serviceResource = client.services().withName(resource.getServiceName());
        deleteResource(serviceResource, resource.getServiceName());

        Resource<Deployment> deploymentResource = client.apps().deployments().withName(resource.getDeploymentName());
        deleteResource(deploymentResource, resource.getDeploymentName());
    }

    private void deleteResource(Resource<?> resource, String resourceName) {
        if (resource != null) {
            resource.delete();
            log.info("Delete: {}", resourceName);
        }
    }

    private PersistentVolume createPV(String path, ServerCreateRequest request) {
        PersistentVolume pv = new PersistentVolume();
        pv.setApiVersion("v1");
        pv.setKind("PersistentVolume");

        ObjectMeta metadata = new ObjectMeta();
        metadata.setGenerateName("minecraft-" + request.type().toLowerCase() + "-pv-");
        pv.setMetadata(metadata);

        Map<String, String> labels = new HashMap<>();
        labels.put("type", "minecraft-" + request.type().toLowerCase());
        metadata.setLabels(labels);

        PersistentVolumeSpec pvSpec = new PersistentVolumeSpec();
        pvSpec.setStorageClassName("manual");
        pvSpec.setCapacity(Collections.singletonMap("storage", new Quantity(request.disk())));
        pvSpec.setAccessModes(Collections.singletonList("ReadWriteOnce"));

        HostPathVolumeSource hostPath = new HostPathVolumeSource();
        hostPath.setPath(path);
        pvSpec.setHostPath(hostPath);

        pv.setSpec(pvSpec);
//        System.out.println(Serialization.asYaml(pv));
        return pv;
    }

    private PersistentVolumeClaim createPVC(ServerCreateRequest request) {
        PersistentVolumeClaim pvc = new PersistentVolumeClaim();
        pvc.setApiVersion("v1");
        pvc.setKind("PersistentVolumeClaim");

        ObjectMeta metadata = new ObjectMeta();
        metadata.setGenerateName("minecraft-" + request.type().toLowerCase() + "-pvc-");
        pvc.setMetadata(metadata);

        PersistentVolumeClaimSpec pvcSpec = new PersistentVolumeClaimSpec();
        pvcSpec.setStorageClassName("manual");
        pvcSpec.setAccessModes(Collections.singletonList("ReadWriteOnce"));

        VolumeResourceRequirements requests = new VolumeResourceRequirements();
        requests.setRequests(Collections.singletonMap("storage", new Quantity(request.disk())));
        pvcSpec.setResources(requests);

        pvc.setSpec(pvcSpec);
//        System.out.println(Serialization.asYaml(pvc));
        return pvc;
    }

    private io.fabric8.kubernetes.api.model.Service createService(ServerCreateRequest request) {
        io.fabric8.kubernetes.api.model.Service service = new io.fabric8.kubernetes.api.model.Service();
        service.setApiVersion("v1");
        service.setKind("Service");

        ObjectMeta metadata = new ObjectMeta();
        metadata.setGenerateName("minecraft-" + request.type().toLowerCase() + "-service-");
        service.setMetadata(metadata);

        ServiceSpec serviceSpec = new ServiceSpec();
        ServicePort servicePort = new ServicePort();
        servicePort.setName("minecraft-" + request.type().toLowerCase() + "-service-port");
        servicePort.setProtocol("TCP");
        ServerType type = ServerType.valueOf(request.type().toUpperCase());
        servicePort.setPort(type == ServerType.BUNGEECORD || type == ServerType.VELOCITY ? 25577 : 25565);
        servicePort.setTargetPort(new IntOrString(request.port()));

        ServicePort rconPort = new ServicePort();
        rconPort.setName("rcon-" + request.type().toLowerCase() + "-port");
//        rconPort.setProtocol("TCP");
        rconPort.setPort(28016);
        rconPort.setTargetPort(new IntOrString(28016));

        serviceSpec.setPorts(Arrays.asList(servicePort, rconPort));

        Map<String, String> selector = new HashMap<>();
        selector.put("app", "minecraft-" + type.toString().toLowerCase());
        serviceSpec.setSelector(selector);

        if (type == ServerType.BUNGEECORD || type == ServerType.VELOCITY) {
            serviceSpec.setType("LoadBalancer");
        }

        service.setSpec(serviceSpec);
        return service;
    }

    private Deployment createDeployment(ServerCreateRequest request, PersistentVolumeClaim pvc) {
        Deployment deployment = new Deployment();
        deployment.setApiVersion("apps/v1");
        deployment.setKind("Deployment");

        ObjectMeta metadata = new ObjectMeta();
        metadata.setGenerateName("minecraft-" + request.type().toLowerCase() + "-deployment-");
        deployment.setMetadata(metadata);

        DeploymentSpec deploymentSpec = new DeploymentSpec();

        PodTemplateSpec templateSpec = new PodTemplateSpec();

        metadata = new ObjectMeta();
        metadata.getLabels().put("app", "minecraft-" + request.type().toLowerCase());
        templateSpec.setMetadata(metadata);

        PodSpec podSpec = new PodSpec();
        Container container = new Container();
        container.setName("minecraft-" + request.type().toLowerCase());
        container.setImage("itzg/minecraft-server:java17");

        container.setPorts(Collections.singletonList(new ContainerPortBuilder().withContainerPort(25565).build()));

        List<EnvVar> envs = new ArrayList<>();

        ServerType type = ServerType.valueOf(request.type().toUpperCase());
        if (type != ServerType.BUNGEECORD && type != ServerType.VELOCITY) {
            EnvVar eulaVar = new EnvVar();
            eulaVar.setName("EULA");
            eulaVar.setValue("TRUE");
            envs.add(eulaVar);
        }

        EnvVar typeVar = new EnvVar();
        typeVar.setName("TYPE");
        typeVar.setValue(type.toString().toUpperCase());
        envs.add(typeVar);


        //RCON
        EnvVar rconEnableVar = new EnvVar();
        rconEnableVar.setName("ENABLE_RCON");
        rconEnableVar.setValue("TRUE");
        envs.add(rconEnableVar);

        EnvVar rconPortVar = new EnvVar();
        rconPortVar.setName("RCON_PORT");
        rconPortVar.setValue(String.valueOf(28016));
        envs.add(rconPortVar);

        EnvVar rconPasswordVar = new EnvVar();
        rconPasswordVar.setName("RCON_PASSWORD");
        rconPasswordVar.setValue(String.valueOf(1234));
        envs.add(rconPasswordVar);

        container.setEnv(envs);

        VolumeMount volumeMount = new VolumeMount();
        volumeMount.setMountPath("/data");
        volumeMount.setName("minecraft-" + type.toString().toLowerCase() + "-pv-storage");
        container.setVolumeMounts(Collections.singletonList(volumeMount));

        podSpec.setContainers(Collections.singletonList(container));

        PersistentVolumeClaimVolumeSource pvcSource = new PersistentVolumeClaimVolumeSource();
        pvcSource.setClaimName(pvc.getMetadata().getName());

        Volume volume = new Volume();
        volume.setName("minecraft-" + type.toString().toLowerCase() + "-pv-storage");
        volume.setPersistentVolumeClaim(pvcSource);

        podSpec.setVolumes(Collections.singletonList(volume));

        templateSpec.setSpec(podSpec);

        deploymentSpec.setTemplate(templateSpec);

        LabelSelector selector = new LabelSelectorBuilder()
                .withMatchLabels(Collections.singletonMap("app", "minecraft-" + type.toString().toLowerCase()))
                .build();

        deploymentSpec.setSelector(selector);
        deployment.setSpec(deploymentSpec);

        System.out.println(Serialization.asYaml(deployment));
        return deployment;
    }

}
