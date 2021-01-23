/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author anton
 */
public interface SignalDataRepository extends JpaRepository <SignalData, Integer> {
    
    @Query(value = "select d.x, d.y from signal_data d where d.signal_id = ?1", nativeQuery = true)
    public List<SignalDataXandY> findBySignalId(Integer signalId);
    
}
