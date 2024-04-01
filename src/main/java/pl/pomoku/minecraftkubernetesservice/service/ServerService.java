package pl.pomoku.minecraftkubernetesservice.service;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.pomoku.minecraftkubernetesservice.dto.request.ServerCreateRequest;
import pl.pomoku.minecraftkubernetesservice.entity.Server;
import pl.pomoku.minecraftkubernetesservice.entity.ServerResource;
import pl.pomoku.minecraftkubernetesservice.entity.ServerType;
import pl.pomoku.minecraftkubernetesservice.entity.Status;
import pl.pomoku.minecraftkubernetesservice.repository.ServerRepository;
import pl.pomoku.minecraftkubernetesservice.repository.ServerResourceRepository;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;
    private final ServerResourceRepository serverResourceRepository;
    private final KubernetesClient client;
    private static final String path = "/home/hubirto/Desktop/servers";

    public ResponseEntity<?> create(ServerCreateRequest request) {
        //check if port used
        if (serverRepository.existsByPort(request.port())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Port is already used");
        }

        //create server in database
        Server server = Server.builder()
                .name(request.name())
                .type(ServerType.valueOf(request.type().toUpperCase()))
                .ram(request.ram())
                .disk(request.disk())
                .path("/data")
                .version(request.version())
                .status(Status.DISABLE)
                .port(request.port())
                .build();
        server = serverRepository.save(server);
        UUID id = server.getId();

        //create folder to server data
        new File(path + "/" + id.toString() + "/data").mkdirs();

        //save path folder in database
        //można, zamiast tego użyć mechanizmów bazy danych
        server.setPath(path + "/" + id + "/data");
        serverRepository.save(server);
        serverResourceRepository.save(createServerResource(request, server));

        return ResponseEntity.ok("Server created successfully");
    }

    private ServerResource createServerResource(ServerCreateRequest request, Server server) {
        //create persistent volume
        PersistentVolume pv = client.persistentVolumes().createOrReplace(createPV(path + "/" + server.getId() + "/data", request));

        //create persistent volume claim
        PersistentVolumeClaim pvc = client.persistentVolumeClaims().createOrReplace(createPVC(request));

        //create service
        io.fabric8.kubernetes.api.model.Service service = client.services().createOrReplace(createService(request));

        //create deployments
        Deployment deployment = client.apps().deployments().createOrReplace(createDeployment(request));

        ServerResource serverResource = ServerResource.builder()
                .server(server)
                .pvName(pv.getMetadata().getName())
                .pvcName(pvc.getMetadata().getName())
                .serviceName(service.getMetadata().getName())
                .deploymentName(deployment.getMetadata().getName())
                .build();
        return serverResource;
    }

    public Server getById(UUID id) {
        return serverRepository.getById(id);
    }

    public boolean isExistById(UUID id) {
        return serverRepository.existsById(id);
    }

    public ResponseEntity<?> remove(UUID id) {
        if (isExistById(id)) {
//            serverRepository.deleteById(id);
//            deleteServer(id, serverRepository.getById(id).getType());
            System.out.println(serverRepository.getById(id).getServerResources());
            return ResponseEntity.ok("Delete server with ID: %s".formatted(id));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found server with ID: %s".formatted(id));
    }

    public void deleteServer(UUID id, ServerType type) {

//        Resource<PersistentVolume> pvResource = client.persistentVolumes().withName(pvName);
//        if (pvResource != null) {
//            pvResource.delete();
//        }
//
//        Resource<PersistentVolumeClaim> pvcResource = client.persistentVolumeClaims().withName(pvcName);
//        if (pvcResource != null) {
//            pvcResource.delete();
//        }
//
//        ServiceResource<io.fabric8.kubernetes.api.model.Service> serviceResource = client.services().withName(serviceName);
//        if (serviceResource != null) {
//            serviceResource.delete();
//        }
//
//        Resource<Deployment> deploymentResource = client.apps().deployments().withName(deploymentName);
//        if (deploymentResource != null) {
//            deploymentResource.delete();
//        }
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

        serviceSpec.setPorts(Collections.singletonList(servicePort));

        Map<String, String> selector = new HashMap<>();
        selector.put("app", "minecraft-" + type.toString().toLowerCase());
        serviceSpec.setSelector(selector);

        if (type == ServerType.BUNGEECORD || type == ServerType.VELOCITY) {
            serviceSpec.setType("LoadBalancer");
        }

        service.setSpec(serviceSpec);
        return service;
    }

    private Deployment createDeployment(ServerCreateRequest request) {
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

        container.setEnv(envs);

        VolumeMount volumeMount = new VolumeMount();
        volumeMount.setMountPath("/data");
        volumeMount.setName("minecraft-" + type.toString().toLowerCase() + "-pv-storage");
        container.setVolumeMounts(Collections.singletonList(volumeMount));

        podSpec.setContainers(Collections.singletonList(container));

        PersistentVolumeClaimVolumeSource pvcSource = new PersistentVolumeClaimVolumeSource();
        pvcSource.setClaimName("minecraft-" + type.toString().toLowerCase() + "-pvc");

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
