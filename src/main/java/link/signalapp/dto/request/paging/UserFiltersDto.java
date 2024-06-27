package link.signalapp.dto.request.paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserFiltersDto implements FiltersDto {

    private String search;

    private List<Integer> roleIds;
}
