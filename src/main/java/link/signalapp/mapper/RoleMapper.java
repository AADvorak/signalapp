package link.signalapp.mapper;

import link.signalapp.dto.response.RoleDtoResponse;
import link.signalapp.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDtoResponse roleToDto(Role role);

}
