package com.example.signalapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDtoResponse {

    private int id;

    private String module;

    private String name;

    private String container;

    private boolean forMenu;

    private boolean transformer;

    private boolean personal;

}
