package link.signalapp.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;

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

    private LocalDateTime createTime;

    private LocalDateTime lastActionTime;

    private Set<RoleDtoResponse> roles;

    private int storedSignalsNumber;

}
