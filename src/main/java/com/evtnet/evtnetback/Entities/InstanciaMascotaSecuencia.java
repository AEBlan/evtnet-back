package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
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
    private String texto;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    // muchas secuencias -> una instancia_mascota
    @ManyToOne(optional = false)
    @JoinColumn(name = "instancia_mascota_id", nullable = false)
    private InstanciaMascota instanciaMascota;

    // muchas secuencias -> una imagen_mascota (reutilizable)
    @ManyToOne(optional = false)
    @JoinColumn(name = "imagen_mascota_id", nullable = false)
    private ImagenMascota imagenMascota;
}
