package com.example.signalapp.mapper;

import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.dto.response.SignalWithDataDtoResponse;
import com.example.signalapp.model.Signal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SignalMapper {

    SignalMapper INSTANCE = Mappers.getMapper(SignalMapper.class);

    SignalDtoResponse signalToDto(Signal signal);

    SignalWithDataDtoResponse signalToDtoWithData(Signal signal);

}
