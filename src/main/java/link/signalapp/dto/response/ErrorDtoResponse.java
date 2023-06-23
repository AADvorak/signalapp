package link.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ErrorDtoResponse {

    private String code;

    private String message;

    private Object params;

}
