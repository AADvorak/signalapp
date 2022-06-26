package com.example.signalapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserPK implements Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPK userPK = (UserPK) o;
        return Objects.equals(user, userPK.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
