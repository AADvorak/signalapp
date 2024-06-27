package link.signalapp.dto.request.paging;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PageDtoRequest<Filters extends FiltersDto> {

    public PageDtoRequest() {
        this.page = 0;
        this.size = 10;
        this.sort = new SortDto();
    }

    @PositiveOrZero
    private int page;

    @Min(5)
    @Max(25)
    private int size;

    private SortDto sort;

    private Filters filters;
}
