package link.signalapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SignalDtoResponse {

    private int id;

    private String name;

    private String description;

    private BigDecimal maxAbsY;

    @JsonProperty("xMin")
    private BigDecimal xMin;

    private BigDecimal sampleRate;

}
