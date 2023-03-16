package com.example.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ResponseWithTotalCounts<T> {

    private List<T> data;

    private long elements;

    private long pages;

}
