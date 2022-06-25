package com.example.signalapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {

    private int id;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String email;

    private boolean emailConfirmed;

}
