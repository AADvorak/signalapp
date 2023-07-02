package link.signalapp.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class RestorePasswordDtoRequest {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String localeTitle;

    @NotEmpty
    private String localeMsg;

}
