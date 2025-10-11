package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

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

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 -> 0..n secuencias
    @OneToMany(mappedBy = "instanciaMascota", fetch = FetchType.EAGER)
    private List<InstanciaMascotaSecuencia> instanciaMascotaSecuencias;

    // 1 -> 0..n usuario_instancia_mascota
    @OneToMany(mappedBy = "instanciaMascota", fetch = FetchType.EAGER)
    private List<UsuarioInstanciaMascota> usuarioInstanciaMascotas;
}
