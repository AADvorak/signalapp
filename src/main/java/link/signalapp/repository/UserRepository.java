package link.signalapp.repository;

import link.signalapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findByEmailAndEmailConfirmedTrue(String email);

    @Modifying
    @Query(value = "delete from \"user\" where id = (select user_id from user_token where token = ?1)",
            nativeQuery = true)
    int deleteByToken(String token);

    @Modifying
    @Query(value = "update \"user\" set email_confirmed = true where id = ?1",
            nativeQuery = true)
    int updateSetEmailConfirmedTrue(int userId);

}
