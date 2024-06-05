package link.signalapp.repository.jdbc;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class JdbcFilteringQueryBuilder {

    private final String baseQuery;
    private final Map<String, String> conditions;

    public String build(Map<String, Object> parameters) {
        StringBuilder queryBuilder = new StringBuilder(baseQuery);
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            String condition = conditions.get(key);
            if (condition != null && parameter != null) {
                queryBuilder.append(condition);
            }
        }
        return queryBuilder.toString();
    }
}
