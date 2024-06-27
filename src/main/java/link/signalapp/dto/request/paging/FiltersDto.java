package link.signalapp.dto.request.paging;

public interface FiltersDto {
    String getSearch();

    default String getSearchFormatted() {
        return getSearch() == null || getSearch().isEmpty() ? null : "%" + getSearch() + "%";
    }
}
