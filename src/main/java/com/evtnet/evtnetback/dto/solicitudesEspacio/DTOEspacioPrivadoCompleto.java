package com.evtnet.evtnetback.dto.solicitudesEspacio;

import com.evtnet.evtnetback.dto.espacios.DTOArchivo;
import com.evtnet.evtnetback.dto.espacios.DTOEstadoEspacio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOEspacioPrivadoCompleto {
    private Long idEspacio;
    private String nombreEspacio;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private Long fechaIngreso;
    private Solicitante solicitante;
    private List<EspacioEstado> espacioEstados;
    private List<DTOEstadoEspacio> estadosPosibles;
    private List<DTOArchivo> documentacion;


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
    public static class EspacioEstado{
        private Long id;
        private String nombre;
        private String descripcion;
        private Long fechaHoraDesde;
    }
}
