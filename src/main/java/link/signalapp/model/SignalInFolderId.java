package link.signalapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class SignalInFolderId implements Serializable {

    @Column(name = "signal_id")
    private int signalId;

    @Column(name = "folder_id")
    private int folderId;
}
