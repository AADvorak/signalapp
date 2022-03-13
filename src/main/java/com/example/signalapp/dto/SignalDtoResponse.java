package com.example.signalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalDtoResponse {

    private int id;

    private String name;

    private String description;

}
