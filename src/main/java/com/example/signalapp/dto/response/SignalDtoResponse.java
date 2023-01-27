package com.example.signalapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalDtoResponse {

    private int id;

    private String name;

    private String description;

    private BigDecimal maxAbsY;

    @JsonProperty("xMin")
    private BigDecimal xMin;

    private BigDecimal sampleRate;

}
