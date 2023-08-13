package link.signalapp.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SignalFilterDto {

    private String search;

    private List<Integer> folderIds;

    private List<BigDecimal> sampleRates;

    @Positive
    private int page;

    @Min(5)
    @Max(25)
    private int size;

    private String sortBy;

    private String sortDir;

}
