package link.signalapp.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class UserToken {

    @EmbeddedId
    private UserPK id;

    @Column
    private String token;

    @Column
    private LocalDateTime lastActionTime;

}
