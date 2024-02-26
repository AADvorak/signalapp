package link.signalapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import link.signalapp.validator.MaxLength;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SignalDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    private String description;

    @NotNull
    @Positive
    private BigDecimal maxAbsY;

    @JsonProperty("xMin")
    @NotNull
    private BigDecimal xMin;

    @NotNull
    @Positive
    private BigDecimal sampleRate;

}
