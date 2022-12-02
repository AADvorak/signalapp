package com.example.signalapp.dto.response;

import com.example.signalapp.dto.SignalDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalWithDataDtoResponse extends SignalDtoResponse {

    private List<SignalDataDto> data;

}
