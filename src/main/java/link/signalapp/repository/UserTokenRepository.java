package link.signalapp.repository;

import link.signalapp.model.UserTokenId;
import link.signalapp.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserTokenRepository extends JpaRepository<UserToken, UserTokenId> {

    @Query(value = """
            select *
            from user_token
            where user_id = :userId
            order by last_action_time desc
            limit 1
            """, nativeQuery = true)
    UserToken findLastByUserId(@Param("userId") int userId);

    @Query(value = """
            select *
            from user_token
            where token = :token and token != ''
                and extract(epoch from (:currentTime - last_action_time)) < :userIdleTimeout
            """, nativeQuery = true)
    UserToken findActiveToken(
            @Param("token") String token,
            @Param("currentTime") LocalDateTime currentTime,
            @Param("userIdleTimeout") int userIdleTimeout
    );

    @Modifying
    @Query(value = """
            delete from user_token
            where user_id = :userId
                and (extract(epoch from (:currentTime - last_action_time)) > :userIdleTimeout or token = '')
            """, nativeQuery = true)
    int deleteOldTokens(
            @Param("userId") int userId,
            @Param("currentTime") LocalDateTime currentTime,
            @Param("userIdleTimeout") int userIdleTimeout
    );

    @Modifying
    @Query(value = """
            update user_token
            set token = ''
            where token = :token
            """, nativeQuery = true)
    int clearByToken(@Param("token") String token);

    @Modifying
    @Query(value = """
            update user_token
            set last_action_time = :lastActionTime
            where token = :token
            """, nativeQuery = true)
    int updateLastActionTimeByToken(
            @Param("lastActionTime") LocalDateTime lastActionTime,
            @Param("token") String token
    );

}
