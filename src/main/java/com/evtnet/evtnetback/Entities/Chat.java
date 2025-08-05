package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Chat")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat extends Base {

    // Relaciones
    @OneToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    
    @OneToMany(mappedBy = "chat")
    private List<Mensaje> mensajes;
} 