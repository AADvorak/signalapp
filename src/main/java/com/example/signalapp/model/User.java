package com.example.signalapp.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
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

}
