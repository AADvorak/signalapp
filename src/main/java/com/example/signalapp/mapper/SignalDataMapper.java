package com.example.signalapp.mapper;

import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.model.SignalData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SignalDataMapper {

    SignalDataMapper INSTANCE = Mappers.getMapper(SignalDataMapper.class);

    SignalDataDto signalDataToDto(SignalData signalData);

    SignalData dtoToSignalData(SignalDataDto dto);

}
