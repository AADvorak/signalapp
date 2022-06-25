package com.example.signalapp.mapper;

import com.example.signalapp.dto.request.UserDtoRequest;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.example.signalapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDtoResponse userToDto(User user);

    User dtoToUser(UserDtoRequest dto);

}
