package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Column(name = "identificador", nullable = false, unique = true, length = 50)
    private String identificador;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "regex_validacion")
    private String regexValidacion;

    // Lo guardamos como texto para admitir números, booleanos o listas (según el caso)
    @Column(name = "valor", nullable = false, length = 512)
    private String valor;

    //Se puede eliminar pero de deben dar de baja todas sus funciones
    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;
}
