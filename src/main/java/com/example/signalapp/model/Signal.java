/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.model;

import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.mapper.SignalDataMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "signal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignalData> data;

    public Signal(SignalDtoRequest dtoRequest, User user) {
        name = dtoRequest.getName();
        description = dtoRequest.getDescription();
        this.user = user;
        setData(dtoRequest.getData().stream().map(SignalDataMapper.INSTANCE::dtoToSignalData).collect(Collectors.toList()));
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
