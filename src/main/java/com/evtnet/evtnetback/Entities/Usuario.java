package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Usuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "apellido")
    private String apellido;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "dni")
    private String dni;
    
    @Column(name = "mail")
    private String mail;
    
    @Column(name = "fechaNacimiento")
    private LocalDateTime fechaNacimiento;
    
    @Column(name = "fotoPerfil")
    private String fotoPerfil;
    
    @Column(name = "contrasena")
    private String contrasena;
    
    @Column(name = "CBU")
    private String CBU;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "usuario")
    private List<RolUsuario> rolesUsuario;
    
    @OneToMany(mappedBy = "autor")
    private List<Calificacion> calificacionesComoAutor;
    
    @OneToMany(mappedBy = "denunciante")
    private List<DenunciaEvento> denunciasEvento;
    
    @OneToMany(mappedBy = "usuario")
    private List<ReseñaEspacio> reseñasEspacio;
    
    @OneToMany(mappedBy = "solicitante")
    private List<SolicitudEspacioPublico> solicitudesEspacioPublico;
    
    @OneToOne(mappedBy = "responsable")
    private AdministradorEvento administradorEvento;
    
    @OneToOne(mappedBy = "propietario")
    private AdministradorEspacio administradorEspacio;
    
    @OneToOne(mappedBy = "organizador")
    private AdministradorSuperEvento administradorSuperEvento;
    
    // Auto-referencia
    @ManyToOne
    @JoinColumn(name = "usuario1_id")
    private Usuario usuario1;
    
    @OneToMany(mappedBy = "usuario1")
    private List<Usuario> usuarios2;
    
    @OneToMany(mappedBy = "usuario")
    private List<Mensaje> mensajes;
    
    @OneToMany(mappedBy = "usuario")
    private List<Inscripcion> inscripciones;
    
    @OneToMany(mappedBy = "calificado")
    private List<Calificacion> calificacionesComoCalificado;
    
    @OneToMany(mappedBy = "usuario")
    private List<DenunciaEventoEstado> denunciasEventoEstado;
    
    @OneToMany(mappedBy = "usuario")
    private List<SEPEstado> sepEstados; }