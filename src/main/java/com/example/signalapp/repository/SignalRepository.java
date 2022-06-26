/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.repository;

import com.example.signalapp.model.Signal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author anton
 */
public interface SignalRepository extends JpaRepository <Signal, Integer> {

    List<Signal> findByUserId(int userId);

    Signal findByIdAndUserId(int id, int userId);

    int deleteByIdAndUserId(int id, int userId);

}
