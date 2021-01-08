/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.modules;

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
@Table(name = "module")
public class Module {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "module")
    private String module;

    @Column(name = "name")
    private String name;
    
    @Column(name = "container")
    private String container;
    
    @Column(name = "for_menu")
    private boolean forMenu;
    
    @Column(name = "transformer")
    private boolean transformer;
    
    Module() {}

    Module(String module, String name, String container, boolean forMenu, boolean transformer) {
        this.module = module;
        this.name = name;
        this.container = container;
        this.forMenu = forMenu;
        this.transformer = transformer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public boolean isForMenu() {
        return forMenu;
    }

    public void setForMenu(boolean forMenu) {
        this.forMenu = forMenu;
    }

    public boolean isTransformer() {
        return transformer;
    }

    public void setTransformer(boolean transformer) {
        this.transformer = transformer;
    }
    
}
