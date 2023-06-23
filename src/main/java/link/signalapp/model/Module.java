/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package link.signalapp.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 *
 * @author anton
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class Module {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String module;

    @Column
    private String name;
    
    @Column
    private String container;
    
    @Column
    private boolean forMenu;
    
    @Column
    private boolean transformer;

}
