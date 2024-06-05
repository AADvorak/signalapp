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
        return filter.getSearch() == null || filter.getSearch().isEmpty() ? null : "%" + filter.getSearch() + "%";
    }

    private Sort getSort(FilterDto filter) {
        Sort sort = Sort.by(getSortByOrDefault(filter.getSortBy()));
        return "asc".equals(filter.getSortDir()) ? sort : sort.descending();
    }

    private String getSortByOrDefault(String sortBy) {
        return sortBy != null && !sortBy.isEmpty() && availableSortFields.contains(sortBy)
                ? sortBy : defaultSortField;
    }
}
