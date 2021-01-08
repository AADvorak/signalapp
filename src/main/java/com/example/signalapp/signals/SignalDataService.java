/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author anton
 */
@Component
public class SignalDataService {
    
    private final JdbcTemplate jdbcTemplate;

    public SignalDataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Transactional
    public void replace (List<SignalData> newSignalDataList, Integer id) {
        deleteByTitleId(id);
        insert(newSignalDataList, id);
    }
    
    public void deleteByTitleId(Integer id) {
        jdbcTemplate.update("delete from signal_data d where d.signal_title_id = ?", id);
    }
    
    public void insert(List<SignalData> newSignalDataList, Integer id) {
        newSignalDataList.forEach(signalData -> {
            jdbcTemplate.update("insert into signal_data (signal_title_id, x, y) values (?, ?, ?)", new Object[] {id, signalData.getX(), signalData.getY()});
        });
    }
    
}
