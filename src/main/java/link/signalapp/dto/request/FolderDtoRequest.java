package link.signalapp.dto.request;

import link.signalapp.validator.MaxLength;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class FolderDtoRequest {

    @NotEmpty
    @MaxLength
    private String name;

    private String description;

}
