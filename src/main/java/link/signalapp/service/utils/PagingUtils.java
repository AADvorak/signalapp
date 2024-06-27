package link.signalapp.service.utils;

import link.signalapp.dto.request.PageDtoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class PagingUtils {

    private final List<String> availableSortFields;
    private final String defaultSortField;

    public Pageable getPageable(PageDtoRequest request, int maxPageSize) {
        return PageRequest.of(request.getPage(), request.getSize() > maxPageSize
                || request.getSize() <= 0
                ? maxPageSize
                : request.getSize(), getSort(request));
    }

    public String getSearch(PageDtoRequest request) {
        return request.getSearch() == null || request.getSearch().isEmpty() ? null : "%" + request.getSearch() + "%";
    }

    private Sort getSort(PageDtoRequest request) {
        Sort sort = Sort.by(getSortByOrDefault(request.getSortBy()));
        return "asc".equals(request.getSortDir()) ? sort : sort.descending();
    }

    private String getSortByOrDefault(String sortBy) {
        return sortBy != null && !sortBy.isEmpty() && availableSortFields.contains(sortBy)
                ? sortBy : defaultSortField;
    }
}
