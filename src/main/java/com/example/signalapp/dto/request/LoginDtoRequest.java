package com.example.signalapp.dto.request;

import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class LoginDtoRequest {

    private String token;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
