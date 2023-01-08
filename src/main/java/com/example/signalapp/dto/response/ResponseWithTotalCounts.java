package com.example.signalapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWithTotalCounts<T> {

    private List<T> data;

    private long elements;

    private long pages;

}
