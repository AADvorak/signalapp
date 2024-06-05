/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package link.signalapp.repository;

import link.signalapp.model.Signal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author anton
 */
public interface SignalRepository extends PagingAndSortingRepository<Signal, Integer>,
        JpaRepository<Signal, Integer>, JpaSpecificationExecutor<Signal> {

    @Query(value = """
            select count(1)
            from signal
            where user_id = :userId
            """, nativeQuery = true)
    int countByUserId(@Param("userId") int userId);

    @Query(value = """
            select distinct sample_rate
            from signal
            where user_id = :userId
            """, nativeQuery = true)
    List<BigDecimal> sampleRatesByUserId(@Param("userId") int userId);

    Optional<Signal> findByIdAndUserId(int id, int userId);

    int deleteByIdAndUserId(int id, int userId);

}
