/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package link.signalapp.repository;

import link.signalapp.model.Signal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author anton
 */
public interface SignalRepository extends PagingAndSortingRepository<Signal, Integer>, JpaRepository<Signal, Integer> {

    @Query(value = "select count(1) from signal " +
            "where user_id = :userId", nativeQuery = true)
    int countByUserId(int userId);

    @Query(value = "select distinct sample_rate from signal " +
            "where user_id = :userId", nativeQuery = true)
    List<BigDecimal> sampleRatesByUserId(int userId);

    @Query(value = "select s.* from signal s " +
            "where user_id = :userId " +
            "and (:filter = '' or upper(name) like upper(:filter) " +
            "or upper(description) like upper(:filter)) " +
            "and (0 in :sampleRates or sample_rate in :sampleRates) " +
            "and (0 in :folderIds or exists(select 1 from signal_in_folder sif " +
            "where sif.signal_id = s.id and sif.folder_id in :folderIds))", nativeQuery = true)
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
