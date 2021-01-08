/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author anton
 */
@Entity
@Table(name = "signal_data")
public class SignalData {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "signal_title_id")
    private Integer signalTitleId;
    
    @Column(name = "x")
    private Double x;
    
    @Column(name = "y")
    private Double y;
    
    SignalData () {}

    SignalData(Integer signalTitleId, Double x, Double y) {
        this.signalTitleId = signalTitleId;
        this.x = x;
        this.y = y;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSignalTitleId() {
        return signalTitleId;
    }

    public void setSignalTitleId(Integer signalTitleId) {
        this.signalTitleId = signalTitleId;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
    
}
