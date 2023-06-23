package link.signalapp.dto.request;

import link.signalapp.validator.Container;
import link.signalapp.validator.MaxLength;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class EditModuleDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    @Container
    private String container;

    private boolean forMenu;

    private boolean transformer;

}
