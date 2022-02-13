package com.example.signalapp.dto;

import lombok.Data;

@Data
public class ModuleDtoRequest {

    private String module;

    private String name;

    private String container;

    private boolean forMenu;

    private boolean transformer;

}
