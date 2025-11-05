package com.evtnet.evtnetback.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.evtnet.evtnetback.entity.SolicitudEspacioPublico;
import com.evtnet.evtnetback.entity.SubEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.entity.Espacio;

@Repository
public interface EspacioRepository extends BaseRepository<Espacio, Long> {

    @Query("""
        SELECT e FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE u.username = :username
          AND tae.nombre = 'Propietario'
    """)
    List<Espacio> findByPropietarioUsername(@Param("username") String username);

    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
        FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE e.id = :id
          AND u.username = :username
          AND tae.nombre = :tipoAdmin
          AND ae.fechaHoraBaja IS NULL
    """)
    boolean existsByIdAndPropietarioAdmin_Username(@Param("id") Long id, @Param("username") String username, @Param("tipoAdmin") String tipoAdmin);
    
    @Query("""
        SELECT e FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE u.username = :username
          AND tae.nombre = 'Propietario'
    """)
    List<Espacio> findAllByPropietario_Username(String username);

    @Query("""
        SELECT count(e) FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE u.username = :username
          AND tae.nombre = 'Propietario'
          AND LOWER(e.nombre) = LOWER(:nombre)
          AND e.latitudUbicacion BETWEEN :latitudDesde AND :latitudHasta
          AND e.longitudUbicacion BETWEEN :longitudDesde AND :longitudHasta
    """)
    Long findDuplicado(@Param("nombre") String nombre, @Param("username") String username, @Param("latitudDesde")BigDecimal latitudDesde, @Param("latitudHasta") BigDecimal latitudHasta, @Param("longitudDesde")BigDecimal longitudDesde, @Param("longitudHasta") BigDecimal longitudHasta);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    JOIN e.tipoEspacio te
    LEFT JOIN e.subEspacios sub
    LEFT JOIN sub.caracteristicas c
    LEFT JOIN sub.disciplinasSubespacio ds
    LEFT JOIN ds.disciplina d
    WHERE te.id IN (:tiposEspacio)
      AND e.latitudUbicacion BETWEEN :latitudDesde AND :latitudHasta
      AND e.longitudUbicacion BETWEEN :longitudDesde AND :longitudHasta
      AND (
        LOWER(e.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.direccionUbicacion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sub.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sub.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')        
        OR LOWER(c.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(d.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
      )
      AND (:idDisciplina is not null AND d.id in (:idDisciplina))
      AND ee.estadoEspacio.nombre like "Habilitado"
""")
    List<Espacio> findEspacios(@Param("texto") String texto, @Param("tiposEspacio")List<Long>tiposEspacio, @Param("latitudDesde")BigDecimal latitudDesde, @Param("latitudHasta") BigDecimal latitudHasta, @Param("longitudDesde")BigDecimal longitudDesde, @Param("longitudHasta") BigDecimal longitudHasta, @Param("idDisciplina") List<Long> idDisciplina);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    JOIN e.tipoEspacio te
    LEFT JOIN e.subEspacios sub
    LEFT JOIN sub.caracteristicas c
    LEFT JOIN sub.disciplinasSubespacio ds
    LEFT JOIN ds.disciplina d
    WHERE e.latitudUbicacion BETWEEN :latitudDesde AND :latitudHasta
      AND e.longitudUbicacion BETWEEN :longitudDesde AND :longitudHasta
      AND ee.estadoEspacio.nombre like "Habilitado"
      AND ee.fechaHoraBaja is null
""")
    List<Espacio> findEspaciosByUbicacion(
            @Param("latitudDesde")BigDecimal latitudDesde,
            @Param("latitudHasta") BigDecimal latitudHasta,
            @Param("longitudDesde")BigDecimal longitudDesde,
            @Param("longitudHasta") BigDecimal longitudHasta);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    LEFT JOIN e.subEspacios sub
    LEFT JOIN sub.caracteristicas c
    LEFT JOIN sub.disciplinasSubespacio ds
    LEFT JOIN ds.disciplina d
    WHERE (
        LOWER(e.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.direccionUbicacion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sub.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sub.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')        
        OR LOWER(c.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(d.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
      )
      AND ee.estadoEspacio.nombre like "Habilitado"
      AND ee.fechaHoraBaja is null
""")
    List<Espacio> findEspaciosByTexto(@Param("texto") String texto);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    JOIN e.tipoEspacio te
    WHERE te.id IN (:tiposEspacio)
      AND ee.estadoEspacio.nombre like "Habilitado"
      AND ee.fechaHoraBaja is null
""")
    List<Espacio> findEspaciosByTipo(@Param("tiposEspacio")List<Long>tiposEspacio);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    JOIN e.tipoEspacio te
    LEFT JOIN e.subEspacios sub
    LEFT JOIN sub.disciplinasSubespacio ds
    LEFT JOIN ds.disciplina d
    WHERE d.id IN (:disciplinas)
      AND ee.estadoEspacio.nombre like "Habilitado"
      AND ee.fechaHoraBaja is null
""")
    List<Espacio> findEspaciosByDisciplina(@Param("disciplinas")List<Long>disciplinas);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    WHERE ee.estadoEspacio.nombre like "Habilitado"
        AND ee.fechaHoraBaja is null
