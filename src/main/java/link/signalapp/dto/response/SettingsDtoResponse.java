package link.signalapp.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SettingsDtoResponse {

    private boolean verifyCaptcha;

    private int maxPageSize;

}
