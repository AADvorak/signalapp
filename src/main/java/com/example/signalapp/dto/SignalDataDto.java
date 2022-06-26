package com.example.signalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalDataDto {

    private BigDecimal x;

    private BigDecimal y;

}
