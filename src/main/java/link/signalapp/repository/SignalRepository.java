/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package link.signalapp.repository;

import link.signalapp.model.Signal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author anton
 */
public interface SignalRepository extends PagingAndSortingRepository<Signal, Integer> {

    @Query(value = "select count(1) from signal " +
            "where user_id = :userId", nativeQuery = true)
    int countByUserId(int userId);

    @Query(value = "select distinct sample_rate from signal " +
            "where user_id = :userId", nativeQuery = true)
    List<BigDecimal> sampleRatesByUserId(int userId);

    @Query(value = "select * from signal " +
            "where user_id = :userId " +
            "and (:filter = '' or upper(name) like upper(:filter) " +
            "or upper(description) like upper(:filter)) " +
            "and (0 in :sampleRates or sample_rate in :sampleRates)", nativeQuery = true)
    Page<Signal> findByUserIdAndFilter(
            int userId,
            String filter,
            List<BigDecimal> sampleRates,
            Pageable pageable
    );

    @Query(value = "select s.* from signal s " +
            "join signal_in_folder sif on s.id = sif.signal_id and folder_id in :folderIds " +
            "where user_id = :userId " +
            "and (:filter = '' or upper(name) like upper(:filter) " +
            "or upper(description) like upper(:filter)) " +
            "and (0 in :sampleRates or sample_rate in :sampleRates)", nativeQuery = true)
    Page<Signal> findByUserIdAndFilter(
            int userId,
            String filter,
            List<BigDecimal> sampleRates,
            List<Integer> folderIds,
            Pageable pageable
    );

    Signal findByIdAndUserId(int id, int userId);

    int deleteByIdAndUserId(int id, int userId);

}