""")
    List<Espacio> findAllHabilitados();

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado espacioEstado
    JOIN e.administradoresEspacio ae
    JOIN ae.tipoAdministradorEspacio tae
    JOIN ae.usuario u
    LEFT JOIN e.subEspacios sub
    LEFT JOIN sub.caracteristicas c
    LEFT JOIN sub.disciplinasSubespacio ds
    LEFT JOIN ds.disciplina d
    WHERE (
        LOWER(e.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.direccionUbicacion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sub.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sub.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(c.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(d.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
      )
      AND (
        (:esAdmin = TRUE AND tae.nombre = 'Administrador' AND u.username=:username)
        OR (:esPropietario = TRUE AND tae.nombre = 'Propietario' AND u.username=:username)
      )
      AND ae.fechaHoraBaja IS NULL
      
    """)
    List<Espacio> findMisEspacios(@Param("texto") String texto, @Param("esAdmin")boolean esAdmin, @Param("esPropietario")boolean esPropietario, @Param("username") String username);

    @Query("""
        SELECT tae.nombre
        FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE e.id = :id
          AND u.username = :username
          AND ae.fechaHoraBaja IS NULL
    """)
    String rolByEspacioUsername(@Param("id") Long id, @Param("username") String username);

    @Query("""
    SELECT s
    FROM SubEspacio s
    LEFT JOIN s.encargadoSubEspacio es
    WHERE s.espacio.id = :idEspacio
    """)
    List<SubEspacio>findEncargadoByEspacio(@Param("idEspacio")Long idEspacio);

    @Query("""
    SELECT e
    FROM Espacio e
    WHERE e.tipoEspacio.nombre like "Público"
    """)
    List<Espacio> findPublicos();

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.tipoEspacio te
    WHERE (
        LOWER(e.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(e.direccionUbicacion) LIKE CONCAT('%', LOWER(:texto), '%')
      )
      AND te.nombre like 'Privado'
""")
    List<Espacio> findEspaciosByTextoSolicitud(@Param("texto") String texto);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.espacioEstado ee
    JOIN e.tipoEspacio te
    WHERE ee.estadoEspacio.id IN (:estado)
      AND ee.fechaHoraBaja is null
      AND te.nombre like 'Privado'
""")
    List<Espacio> findEspaciosByEstado(@Param("estado")List<Long>estado);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
    JOIN e.tipoEspacio te
    WHERE e.latitudUbicacion BETWEEN :latitudDesde AND :latitudHasta
      AND e.longitudUbicacion BETWEEN :longitudDesde AND :longitudHasta
      AND te.nombre like 'Privado'
""")
    List<Espacio> findEspaciosByUbicacionSolicitud(
            @Param("latitudDesde")BigDecimal latitudDesde,
            @Param("latitudHasta") BigDecimal latitudHasta,
            @Param("longitudDesde")BigDecimal longitudDesde,
            @Param("longitudHasta") BigDecimal longitudHasta);

    @Query("""
    SELECT DISTINCT e
    FROM Espacio e
    JOIN e.tipoEspacio te
    WHERE FUNCTION('DATE',e.fechaHoraAlta) BETWEEN :fechaIngresoDesde AND :fechaIngresoHasta
        AND te.nombre like 'Privado'
""")
    List<Espacio> findEspaciosByFechaIngreso(
            @Param("fechaIngresoDesde") LocalDate fechaIngresoDesde,
            @Param("fechaIngresoHasta") LocalDate fechaIngresoHasta);

    @Query("""
    SELECT DISTINCT e
    FROM Espacio e
    JOIN e.espacioEstado ee
    JOIN e.tipoEspacio te
    WHERE FUNCTION('DATE',ee.fechaHoraAlta) BETWEEN :fechaCambioDesde AND :fechaCambioHasta
        AND ee.fechaHoraBaja is null
        AND te.nombre like 'Privado'
""")
    List<Espacio> findEspaciosByFechaCambioEstado(
            @Param("fechaCambioDesde") LocalDate fechaCambioDesde,
            @Param("fechaCambioHasta") LocalDate fechaCambioHasta);

    @Query("""
    SELECT DISTINCT e
    FROM Espacio e
    JOIN e.tipoEspacio te
    WHERE te.nombre like 'Privado'
""")
    List<Espacio> findAllPrivados();

    @Query("""
    SELECT DISTINCT e
    FROM Espacio e
    JOIN e.administradoresEspacio ae
    JOIN ae.usuario u
    WHERE ae.tipoAdministradorEspacio.nombre like 'Propietario'
        AND u.username like :username
        AND ae.fechaHoraBaja is null
""")
    List<Espacio> findEspaciosPropios(@Param("username") String username);
}

