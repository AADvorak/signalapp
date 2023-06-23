package link.signalapp.mapper;

import link.signalapp.dto.request.UserDtoRequest;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDtoResponse userToDto(User user);

    User dtoToUser(UserDtoRequest dto);

}
