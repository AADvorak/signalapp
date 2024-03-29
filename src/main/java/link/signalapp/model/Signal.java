/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package link.signalapp.model;

import link.signalapp.dto.request.SignalDtoRequest;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.*;

/**
 *
 * @author anton
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class Signal {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String name;
    
    @Column
    private String description;
    
    @Column
    private LocalDateTime createTime = LocalDateTime.now();

    @Column
    private int userId;

    @Column(name = "max_abs_y")
    private BigDecimal maxAbsY;

    private BigDecimal sampleRate;

    @Column(name = "x_min")
    private BigDecimal xMin;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "signal_in_folder",
            joinColumns = @JoinColumn(name = "signal_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id")
    )
    private Set<Folder> folders;

    public Signal(SignalDtoRequest dtoRequest, int userId) {
        name = dtoRequest.getName();
        description = dtoRequest.getDescription();
        maxAbsY = dtoRequest.getMaxAbsY();
        sampleRate = dtoRequest.getSampleRate();
        xMin = dtoRequest.getXMin();
        this.userId = userId;
    }
    
}
