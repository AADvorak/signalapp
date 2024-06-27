package link.signalapp.repository.jdbc;

import link.signalapp.dto.request.paging.SignalFiltersDto;
import link.signalapp.model.Signal;
import link.signalapp.repository.FilterSignalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

//@Repository - currently specifications repository is used
public class JdbcFilterSignalRepositoryImpl implements FilterSignalRepository {

    private static final String QUERY_BASE = """
            select {}
            from signal s
            where s.user_id = :userId
            """;
    private static final String QUERY_SEARCH_CONDITION = """
                and (upper(s.name) like upper(:search)
                    or upper(s.description) like upper(:search))
            """;
    private static final String QUERY_SAMPLE_RATES_CONDITION = """
                and s.sample_rate in (:sampleRates)
            """;
    public static final String QUERY_FOLDER_IDS_CONDITION = """
                and exists(
                    select 1
                    from signal_in_folder sif
                    where sif.signal_id = s.id and sif.folder_id in (:folderIds))
            """;

    private static final RowMapper<Signal> ROW_MAPPER = (rs, rowNum) ->
            new Signal()
                    .setId(rs.getInt("id"))
                    .setName(rs.getString("name"))
                    .setDescription(rs.getString("description"))
                    .setCreateTime(rs.getTimestamp("create_time").toLocalDateTime())
                    .setUserId(rs.getInt("user_id"))
                    .setMaxAbsY(rs.getBigDecimal("max_abs_y"))
                    .setSampleRate(rs.getBigDecimal("sample_rate"))
                    .setXMin(rs.getBigDecimal("x_min"));

    private final JdbcPageQueryExecutor<Signal> queryExecutor;

    public JdbcFilterSignalRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        queryExecutor = new JdbcPageQueryExecutor<>(QUERY_BASE, Map.of(
                "search", QUERY_SEARCH_CONDITION,
                "sampleRates", QUERY_SAMPLE_RATES_CONDITION,
                "folderIds", QUERY_FOLDER_IDS_CONDITION
        ), ROW_MAPPER, jdbcTemplate);
    }

    @Override
    public Page<Signal> findByUserIdAndFilters(int userId, SignalFiltersDto filters, Pageable pageable) {
        Map<String, Object> params = makeParams(userId, filters);
        return queryExecutor.execute(params, pageable);
    }

    Map<String, Object> makeParams(int userId, SignalFiltersDto filters) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("search", filters.getSearchFormatted());
        params.put("sampleRates", filters.getSampleRates());
        params.put("folderIds", filters.getFolderIds());
        return params;
    }
}
