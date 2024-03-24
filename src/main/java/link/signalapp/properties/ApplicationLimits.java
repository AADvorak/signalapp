package link.signalapp.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ApplicationLimits {

    private int maxSignalLength;

    private int maxUserSignalsNumber;

    private int maxUserFoldersNumber;

}
