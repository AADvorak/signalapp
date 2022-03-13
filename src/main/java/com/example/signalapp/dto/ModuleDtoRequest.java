package com.example.signalapp.dto;

import com.example.signalapp.validator.Container;
import com.example.signalapp.validator.Module;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDtoRequest {

    @Module
    private String module;

    @NotEmpty
    private String name;

    @Container
    private String container;

    private boolean forMenu;

    private boolean transformer;

}
