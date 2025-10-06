package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_registro")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoRegistro extends Base {

    @Column(name = "nombre")
    private String nombre;

    @ManyToMany(mappedBy = "tipos")
    private List<Registro> registros;
}
