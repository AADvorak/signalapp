package com.example.signalapp.dto.request;

import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.service.SignalService;
import com.example.signalapp.validator.MaxLength;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class SignalDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    private String description;

    @Size(min = 2, max = SignalService.MAX_SIGNAL_LENGTH)
    private List<SignalDataDto> data;

}
