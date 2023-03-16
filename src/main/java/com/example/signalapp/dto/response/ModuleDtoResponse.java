package com.example.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ModuleDtoResponse {

    private int id;

    private String module;

    private String name;

    private String container;

    private boolean forMenu;

    private boolean transformer;

    private boolean personal;

}
