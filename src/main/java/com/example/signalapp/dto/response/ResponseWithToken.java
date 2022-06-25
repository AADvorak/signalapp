package com.example.signalapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseWithToken<T> {

    private T response;

    private String token;

}
