package com.evtnet.evtnetback.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "instancia_mascota_secuencia")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanciaMascotaSecuencia extends Base {

    @Column(name = "texto")
    @Size(max=2047, message = "{validation.name.size.too_long}")
    private String texto;

    @Column(name = "orden")
    private Integer orden;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // muchas secuencias -> una instancia_mascota
    @ManyToOne(optional = false)
    @JoinColumn(name = "instancia_mascota_id", nullable = false)
    private InstanciaMascota instanciaMascota;

    // muchas secuencias -> una imagen_mascota (reutilizable)
    @ManyToOne(optional = false)
    @JoinColumn(name = "imagen_mascota_id", nullable = false)
    private ImagenMascota imagenMascota;
}
