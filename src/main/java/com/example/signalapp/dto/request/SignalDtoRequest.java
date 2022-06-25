package com.example.signalapp.dto.request;

import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.validator.MaxLength;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SignalDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    private String description;

    @NotEmpty
    private List<SignalDataDto> data;

}
