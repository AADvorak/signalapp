/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.repository;

import com.example.signalapp.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author anton
 */
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    int deleteByIdAndUserId(int id, int userId);

    Optional<Module> findByIdAndUserId(int id, int userId);

    List<Module> findByUserIdIsNull();

    List<Module> findByUserIdIsNullOrUserIdEquals(int userId);

}
