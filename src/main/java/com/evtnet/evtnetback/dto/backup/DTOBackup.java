package com.evtnet.evtnetback.dto.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOBackup {
    private Integer id;
    private String ruta;
    private Double tamano;
    private Long fechaHora;
    private String tipo;
    private String programacion;
    private Boolean pendiente;
    private Integer dependeDe;
}
