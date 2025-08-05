package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ParametroSistema")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametroSistema extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "valor")
    private String valor;
} 