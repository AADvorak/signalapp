package com.example.signalapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalWithDataDtoResponse extends SignalDtoResponse {

    private List<BigDecimal> data;

}
