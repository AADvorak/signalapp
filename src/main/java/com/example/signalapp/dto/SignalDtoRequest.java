package com.example.signalapp.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SignalDtoRequest {

    @NotEmpty
    private String name;

    private String description;

    @NotEmpty
    private List<SignalDataDto> data;

}
