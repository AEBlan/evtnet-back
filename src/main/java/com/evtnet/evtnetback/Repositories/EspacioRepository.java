package com.evtnet.evtnetback.Repositories;

import java.math.BigDecimal;
import java.util.List;

import com.evtnet.evtnetback.Entities.AdministradorEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.Entities.Espacio;

import com.evtnet.evtnetback.Repositories.BaseRepository;

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
""")
    List<Espacio> findEspacios(@Param("text") String texto, @Param("tiposEspacio")List<Long>tiposEspacio, @Param("latitudDesde")BigDecimal latitudDesde, @Param("latitudHasta") BigDecimal latitudHasta, @Param("longitudDesde")BigDecimal longitudDesde, @Param("longitudHasta") BigDecimal longitudHasta);

    @Query("""
    SELECT DISTINCT e FROM Espacio e
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
    """)
    List<Espacio> findMisEspacios(@Param("text") String texto, @Param("esAdmin")boolean esAdmin, @Param("esPropietario")boolean esPropietario, @Param("username") String username);

    @Query("""
        SELECT tae.nombre
        FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE e.id = :id
          AND u.username = :username
    """)
    String rolByEspacioUsername(@Param("id") Long id, @Param("username") String username);
}

