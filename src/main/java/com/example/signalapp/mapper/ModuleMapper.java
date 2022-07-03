package com.example.signalapp.mapper;

import com.example.signalapp.dto.request.ModuleDtoRequest;
import com.example.signalapp.dto.response.ModuleDtoResponse;
import com.example.signalapp.model.Module;
import com.example.signalapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class ModuleMapper {

    public static ModuleMapper INSTANCE = Mappers.getMapper(ModuleMapper.class);

    public abstract Module dtoToModule(ModuleDtoRequest dto);

    @Mappings({
            @Mapping(target = "personal", source = "user", qualifiedByName = "userToPersonal")
    })
    public abstract ModuleDtoResponse moduleToDto(Module module);

    @Named("userToPersonal")
    public static boolean userToPersonal(User user) {
        return user != null;
    }

}
