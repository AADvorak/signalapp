/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.model;

import com.example.signalapp.dto.SignalDtoRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author anton
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "signal")
public class Signal {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "signal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignalData> data;

    public Signal(SignalDtoRequest dtoRequest) {
        name = dtoRequest.getName();
        description = dtoRequest.getDescription();
        setData(dtoRequest.getData().stream().map(SignalData::new).collect(Collectors.toList()));
    }

    public void setData(List<SignalData> data) {
        if (this.data == null) {
            this.data = data;
        } else {
            this.data.clear();
            this.data.addAll(data);
        }
        this.setSignalToData();
    }
    
    public void setSignalToData() {
        this.data.forEach(item -> item.setSignal(this));
    }
    
}
