package link.signalapp.model;

import lombok.*;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class UserToken {

    @EmbeddedId
    private UserTokenPK id;

    @Column
    private LocalDateTime lastActionTime;

}
