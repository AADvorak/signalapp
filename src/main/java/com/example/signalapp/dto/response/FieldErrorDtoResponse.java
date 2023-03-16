package com.example.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class FieldErrorDtoResponse {

    private String code;

    private String field;

    private String message;

}
