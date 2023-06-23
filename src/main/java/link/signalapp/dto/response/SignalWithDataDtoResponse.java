package link.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SignalWithDataDtoResponse extends SignalDtoResponse {

    private List<BigDecimal> data;

}
