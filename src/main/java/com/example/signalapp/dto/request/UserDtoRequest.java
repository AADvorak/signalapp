package com.example.signalapp.dto.request;

import com.example.signalapp.service.UserService;
import com.example.signalapp.validator.MaxLength;
import com.example.signalapp.validator.MinLength;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserDtoRequest {

    private String token;

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

    @Size(max = UserService.MAX_PASSWORD_LENGTH)
    @MinLength
    private String password;

}
