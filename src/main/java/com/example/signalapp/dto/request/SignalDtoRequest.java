package com.example.signalapp.dto.request;

import com.example.signalapp.service.SignalService;
import com.example.signalapp.validator.MaxLength;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SignalDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    private String description;

    @Positive
    private BigDecimal maxAbsY;

    @JsonProperty("xMin")
    @NotNull
    private BigDecimal xMin;

    @Positive
    private BigDecimal sampleRate;

    @NotEmpty
    @Size(max = SignalService.MAX_SIGNAL_LENGTH)
    private List<BigDecimal> data;

}
