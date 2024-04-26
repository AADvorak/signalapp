package link.signalapp.service.utils;

import link.signalapp.dto.request.FilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class FilterUtils {

    private final List<String> availableSortFields;
    private final String defaultSortField;

    public Pageable getPageable(FilterDto filter, int maxPageSize) {
        return PageRequest.of(filter.getPage(), filter.getSize() > maxPageSize
                || filter.getSize() <= 0
                ? maxPageSize
                : filter.getSize(), getSort(filter));
    }

    public String getSearch(FilterDto filter) {
        return filter.getSearch() == null || filter.getSearch().isEmpty() ? "" : "%" + filter.getSearch() + "%";
    }

    public <T> List<T> listWithDefaultValue(List<T> list, T defaultValue) {
        return list == null || list.isEmpty() ? List.of(defaultValue) : list;
    }

    private Sort getSort(FilterDto filter) {
        Sort sort = Sort.by(camelToSnake(getSortByOrDefault(filter.getSortBy())));
        return "asc".equals(filter.getSortDir()) ? sort : sort.descending();
    }

    private String getSortByOrDefault(String sortBy) {
        return sortBy != null && !sortBy.isEmpty() && availableSortFields.contains(sortBy)
                ? sortBy : defaultSortField;
    }

    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
