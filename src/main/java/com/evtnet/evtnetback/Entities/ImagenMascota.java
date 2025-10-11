package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "imagen_mascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenMascota extends Base {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "imagen")   // URL o ruta pública (png/svg)
    private String imagen;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 imagen -> 0..n secuencias (FK en InstanciaMascotaSecuencia: imagen_mascota_id)
    @OneToMany(mappedBy = "imagenMascota")
    private List<InstanciaMascotaSecuencia> instanciaMascotaSecuencias;
}
