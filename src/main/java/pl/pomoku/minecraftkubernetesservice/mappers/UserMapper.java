package pl.pomoku.minecraftkubernetesservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import pl.pomoku.minecraftkubernetesservice.dto.request.RegisterRequest;
import pl.pomoku.minecraftkubernetesservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @ValueMappings({
            @ValueMapping(source = "firstName", target = "firstName"),
            @ValueMapping(source = "lastName", target = "lastName"),
            @ValueMapping(source = "email", target = "email"),
            @ValueMapping(source = "phone", target = "phone"),
            @ValueMapping(source = "password", target = "password")
    })
    User registerRequestToUser(RegisterRequest request);

}
