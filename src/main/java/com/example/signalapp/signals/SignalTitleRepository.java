/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author anton
 */
public interface SignalTitleRepository extends JpaRepository <SignalTitle, Integer> {

    public List<SignalTitle> findAllByOrderByCreateTimeDesc();
    
}
