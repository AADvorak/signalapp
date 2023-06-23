package link.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ResponseWithToken<T> {

    private T response;

    private String token;

}
