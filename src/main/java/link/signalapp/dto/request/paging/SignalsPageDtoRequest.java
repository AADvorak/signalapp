package link.signalapp.dto.request.paging;

import java.math.BigDecimal;
import java.util.List;

public class SignalsPageDtoRequest extends PageDtoRequest<SignalFiltersDto> {

    // todo this constructor and methods are needed for tests compatibility
    public SignalsPageDtoRequest() {
        super();
        setFilters(new SignalFiltersDto());
    }

    public SignalsPageDtoRequest setPage(int page) {
        super.setPage(page);
        return this;
    }

    public SignalsPageDtoRequest setSize(int size) {
        super.setSize(size);
        return this;
    }

    public SignalsPageDtoRequest setSearch(String search) {
        this.getFilters().setSearch(search);
        return this;
    }

    public SignalsPageDtoRequest setSortBy(String sortBy) {
        this.getSort().setBy(sortBy);
        return this;
    }

    public SignalsPageDtoRequest setSortDir(String sortDir) {
        this.getSort().setDir(sortDir);
        return this;
    }

    public SignalsPageDtoRequest setFolderIds(List<Integer> folderIds) {
        this.getFilters().setFolderIds(folderIds);
        return this;
    }

    public SignalsPageDtoRequest setSampleRates(List<BigDecimal> sampleRates) {
        this.getFilters().setSampleRates(sampleRates);
        return this;
    }
}
