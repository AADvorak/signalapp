package link.signalapp.mapper;

import link.signalapp.dto.request.FolderDtoRequest;
import link.signalapp.dto.response.FolderDtoResponse;
import link.signalapp.model.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderMapper {

    FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);

    FolderDtoResponse folderToDto(Folder folder);

    Folder dtoToFolder(FolderDtoRequest dto);

}
