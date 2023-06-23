package link.signalapp.dto.request;

import link.signalapp.service.SignalService;
import link.signalapp.validator.MaxLength;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SignalDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    private String description;

    @Positive
    private BigDecimal maxAbsY;

    @JsonProperty("xMin")
    @NotNull
    private BigDecimal xMin;

    @Positive
    private BigDecimal sampleRate;

    @NotEmpty
    @Size(max = SignalService.MAX_SIGNAL_LENGTH)
    private List<BigDecimal> data;

}
