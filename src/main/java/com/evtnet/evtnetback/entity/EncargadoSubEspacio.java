package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, exclude={"subEspacio"})
@Entity
@Table(name = "encargado_subespacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncargadoSubEspacio extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subespacio_id", nullable = false)
    private SubEspacio subEspacio;

    //@OneToMany(mappedBy = "encargadoSubEspacio", fetch = FetchType.LAZY)
    //private List<SubEspacio> subEspacios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

}
