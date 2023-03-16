package com.example.signalapp.dto.request;

import com.example.signalapp.validator.Container;
import com.example.signalapp.validator.MaxLength;
import com.example.signalapp.validator.Module;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ModuleDtoRequest {

    @Module
    @MaxLength
    private String module;

    @NotEmpty
    @MaxLength
    private String name;

    @Container
    private String container;

    private boolean forMenu;

    private boolean transformer;

}
