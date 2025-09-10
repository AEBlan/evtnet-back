package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Pattern;  // para @Pattern
import java.time.LocalDateTime;


@Data 
@EqualsAndHashCode(callSuper = true)
@Entity 
@Table(name = "imagen_espacio")
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ImagenEspacio extends Base {

    @Column(name = "imagen", nullable = false, length = 512)
    @Pattern(regexp = "(?i).*\\.(png|svg)$",
             message = "La imagen debe ser .png o .svg")
    private String imagen;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @ManyToOne
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;
}