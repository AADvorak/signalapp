/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.model;

import com.example.signalapp.dto.SignalDataDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 *
 * @author anton
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class SignalData {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private BigDecimal x;
    
    @Column
    private BigDecimal y;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = false)
    private Signal signal;

}
