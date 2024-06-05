package link.signalapp.repository;

import link.signalapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Integer>,
        JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByEmail(String email);

    User findByEmailAndEmailConfirmedTrue(String email);

    /**
     * This method doesn't work, select count query is generated incorrectly
     * It is replaced by JDBC implementation
     */
    @Deprecated
    @Query(value = """
            select u.*
            from "user" u
            where true
                and (:search = '' or upper(u.first_name) like upper(:search)
                    or upper(u.last_name) like upper(:search)
                    or upper(u.patronymic) like upper(:search)
                    or upper(u.email) like upper(:search))
                and (0 in :roleIds or exists(
                    select 1
                    from user_in_role uir
                    where uir.user_id = u.id and uir.role_id in :roleIds))
            """, nativeQuery = true)
    Page<User> findByFilter(
            @Param("search") String search,
            @Param("roleIds") List<Integer> roleIds,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
            delete from "user"
            where id = (
                select user_id from user_token
                where token = :token
            )
            """, nativeQuery = true)
    int deleteByToken(@Param("token") String token);

    @Modifying
    @Query(value = """
            update "user"
            set email_confirmed = true
            where id = :userId
            """, nativeQuery = true)
    int updateSetEmailConfirmedTrue(@Param("userId") int userId);

}
