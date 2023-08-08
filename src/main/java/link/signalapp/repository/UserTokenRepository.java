package link.signalapp.repository;

import link.signalapp.model.UserTokenPK;
import link.signalapp.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserTokenRepository extends JpaRepository<UserToken, UserTokenPK> {

    @Query(value = "select user_id, token, last_action_time " +
            "from user_token " +
            "where token = :token and extract(epoch from (:currentTime - last_action_time)) < :userIdleTimeout",
            nativeQuery = true)
    UserToken findActiveToken(String token, LocalDateTime currentTime, int userIdleTimeout);

    @Modifying
    @Query(value = "delete from user_token where user_id = :userId " +
            "and extract(epoch from (:currentTime - last_action_time)) > :userIdleTimeout",
            nativeQuery = true)
    int deleteOldTokens(int userId, LocalDateTime currentTime, int userIdleTimeout);

    @Modifying
    @Query(value = "delete from user_token where token = :token",
            nativeQuery = true)
    int deleteByToken(String token);

}
