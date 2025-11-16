package com.evtnet.evtnetback.entity;
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

    @Column(name = "page_selector")
    private String pageSelector;

    //@Column(name = "events")
    //private String events;

    @ManyToMany
    @JoinTable(
            name = "instancia_evento_mascota",
            joinColumns = @JoinColumn(name = "instancia_mascota_id"),
            inverseJoinColumns = @JoinColumn(name = "evento_mascota_id")
    )
    private List<EventoMascota> eventos;

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
