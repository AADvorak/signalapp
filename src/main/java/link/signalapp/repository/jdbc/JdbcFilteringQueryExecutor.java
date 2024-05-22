package link.signalapp.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcFilteringQueryExecutor<T> {

    private final String queryTemplate;
    private final RowMapper<T> rowMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Page<T> execute(Map<String, Object> parameters, Pageable pageable) {
        List<T> result = jdbcTemplate.query(makeResultQuery(pageable), parameters, rowMapper);
        if (result.isEmpty()) {
            return Page.empty(pageable);
        }
        if (pageable.getPageNumber() == 0 && pageable.getPageSize() > result.size()) {
            return new PageImpl<>(result);
        }
        Long total = jdbcTemplate.queryForObject(makeCountQuery(), parameters, Long.class);
        return new PageImpl<>(result, pageable, total != null ? total : 0L);
    }

    private String makeResultQuery(Pageable pageable) {
        return queryTemplate.replace("{}", "*")
                + makeOrderByQuery(pageable) + makeLimitQuery(pageable);
    }

    private String makeCountQuery() {
        return queryTemplate.replace("{}", "count(1)");
    }

    private String makeOrderByQuery(Pageable pageable) {
        var order = pageable.getSort().stream().findFirst().orElseThrow();
        return " order by " + order.getProperty() + " " + order.getDirection();
    }

    private String makeLimitQuery(Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        String limit = " fetch first " + size + " rows only";
        if (page > 0) {
            return limit + " offset " + page;
        }
        return limit;
    }
}
