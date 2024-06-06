package link.signalapp.repository.jdbc;

import link.signalapp.model.Role;
import link.signalapp.model.User;
import link.signalapp.repository.FilterUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcFilterUserRepositoryImpl implements FilterUserRepository {

    private static final String USERS_QUERY = """
            select {}
            from "user" u
            where true
            """;
    private static final String USERS_QUERY_SEARCH_CONDITION = """
                and (upper(u.first_name) like upper(:search)
                    or upper(u.last_name) like upper(:search)
                    or upper(u.patronymic) like upper(:search)
                    or upper(u.email) like upper(:search))
            """;
    private static final String USERS_QUERY_ROLE_IDS_CONDITION = """
                and exists(
                    select 1
                    from user_in_role uir
                    where uir.user_id = u.id and uir.role_id in (:roleIds))
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
    private final JdbcFilteringQueryExecutor<User> queryExecutor;

    public JdbcFilterUserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        queryExecutor = new JdbcFilteringQueryExecutor<>(USERS_QUERY, Map.of(
                "search", USERS_QUERY_SEARCH_CONDITION,
                "roleIds", USERS_QUERY_ROLE_IDS_CONDITION
        ), USER_ROW_MAPPER, jdbcTemplate);
    }

    @Override
    public Page<User> findByFilter(String search, List<Integer> roleIds, Pageable pageable) {
        Map<String, Object> params = makeParams(search, roleIds);
        Page<User> userPage = queryExecutor.execute(params, pageable);
        userPage.stream().forEach(this::fetchRoles);
        return userPage;
    }

    private void fetchRoles(User user) {
        Map<String, Object> params = Map.of("userId", user.getId());
        user.setRoles(new HashSet<>(jdbcTemplate.query(ROLES_QUERY, params, ROLE_ROW_MAPPER)));
    }

    private Map<String, Object> makeParams(String search, List<Integer> roleIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("search", search);
        params.put("roleIds", roleIds);
        return params;
    }
}
