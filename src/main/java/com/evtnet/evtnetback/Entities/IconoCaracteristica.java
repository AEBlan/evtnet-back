package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Pattern;  // para @Pattern
import java.util.List;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "icono_caracteristica")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IconoCaracteristica extends Base {

    @Column(name = "imagen", nullable = false, length = 512)
    @Pattern(regexp = "(?i).*\\.(png|svg)$",
             message = "La imagen debe tener extensión .png o .svg")
    private String imagen;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    // 1 icono -> muchas características
    @OneToMany(mappedBy = "iconoCaracteristica")
    private List<Caracteristica> caracteristicas;
}