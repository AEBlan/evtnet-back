package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Pattern;  // para @Pattern
import java.util.List;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "icono_caracteristica")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IconoCaracteristica extends Base {

    @Column(name = "imagen", nullable = false, length = 512)
    private String imagen;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 icono -> muchas características
    @OneToMany(mappedBy = "iconoCaracteristica")
    private List<Caracteristica> caracteristicas;
}