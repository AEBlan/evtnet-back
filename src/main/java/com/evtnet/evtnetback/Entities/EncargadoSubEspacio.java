package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
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
