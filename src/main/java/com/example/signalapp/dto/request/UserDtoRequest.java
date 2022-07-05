package com.example.signalapp.dto.request;

import com.example.signalapp.validator.MaxLength;
import com.example.signalapp.validator.MinLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRequest {

    @MaxLength
    private String firstName;

    @MaxLength
    private String lastName;

    @MaxLength
    private String patronymic;

    @NotEmpty
    @MaxLength
    @Email
    private String email;

    @MaxLength
    @MinLength
    private String password;

}
