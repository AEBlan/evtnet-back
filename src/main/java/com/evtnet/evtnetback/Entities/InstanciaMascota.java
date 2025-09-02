package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "instancia_mascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanciaMascota extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "page_regex")
    private String page_regex;

    @Column(name = "events")
    private String events;

    @Column(name = "selector")
    private String selector;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fecha_hora_alta;

    // 1 -> 0..n secuencias
    @OneToMany(mappedBy = "instancia_mascota", fetch = FetchType.EAGER)
    private List<InstanciaMascotaSecuencia> instancia_mascota_secuencias;

    // 1 -> 0..n usuario_instancia_mascota
    @OneToMany(mappedBy = "instancia_mascota", fetch = FetchType.EAGER)
    private List<UsuarioInstanciaMascota> usuario_instancia_mascotas;
}
