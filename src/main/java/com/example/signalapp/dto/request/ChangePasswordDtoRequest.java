package com.example.signalapp.dto.request;

import com.example.signalapp.validator.MaxLength;
import com.example.signalapp.validator.MinLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDtoRequest {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    @MaxLength
    @MinLength
    private String password;

}
