package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.dto.cronogramas.*;
import com.evtnet.evtnetback.dto.espacios.DTOVerificacionVigencia;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfiguracionHorarioEspacioServiceImpl extends BaseServiceImpl <ConfiguracionHorarioEspacio, Long> implements ConfiguracionHorarioEspacioService {

    private final ConfiguracionHorarioEspacioRepository configuracionHorarioEspacioRepository;
    private final EspacioRepository espacioRepository;
    private final HorarioEspacioRepository horarioEspacioRepository;
    private final ExcepcionHorarioEspacioRepository excepcionHorarioEspacioRepository;
    private final EventoRepository eventoRepository;
    private final SubEspacioRepository subEspacioRepository;
    private final ComisionPorOrganizacionRepository comisionPorOrganizacionRepository;
    private final ComisionPorInscripcionRepository comisionPorInscripcionRepository;
    private final TipoExcepcionHorarioEspacioRepository tipoExcepcionHorarioEspacioRepository;

    public ConfiguracionHorarioEspacioServiceImpl(
            ConfiguracionHorarioEspacioRepository configuracionHorarioEspacioRepository,
            EspacioRepository espacioRepository,
            HorarioEspacioRepository horarioEspacioRepository,
            ExcepcionHorarioEspacioRepository excepcionHorarioEspacioRepository,
            EventoRepository eventoRepository,
            SubEspacioRepository subEspacioRepository,
            ComisionPorOrganizacionRepository comisionPorOrganizacionRepository,
            ComisionPorInscripcionRepository comisionPorInscripcionRepository,
            TipoExcepcionHorarioEspacioRepository tipoExcepcionHorarioEspacioRepository
    ) {
        super(configuracionHorarioEspacioRepository);
        this.configuracionHorarioEspacioRepository=configuracionHorarioEspacioRepository;
        this.espacioRepository=espacioRepository;
        this.horarioEspacioRepository=horarioEspacioRepository;
        this.excepcionHorarioEspacioRepository=excepcionHorarioEspacioRepository;
        this.eventoRepository=eventoRepository;
        this.subEspacioRepository=subEspacioRepository;
        this.comisionPorOrganizacionRepository=comisionPorOrganizacionRepository;
        this.comisionPorInscripcionRepository=comisionPorInscripcionRepository;
        this.tipoExcepcionHorarioEspacioRepository=tipoExcepcionHorarioEspacioRepository;
    }

    private Map<Integer, String> diasSemana = Map.ofEntries(
            Map.entry(0, "Lunes"),
            Map.entry(1, "Martes"),
            Map.entry(2, "Miércoles"),
            Map.entry(3, "Jueves"),
            Map.entry(4, "Viernes"),
            Map.entry(5, "Sábado"),
            Map.entry(6, "Domingo")
    );
    private Map<String, Integer> diasSemanaInvertido = diasSemana.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));


    @Override
    public DTOCronogramasEspacio obtenerCronogramasEspacio(Long idEspacio)throws Exception{
        Espacio espacio = espacioRepository.findById(idEspacio).get();
        List<ConfiguracionHorarioEspacio> cronogramasEspacio =configuracionHorarioEspacioRepository.findAllByEspacio(idEspacio);
        DTOCronogramasEspacio cronogramaEspacio = DTOCronogramasEspacio.builder()
                .nombre(espacio.getNombre())
                .build();
        List<DTOCronogramasEspacio.DTOCronograma> cronogramas=new ArrayList<>();
        for(ConfiguracionHorarioEspacio cronograma : cronogramasEspacio){
            cronogramas.add(DTOCronogramasEspacio.DTOCronograma.builder()
                            .id(cronograma.getId())
                            .diasHaciaAdelante(cronograma.getDiasAntelacion())
                            .fechaDesde(cronograma.getFechaDesde() == null ?
                                    null : cronograma.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .fechaHasta(cronograma.getFechaHasta() == null ?
                                    null : cronograma.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
        }
        cronogramaEspacio.setCronogramas(cronogramas);
        return cronogramaEspacio;
    }

    @Override
    public DTOCronogramaEspacio obtenerCronogramaEspacio(Long idCronograma)throws Exception{
        ConfiguracionHorarioEspacio cronogramaEspacio =configuracionHorarioEspacioRepository.findById(idCronograma).get();
        DTOCronogramaEspacio dtoCronogramaEspacio = DTOCronogramaEspacio.builder()
                .nombreEspacio(cronogramaEspacio.getSubEspacio().getNombre())
                .idCronograma(cronogramaEspacio.getId())
                .diasHaciaAdelante(cronogramaEspacio.getDiasAntelacion())
                .fechaDesde(cronogramaEspacio.getFechaDesde() == null ?
                        null : cronogramaEspacio.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(cronogramaEspacio.getFechaHasta() == null ?
                        null : cronogramaEspacio.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();

        return dtoCronogramaEspacio;
    }

    @Override
    public DTODetalleCronograma obtenerDetalleCronograma(Long idCronograma)throws Exception{
        ConfiguracionHorarioEspacio cronograma=configuracionHorarioEspacioRepository.findById(idCronograma).get();
        DTODetalleCronograma dtoDetalleCronograma=DTODetalleCronograma.builder()
                .id(cronograma.getId())
                .nombreSubEspacio(cronograma.getSubEspacio().getNombre())
                .fechaDesde(cronograma.getFechaDesde() == null ?
                        null : cronograma.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(cronograma.getFechaHasta() == null ?
                        null : cronograma.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
        List<DTODetalleCronograma.Horario>horarios=new ArrayList<>();
        for (HorarioEspacio horarioEspacio : cronograma.getHorariosEspacio()){

            horarios.add(DTODetalleCronograma.Horario.builder()
                            .id(horarioEspacio.getId())
                            .diaSemana(diasSemanaInvertido.getOrDefault( horarioEspacio.getDiaSemana(), 0 ))
                            .horaDesde(horarioEspacio.getHoraDesde()
                                    .atDate(LocalDate.now()) // combinás la hora con una fecha (necesaria para crear un Instant)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli())
                            .horaHasta(horarioEspacio.getHoraHasta()
                                    .atDate(LocalDate.now())
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli())
                            .precioOrganizacion(horarioEspacio.getPrecioOrganizacion().doubleValue())
                            .adicionalPorInscripcion(horarioEspacio.getAdicionalPorInscripcion().doubleValue())
                    .build());
        }
        dtoDetalleCronograma.setHorarios(horarios);
        return dtoDetalleCronograma;
    }

    @Override
    public void eliminarHorario(Long idHorario)throws Exception{
        if(!this.eventoRepository.existenEventosByHorario(idHorario)){
            this.horarioEspacioRepository.deleteById(idHorario);
        }else{
            throw new Exception("No se puede eliminar el horario, ya hay eventos programados");
        }
    }

    @Override
    public DTOExcepcionesCronograma obtenerExcepcionesCronograma(Long idCronograma)throws Exception{
        ConfiguracionHorarioEspacio cronograma = this.configuracionHorarioEspacioRepository.findById(idCronograma).get();
        DTOExcepcionesCronograma dtoExcepcionesCronograma=DTOExcepcionesCronograma.builder()
                .id(cronograma.getId())
                .nombreSubEspacio(cronograma.getSubEspacio().getNombre())
                .fechaDesde(cronograma.getFechaDesde() == null ?
                        null : cronograma.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(cronograma.getFechaHasta() == null ?
                        null : cronograma.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
        List<DTOExcepcionesCronograma.Excepcion>excepciones=new ArrayList<>();
        for (ExcepcionHorarioEspacio excepcionHorarioEspacio:cronograma.getExcepcionesHorarioEspacio()){

            excepciones.add(DTOExcepcionesCronograma.Excepcion.builder()
                    .id(excepcionHorarioEspacio.getId())
                    .fechaHoraDesde(excepcionHorarioEspacio.getFechaHoraDesde() == null ?
                            null : excepcionHorarioEspacio.getFechaHoraDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .fechaHoraHasta(excepcionHorarioEspacio.getFechaHoraHasta() == null ?
                            null : excepcionHorarioEspacio.getFechaHoraHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .tipo(excepcionHorarioEspacio.getTipoExcepcionHorarioEspacio().getNombre())
                    .hayEventosProgramados(this.eventoRepository.existenEventosByExcepcion(excepcionHorarioEspacio.getId()))
                    .build());
        }
        dtoExcepcionesCronograma.setExcepciones(excepciones);
        return dtoExcepcionesCronograma;
    }

    @Override
    public void eliminarExcepcionCronograma(Long idExcepcion)throws Exception{
        this.excepcionHorarioEspacioRepository.deleteById(idExcepcion);
    }

    @Override
    public DTOVerificacionVigencia verificarVigencia(Long idSubEspacio, Long idCronograma, Long fechaDesde, Long fechaHasta)throws Exception{
        LocalDateTime fechaDesdeFiltro = Instant.ofEpochMilli(fechaDesde)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime fechaHastaFiltro = Instant.ofEpochMilli(fechaHasta)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        List<ConfiguracionHorarioEspacio> configuracionHorarioEspacio = this.configuracionHorarioEspacioRepository.findSuperpuestos(idSubEspacio, idCronograma, fechaDesdeFiltro, fechaHastaFiltro);
        List<Evento> eventos=this.eventoRepository.findByFechas(idSubEspacio, fechaDesdeFiltro, fechaHastaFiltro);
        List<DTOCronogramasEspacio.DTOCronograma>cronogramasSuperpuestos=new ArrayList<>();
        List<DTOVerificacionVigencia.DTOEvento>eventosProblematicos=new ArrayList<>();
        for(ConfiguracionHorarioEspacio cronograma:configuracionHorarioEspacio){
            cronogramasSuperpuestos.add(DTOCronogramasEspacio.DTOCronograma.builder()
                    .id(cronograma.getId())
                    .fechaDesde(cronograma.getFechaDesde() == null ?
                            null : cronograma.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .fechaHasta(cronograma.getFechaHasta() == null ?
                            null : cronograma.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
        }
        for(Evento evento:eventos){
            eventosProblematicos.add(DTOVerificacionVigencia.DTOEvento.builder()
                    .id(evento.getId())
                    .nombre(evento.getNombre())
                    .fechaHoraInicio(evento.getFechaHoraInicio() == null ?
                            null :evento.getFechaHoraInicio().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
        }
        return DTOVerificacionVigencia.builder()
                .cronogramasSuperpuestos(cronogramasSuperpuestos)
                .eventosProblematicos(eventosProblematicos)
                .build();
    }

    @Override
    public Long crearCronograma(DTOCrearCronograma dtoCrearCronograma)throws Exception{
        LocalDateTime fechaDesdeFiltro = Instant.ofEpochMilli(dtoCrearCronograma.getFechaDesde())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime fechaHastaFiltro = Instant.ofEpochMilli(dtoCrearCronograma.getFechaHasta())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        List<ConfiguracionHorarioEspacio> configuracionHorarioEspacio = this.configuracionHorarioEspacioRepository.findSuperpuestos(dtoCrearCronograma.getIdSubEspacio(), null,fechaDesdeFiltro, fechaHastaFiltro);
        if(configuracionHorarioEspacio==null || configuracionHorarioEspacio.size()==0){
            SubEspacio subEspacio=this.subEspacioRepository.findById(dtoCrearCronograma.getIdSubEspacio()).get();
            ConfiguracionHorarioEspacio configuracionHorarioEspacioNuevo=ConfiguracionHorarioEspacio.builder()
                    .fechaDesde(Instant.ofEpochMilli(dtoCrearCronograma.getFechaDesde())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .fechaHasta(Instant.ofEpochMilli(dtoCrearCronograma.getFechaHasta())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .diasAntelacion(dtoCrearCronograma.getDiasHaciaAdelante())
                    .subEspacio(subEspacio)
                    .build();
            configuracionHorarioEspacioNuevo=save(configuracionHorarioEspacioNuevo);
            return configuracionHorarioEspacioNuevo.getId();
        }else{
            throw new Exception("Se superpone el cronograma con otro");
        }
    }

    @Override
    public void modificarCronograma(DTOCronogramaEspacio dtoCronograma)throws Exception{
        // Convertir fechas
        LocalDateTime nuevaFechaDesde = Instant.ofEpochMilli(dtoCronograma.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime nuevaFechaHasta = Instant.ofEpochMilli(dtoCronograma.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Obtener cronograma actual
        ConfiguracionHorarioEspacio cronogramaActual = configuracionHorarioEspacioRepository
                .findById(dtoCronograma.getIdCronograma())
                .orElseThrow(() -> new Exception("No se encontró el cronograma a modificar"));

        LocalDateTime fechaDesdeAnterior = cronogramaActual.getFechaDesde();
        LocalDateTime fechaHastaAnterior = cronogramaActual.getFechaHasta();

        // Verificar si se achicó el rango (por izquierda o por derecha)
        boolean achicaPorIzquierda = nuevaFechaDesde.isAfter(fechaDesdeAnterior);
        boolean achicaPorDerecha = nuevaFechaHasta.isBefore(fechaHastaAnterior);

        // Si se achicó, buscar eventos afectados
        if (achicaPorIzquierda || achicaPorDerecha) {
            LocalDateTime rangoInicioEliminado = achicaPorIzquierda ? fechaDesdeAnterior : null;
            LocalDateTime rangoFinEliminado = achicaPorIzquierda ? nuevaFechaDesde : null;

            LocalDateTime rangoInicioEliminado2 = achicaPorDerecha ? nuevaFechaHasta : null;
            LocalDateTime rangoFinEliminado2 = achicaPorDerecha ? fechaHastaAnterior : null;

            // Consultar eventos que queden fuera del nuevo rango
            List<Evento> eventosAfectados = eventoRepository.findEventosFueraDeRango(
                    dtoCronograma.getIdSubEspacio(),
                    rangoInicioEliminado, rangoFinEliminado,
                    rangoInicioEliminado2, rangoFinEliminado2
            );

            if (!eventosAfectados.isEmpty()) {
                throw new Exception("Existen eventos que ya no estarían dentro del cronograma");
            }
        }

        // Verificar superposición con otros cronogramas
        List<ConfiguracionHorarioEspacio> superpuestos = configuracionHorarioEspacioRepository
                .findSuperpuestos(dtoCronograma.getIdSubEspacio(), dtoCronograma.getIdCronograma(), nuevaFechaDesde, nuevaFechaHasta);

        if (!superpuestos.isEmpty()) {
            throw new Exception("Se superpone el cronograma con otro existente");
        }

        // Actualizar el cronograma
        cronogramaActual.setFechaDesde(nuevaFechaDesde);
        cronogramaActual.setFechaHasta(nuevaFechaHasta);
        cronogramaActual.setDiasAntelacion(dtoCronograma.getDiasHaciaAdelante());
        save(cronogramaActual);
    }

    @Override
    public DTODatosCreacionHorario obtenerDatosCreacionHorario(Long idCronograma)throws Exception{
        ConfiguracionHorarioEspacio cronograma=configuracionHorarioEspacioRepository.findById(idCronograma).get();
        DTODatosCreacionHorario dtoDatosCreacionHorario=DTODatosCreacionHorario.builder()
                .nombreSubEspacio(cronograma.getSubEspacio().getNombre())
                .fechaDesde(cronograma.getFechaDesde() == null ?
                        null : cronograma.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(cronograma.getFechaHasta() == null ?
                        null : cronograma.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
        //TODO ver si se agrega comisión y cuál se toma
        return dtoDatosCreacionHorario;
    }

    @Override
    public void crearHorario(DTOCrearHorario dtoCrearHorario)throws Exception{
        LocalTime horaDesde = Instant.ofEpochMilli(dtoCrearHorario.getHoraDesde())
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        LocalTime horaHasta = Instant.ofEpochMilli(dtoCrearHorario.getHoraHasta())
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
        if(!this.horarioEspacioRepository.existeSuperpuesto(horaDesde, horaHasta, dtoCrearHorario.getIdCronograma(), diasSemana.getOrDefault( dtoCrearHorario.getDiaSemana(), "" ))){
            ConfiguracionHorarioEspacio cronograma=this.configuracionHorarioEspacioRepository.findById(dtoCrearHorario.getIdCronograma()).get();
            HorarioEspacio horarioEspacio=HorarioEspacio.builder()
                    .diaSemana(diasSemana.getOrDefault( dtoCrearHorario.getDiaSemana(), "" ))
                    .horaDesde(horaDesde)
                    .horaHasta(horaHasta)
                    .precioOrganizacion(new BigDecimal(dtoCrearHorario.getPrecioOrganizacion()))
                    .adicionalPorInscripcion(new BigDecimal(dtoCrearHorario.getAdicionalPorInscripcion()))
                    .configuracionHorarioEspacio(cronograma)
                    .build();
            this.horarioEspacioRepository.save(horarioEspacio);
        }else{
            throw new Exception("Se superpone el horario con otro");
        }
    }

    @Override
    public DTODatosCreacionExcepcion obtenerDatosCreacionExcepcion(Long idCronograma)throws Exception{
        ConfiguracionHorarioEspacio cronograma=configuracionHorarioEspacioRepository.findById(idCronograma).get();
        List<TipoExcepcionHorarioEspacio> tiposExcepcion=this.tipoExcepcionHorarioEspacioRepository.findAll();
        DTODatosCreacionExcepcion dtoDatosCreacionExcepcion=DTODatosCreacionExcepcion.builder()
                .nombreSubEspacio(cronograma.getSubEspacio().getNombre())
                .fechaDesde(cronograma.getFechaDesde() == null ?
                        null : cronograma.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(cronograma.getFechaHasta() == null ?
                        null : cronograma.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
        List<DTODatosCreacionExcepcion.TipoExcepcion> tiposExcepciones=new ArrayList<>();
        for(TipoExcepcionHorarioEspacio tipoExcepcion:tiposExcepcion){
            tiposExcepciones.add(DTODatosCreacionExcepcion.TipoExcepcion.builder()
                            .id(tipoExcepcion.getId())
                            .nombre(tipoExcepcion.getNombre())
                    .build());
        }
        dtoDatosCreacionExcepcion.setTiposExcepcion(tiposExcepciones);
        return dtoDatosCreacionExcepcion;
    }

    @Override
    public void crearExcepcion(DTOCrearExcepcion dtoCrearExcepcion)throws Exception{
        LocalDateTime fechaDesde = Instant.ofEpochMilli(dtoCrearExcepcion.getFechaDesde())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime fechaHasta = Instant.ofEpochMilli(dtoCrearExcepcion.getFechaHasta())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ConfiguracionHorarioEspacio cronograma=this.configuracionHorarioEspacioRepository.findById(dtoCrearExcepcion.getIdCronograma()).get();
        TipoExcepcionHorarioEspacio tipoExcepcion=this.tipoExcepcionHorarioEspacioRepository.findById(dtoCrearExcepcion.getIdTipoExcepcion()).get();

        if(!this.excepcionHorarioEspacioRepository.existeSuperpuesto(fechaDesde, fechaHasta, dtoCrearExcepcion.getIdCronograma()) && this.eventoRepository.findByFechas(cronograma.getSubEspacio().getId(), fechaDesde, fechaHasta).isEmpty()){
            ExcepcionHorarioEspacio excepcion=ExcepcionHorarioEspacio.builder()
                    .fechaHoraDesde(fechaDesde)
                    .fechaHoraHasta(fechaHasta)
                    .configuracionHorarioEspacio(cronograma)
                    .tipoExcepcionHorarioEspacio(tipoExcepcion)
                    .build();
            this.excepcionHorarioEspacioRepository.save(excepcion);
        }else if(this.excepcionHorarioEspacioRepository.existeSuperpuesto(fechaDesde, fechaHasta, dtoCrearExcepcion.getIdCronograma())){
            throw new Exception("Se superpone la excepción con otro");
        }else{
            throw new Exception("Existen eventos programados dentro de estas fechas");
        }
    }

    @Override
    public List<DTOHorarioDisponible>buscarHorariosDisponibles(Long idSubEspacio, Long dia)throws Exception{
        LocalDate fechaEvento = Instant.ofEpochMilli(dia)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        DayOfWeek dayOfWeek = fechaEvento.getDayOfWeek();
        String diaSemana = switch (dayOfWeek) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        List<HorarioEspacio>horariosDisponibles=this.horarioEspacioRepository.findBySubEspacioYFecha(idSubEspacio, fechaEvento, diaSemana);
        List<Evento>eventosDelDia=this.eventoRepository.findBySubEspacioAndFecha(idSubEspacio, fechaEvento);
        List<DTOHorarioDisponible> dtoHorarioDisponibles=new ArrayList<>();

        for(HorarioEspacio horario:horariosDisponibles){
            LocalTime inicioHorario = horario.getHoraDesde();
            LocalTime finHorario = horario.getHoraHasta();

            boolean superpuesto = eventosDelDia.stream().anyMatch(evento -> {
                LocalTime inicioEvento = evento.getFechaHoraInicio().toLocalTime();
                LocalTime finEvento = evento.getFechaHoraFin().toLocalTime();

                // Verifica si se solapan
                return inicioHorario.isBefore(finEvento) && finHorario.isAfter(inicioEvento);
            });

            if (!superpuesto) {
                dtoHorarioDisponibles.add(DTOHorarioDisponible.builder()
                        .id(horario.getId())
                        .fechaHoraDesde(inicioHorario == null ? null : inicioHorario.atDate(fechaEvento)
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .fechaHoraHasta(finHorario == null ? null : finHorario.atDate(fechaEvento)
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .precioOrganizacion(horario.getPrecioOrganizacion().doubleValue())
                        .adicionalPorInscripcion(horario.getAdicionalPorInscripcion().doubleValue())
                        .build());
            }
        }
        return dtoHorarioDisponibles;
    }

    @Override
    public List<DTOPeriodoDisponible> obtenerPeriodosLibres(Long idSubEspacio)throws Exception{
        SubEspacio subespacio = subEspacioRepository.findById(idSubEspacio).orElseThrow(() -> new Exception("No se encontró el subespacio"));

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));

        boolean administrador = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() == 1;

        if (!administrador) {
            throw new Exception("Solo los administradores del espacio pueden buscar periodos libres");
        }

        LocalDateTime fechaInicio = LocalDateTime.now();
        LocalDateTime fechaFin = fechaInicio.plusDays(365);

        // Agarrar cronogramas vigentes del próximo año
        List<ConfiguracionHorarioEspacio> cronogramas = new ArrayList<>(subespacio.getConfiguracionesHorarioEspacio().stream()
                .filter(c ->
                        c.getFechaDesde().isAfter(fechaInicio) && c.getFechaDesde().isBefore(fechaFin)
                        || c.getFechaHasta().isAfter(fechaInicio) && c.getFechaHasta().isBefore(fechaFin)
                        || c.getFechaDesde().isBefore(fechaInicio) && c.getFechaHasta().isAfter(fechaFin)
                ).toList());

        // Ordenarlos
        cronogramas.sort((lhs, rhs) -> lhs.getFechaDesde().isBefore(rhs.getFechaDesde()) ? -1 : 1);

        ArrayList<DTOPeriodoDisponible> ret = new ArrayList<>();


        LocalDateTime finPrevio = fechaInicio;
        for (ConfiguracionHorarioEspacio c : cronogramas) {
            if (c.getFechaDesde().isBefore(finPrevio)) {
                finPrevio = c.getFechaHasta();
                continue;
            }

            // Agregar periodos sin cronograma vigente
            ret.add(DTOPeriodoDisponible.builder()
                .fechaHoraDesde(TimeUtil.toMillis(finPrevio))
                .fechaHoraHasta(TimeUtil.toMillis(c.getFechaDesde()))
                .build());

            finPrevio = c.getFechaHasta();

            // Agregar excepciones (solo la parte de adentro de su cronograma, y solo si son externas)
            ret.addAll(c.getExcepcionesHorarioEspacio().stream()
                .filter(e -> e.getTipoExcepcionHorarioEspacio().getNombre().equalsIgnoreCase("Externa"))
                .map(e -> DTOPeriodoDisponible.builder()
                    .fechaHoraDesde(Math.max(TimeUtil.toMillis(c.getFechaDesde()), TimeUtil.toMillis(e.getFechaHoraDesde())))
                    .fechaHoraHasta(Math.min(TimeUtil.toMillis(c.getFechaHasta()), TimeUtil.toMillis(e.getFechaHoraHasta())))
                    .build()).toList());
        }

        // Por si no termina con un cronograma vigente, agregar el último periodo
        if (finPrevio.isBefore(fechaFin)) {
            ret.add(DTOPeriodoDisponible.builder()
                .fechaHoraDesde(TimeUtil.toMillis(finPrevio))
                .fechaHoraHasta(TimeUtil.toMillis(fechaFin))
                .build());
        }

        return ret;

        /*
        //buscar fechas a partir del día de hoy que no tengan cronograma con sus horarios
        //buscar excepciones del cronograma vigente o futuro

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(365);

        // Traer todos los horarios ocupados por cronogramas
        List<HorarioEspacio> ocupados = horarioEspacioRepository.findHorariosOcupados(idSubEspacio, fechaInicio, fechaFin);

        // Traer todas las excepciones dentro de cronogramas
        List<ExcepcionHorarioEspacio> excepciones = excepcionHorarioEspacioRepository.findExcepcionesPorSubEspacio(idSubEspacio, fechaInicio, fechaFin);

        List<DTOPeriodoDisponible> disponibles = new ArrayList<>();

        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            final LocalDate fechaFinal=fecha;
            String diaSemanaStr = diaSemana(fecha);

            LocalTime apertura = LocalTime.of(9, 0);
            LocalTime cierre = LocalTime.of(20, 0);

            LocalTime desde = apertura;

            // Filtrar horarios ocupados de ese día
            List<HorarioEspacio> ocupadosHoy = ocupados.stream()
                    .filter(h -> h.getDiaSemana().equals(diaSemanaStr))
                    .sorted(Comparator.comparing(HorarioEspacio::getHoraDesde))
                    .collect(Collectors.toList());

            for (HorarioEspacio ocupado : ocupadosHoy) {
                if (desde.isBefore(ocupado.getHoraDesde())) {
                    LocalDateTime rangoDesde = desde.atDate(fecha);
                    LocalDateTime rangoHasta = ocupado.getHoraDesde().atDate(fecha);

                    // Considerar excepciones dentro de este rango
                    List<ExcepcionHorarioEspacio> excepcionesHoy = excepciones.stream()
                            .filter(e -> !e.getFechaHoraDesde().toLocalDate().isBefore(fechaFinal) &&
                                    !e.getFechaHoraHasta().toLocalDate().isAfter(fechaFinal))
                            .collect(Collectors.toList());

                    // Si hay excepciones dentro de este rango, se pueden considerar rangos libres adicionales
                    for (ExcepcionHorarioEspacio exc : excepcionesHoy) {
                        LocalDateTime excDesde = exc.getFechaHoraDesde();
                        LocalDateTime excHasta = exc.getFechaHoraHasta();

                        // Asegurarse de que la excepción esté dentro del rango libre
                        LocalDateTime eDesde = excDesde.isAfter(rangoDesde) ? excDesde : rangoDesde;
                        LocalDateTime eHasta = excHasta.isBefore(rangoHasta) ? excHasta : rangoHasta;

                        // Verificar que no haya eventos en la excepción
                        List<Evento> eventosSolapados = eventoRepository.findEventosEnRango(idSubEspacio, eDesde, eHasta);
                        if (eventosSolapados.isEmpty()) {
                            disponibles.add(DTOPeriodoDisponible.builder()
                                    .fechaHoraDesde(eDesde.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                    .fechaHoraHasta(eHasta.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                    .build());
                        }
                    }

                    // Verificar rango original sin excepciones
                    List<Evento> eventosSolapados = eventoRepository.findEventosEnRango(idSubEspacio, rangoDesde, rangoHasta);
                    if (eventosSolapados.isEmpty()) {
                        disponibles.add(DTOPeriodoDisponible.builder()
                                .fechaHoraDesde(rangoDesde.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                .fechaHoraHasta(rangoHasta.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                .build());
                    }
                }
                desde = ocupado.getHoraHasta();
            }

            // Último rango hasta el cierre del día
            if (desde.isBefore(cierre)) {
                LocalDateTime rangoDesde = desde.atDate(fecha);
                LocalDateTime rangoHasta = cierre.atDate(fecha);

                List<Evento> eventosSolapados = eventoRepository.findEventosEnRango(idSubEspacio, rangoDesde, rangoHasta);
                if (eventosSolapados.isEmpty()) {
                    disponibles.add(DTOPeriodoDisponible.builder()
                            .fechaHoraDesde(rangoDesde.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .fechaHoraHasta(rangoHasta.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .build());
                }
            }
        }

        return disponibles;

        */
    }

    private String diaSemana(LocalDate fecha) {
        return switch (fecha.getDayOfWeek()) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }


}
