/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import java.time.LocalDateTime;
import java.util.List;
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
    
    Signal () {}

    Signal(String name, String description, List<SignalData> data) {
        this.name = name;
        this.description = description;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<SignalData> getData() {
        return data;
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
        this.data.forEach(item -> {
            item.setSignal(this);
        });
    }
    
}
