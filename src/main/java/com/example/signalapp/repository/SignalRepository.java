/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.repository;

import com.example.signalapp.model.Signal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author anton
 */
public interface SignalRepository extends PagingAndSortingRepository<Signal, Integer> {

    @Query(value = "select count(1) from signal " +
            "where user_id = :userId", nativeQuery = true)
    int countByUserId(int userId);

    Page<Signal> findByUserId(int userId, Pageable pageable);

    @Query(value = "select * from signal " +
            "where user_id = :userId " +
            "and (upper(name) like upper(:filter) " +
            "or upper(description) like upper(:filter))", nativeQuery = true)
    Page<Signal> findByUserIdAndFilter(int userId, Pageable pageable, String filter);

    Signal findByIdAndUserId(int id, int userId);

    int deleteByIdAndUserId(int id, int userId);

}
