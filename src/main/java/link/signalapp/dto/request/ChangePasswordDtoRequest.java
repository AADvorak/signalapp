package link.signalapp.dto.request;

import link.signalapp.service.UserService;
import link.signalapp.validator.MinLength;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ChangePasswordDtoRequest {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    @Size(max = UserService.MAX_PASSWORD_LENGTH)
    @MinLength
    private String password;

}
