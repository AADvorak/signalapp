package link.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserDtoResponse {

    private int id;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String email;

    private boolean emailConfirmed;

}
