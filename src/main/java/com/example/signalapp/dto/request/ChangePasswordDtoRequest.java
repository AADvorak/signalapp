package com.example.signalapp.dto.request;

import com.example.signalapp.service.UserService;
import com.example.signalapp.validator.MinLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDtoRequest {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    @Size(max = UserService.MAX_PASSWORD_LENGTH)
    @MinLength
    private String password;

}
