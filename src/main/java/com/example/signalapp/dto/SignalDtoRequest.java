package com.example.signalapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class SignalDtoRequest {

    private String name;

    private String description;

    private List<SignalDataDto> data;

}
