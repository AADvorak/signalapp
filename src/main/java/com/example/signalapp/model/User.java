package com.example.signalapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String patronymic;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private boolean emailConfirmed;

    @Column
    private LocalDateTime createTime = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && emailConfirmed == user.emailConfirmed && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(patronymic, user.patronymic) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(createTime, user.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, patronymic, email, password, emailConfirmed, createTime);
    }
}
