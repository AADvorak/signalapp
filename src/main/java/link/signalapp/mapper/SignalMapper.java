package link.signalapp.mapper;

import link.signalapp.dto.response.SignalDtoResponse;
import link.signalapp.dto.response.SignalWithDataDtoResponse;
import link.signalapp.model.Signal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SignalMapper {

    SignalMapper INSTANCE = Mappers.getMapper(SignalMapper.class);

    SignalDtoResponse signalToDto(Signal signal);

    SignalWithDataDtoResponse signalToDtoWithData(Signal signal);

}
