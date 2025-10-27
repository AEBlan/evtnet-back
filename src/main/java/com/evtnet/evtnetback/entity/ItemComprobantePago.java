package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "item_comprobante_pago")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComprobantePago extends Base {

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "monto_unitario", nullable = false)
    private BigDecimal montoUnitario;

    @Column(name = "cantidad")
    private int cantidad;   

    // --- Relaciones ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobro_id", nullable = false)
    private Usuario cobro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false)
    private Usuario pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_pago_id", nullable = false)
    private ComprobantePago comprobantePago;

}
