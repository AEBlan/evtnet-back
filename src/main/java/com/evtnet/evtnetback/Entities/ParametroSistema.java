package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "parametro_sistema")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametroSistema extends Base {

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    // Lo guardamos como texto para admitir números, booleanos o listas (según el caso)
    @Column(name = "valor", nullable = false, length = 512)
    private String valor;
}
