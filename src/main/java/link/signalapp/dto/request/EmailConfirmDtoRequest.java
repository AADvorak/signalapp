package link.signalapp.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class EmailConfirmDtoRequest {

    @NotEmpty
    private String origin;

    @NotEmpty
    private String localeTitle;

    @NotEmpty
    private String localeMsg;

}
