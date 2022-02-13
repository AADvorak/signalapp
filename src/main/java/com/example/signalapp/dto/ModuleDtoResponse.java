package com.example.signalapp.dto;

import lombok.Data;

@Data
public class ModuleDtoResponse {

    private final int id;

    private final String module;

    private final String name;

    private final String container;

    private final boolean forMenu;

    private final boolean transformer;

}
