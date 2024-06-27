package link.signalapp.service.utils;

import link.signalapp.dto.request.paging.FiltersDto;
import link.signalapp.dto.request.paging.PageDtoRequest;
import link.signalapp.dto.request.paging.SortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class PagingUtils {

    private final List<String> availableSortFields;
    private final String defaultSortField;

    public Pageable getPageable(PageDtoRequest<? extends FiltersDto> request, int maxPageSize) {
        return PageRequest.of(request.getPage(), request.getSize() > maxPageSize
                || request.getSize() <= 0
                ? maxPageSize
                : request.getSize(), getSort(request.getSort()));
    }

    private Sort getSort(SortDto sortDto) {
        var sortBy = sortDto == null ? "" : sortDto.getBy();
        var sortDir = sortDto == null ? "" : sortDto.getDir();
        var sort = Sort.by(getSortByOrDefault(sortBy));
        return "asc".equals(sortDir) ? sort : sort.descending();
    }

    private String getSortByOrDefault(String sortBy) {
        return sortBy != null && !sortBy.isEmpty() && availableSortFields.contains(sortBy)
                ? sortBy : defaultSortField;
    }
}
