package link.signalapp.repository.jdbc;

import link.signalapp.model.Role;
import link.signalapp.model.User;
import link.signalapp.repository.FilterUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcFilterUserRepositoryImpl implements FilterUserRepository {

    private static final String USERS_QUERY = """
            select {}
            from "user" u
            where true
                and (:search = '' or upper(u.first_name) like upper(:search)
                    or upper(u.last_name) like upper(:search)
                    or upper(u.patronymic) like upper(:search)
                    or upper(u.email) like upper(:search))
                and (0 in (:roleIds) or exists(
                    select 1
                    from user_in_role uir
                    where uir.user_id = u.id and uir.role_id in (:roleIds)))
            """;

    private static final String ROLES_QUERY = """
            select r.*
            from user_in_role uir
            join role r on r.id = uir.role_id
            where uir.user_id = :userId
            """;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) ->
            new User()
                    .setId(rs.getInt("id"))
                    .setFirstName(rs.getString("first_name"))
                    .setLastName(rs.getString("last_name"))
                    .setPatronymic(rs.getString("patronymic"))
                    .setEmail(rs.getString("email"))
                    .setPassword(rs.getString("password"))
                    .setEmailConfirmed(rs.getBoolean("email_confirmed"))
                    .setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());

    private static final RowMapper<Role> ROLE_ROW_MAPPER = (rs, rowNum) ->
            new Role()
                    .setId(rs.getInt("id"))
                    .setName(rs.getString("name"));

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcFilteringQueryExecutor<User> filteringQueryExecutor;

    public JdbcFilterUserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        filteringQueryExecutor = new JdbcFilteringQueryExecutor<>(
                USERS_QUERY, USER_ROW_MAPPER, jdbcTemplate
        );
    }

    @Override
    public Page<User> findByFilter(String search, List<Integer> roleIds, Pageable pageable) {
        Map<String, Object> params = Map.of(
                "search", search,
                "roleIds", roleIds
        );
        Page<User> userPage = filteringQueryExecutor.execute(params, pageable);
        userPage.stream().forEach(this::fetchRoles);
        return userPage;
    }

    private void fetchRoles(User user) {
        Map<String, Object> params = Map.of("userId", user.getId());
        user.setRoles(new HashSet<>(jdbcTemplate.query(ROLES_QUERY, params, ROLE_ROW_MAPPER)));
    }
}
