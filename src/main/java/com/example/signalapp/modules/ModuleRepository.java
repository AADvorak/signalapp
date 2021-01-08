/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.modules;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author anton
 */
public interface ModuleRepository extends JpaRepository<Module, Integer> {}
