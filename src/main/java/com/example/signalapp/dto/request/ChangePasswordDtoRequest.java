package com.example.signalapp.dto.request;

import com.example.signalapp.service.UserService;
import com.example.signalapp.validator.MinLength;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ChangePasswordDtoRequest {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    @Size(max = UserService.MAX_PASSWORD_LENGTH)
    @MinLength
    private String password;

}
