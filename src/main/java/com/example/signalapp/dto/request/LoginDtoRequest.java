package com.example.signalapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDtoRequest {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
