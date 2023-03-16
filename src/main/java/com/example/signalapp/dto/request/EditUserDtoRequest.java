package com.example.signalapp.dto.request;

import com.example.signalapp.validator.MaxLength;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class EditUserDtoRequest {

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

}
