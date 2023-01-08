/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.model;

import com.example.signalapp.dto.request.SignalDtoRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 *
 * @author anton
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Signal {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String name;
    
    @Column
    private String description;
    
    @Column
    private LocalDateTime createTime = LocalDateTime.now();

    @Column
    private int userId;

    @Column(name = "max_abs_y")
    private BigDecimal maxAbsY;

    public Signal(SignalDtoRequest dtoRequest, int userId, BigDecimal maxAbsY) {
        name = dtoRequest.getName();
        description = dtoRequest.getDescription();
        this.maxAbsY = maxAbsY;
        this.userId = userId;
    }
    
}
