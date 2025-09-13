package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EventoModoEvento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoModoEvento extends Base {

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;
    
    @ManyToOne
    @JoinColumn(name = "modo_evento_id")
    private ModoEvento modoEvento;
} 