package com.evtnet.evtnetback.dto.solicitudesEspacio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOSolicitudCompleta {
    private Long idSEP;
    private String nombreEspacio;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private String justificacion;
    private Long fechaIngreso;
    private Solicitante solicitante;
    private List<SEPEstado>sepEstados;
    private Long idEspacio;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Solicitante{
        private String nombre;
        private String apellido;
        private String username;
        private String email;
        private String urlFotoPerfil;
        private String contentType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SEPEstado{
        private Long id;
        private String nombre;
        private String descripcion;
        private Long fechaHoraDesde;
        private Solicitante responsable;
        private Long idEstado;
    }
}
