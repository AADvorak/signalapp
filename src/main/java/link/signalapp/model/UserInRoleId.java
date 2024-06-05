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
public class UserInRoleId implements Serializable {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "role_id")
    private int roleId;
}
