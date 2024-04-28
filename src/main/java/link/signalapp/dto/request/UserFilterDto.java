package link.signalapp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserFilterDto implements FilterDto {

    private String search;

    @PositiveOrZero
    private int page;

    @Min(5)
    @Max(25)
    private int size;

    private String sortBy;

    private String sortDir;

    private List<Integer> roleIds;

}
