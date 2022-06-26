package com.example.signalapp.repository;

import com.example.signalapp.model.UserPK;
import com.example.signalapp.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserTokenRepository extends JpaRepository<UserToken, UserPK> {

    @Query(value = "select user_id, token, last_action_time\n" +
            "from user_token\n" +
            "where token = ? and extract(epoch from (? - last_action_time)) < ?",
            nativeQuery = true)
    UserToken findActiveToken(String token, LocalDateTime currentTime, int userIdleTimeout);

    @Modifying
    @Query(value = "delete from user_token where token = ?1",
            nativeQuery = true)
    int deleteByToken(String token);

}
