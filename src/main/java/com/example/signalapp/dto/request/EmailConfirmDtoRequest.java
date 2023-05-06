package com.example.signalapp.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class EmailConfirmDtoRequest {

    private String origin;
    private String localeTitle;
    private String localeMsg;

}
