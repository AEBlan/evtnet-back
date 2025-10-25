package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "usuario")
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

    @Column(name = "fecha_nacimiento")
    private LocalDateTime fechaNacimiento;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "contrasena")
    private String contrasena;

    @Column(name = "CBU")
    private String CBU;

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    // Relaciones (las existentes que ya tenías)
    @OneToMany(mappedBy = "usuario")
    private List<RolUsuario> rolesUsuario;

    @OneToMany(mappedBy = "autor")
    private List<Calificacion> calificacionesAutores;

    @OneToMany(mappedBy = "denunciante")
    private List<DenunciaEvento> denunciasEvento;

    @OneToMany(mappedBy = "usuario")
    private List<ResenaEspacio> resenasEspacio;

    @OneToMany(mappedBy = "solicitante")
    private List<SolicitudEspacioPublico> solicitudesEspacioPublico;

    @OneToMany(mappedBy = "usuario")
    private List<AdministradorEvento> administradoresEvento;

    @OneToMany(mappedBy = "usuario")
    private List<AdministradorEspacio> administradoresEspacio;

    @OneToMany(mappedBy = "usuario")
    private List<AdministradorSuperEvento> administradoresSuperEvento;

    @OneToMany(mappedBy = "usuario1")
    private List<Chat> usuarios1;

    @OneToMany(mappedBy = "usuario2")
    private List<Chat> usuarios2;

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioGrupo> usuarioGrupo;

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioInstanciaMascota> usuarioInstanciaMascota;

    @OneToMany(mappedBy = "cobro")
    private List<ItemComprobantePago> itemComprobantePagosCobro;

    @OneToMany(mappedBy = "pago")
    private List<ItemComprobantePago> itemComprobantePagosPago;

    @OneToMany(mappedBy = "usuario")
    private List<Mensaje> mensajes;

    @OneToMany(mappedBy = "usuario")
    private List<Inscripcion> inscripciones;

    @OneToMany(mappedBy = "calificado")
    private List<Calificacion> calificacionesClasificado;

    @OneToMany(mappedBy = "responsable")
    private List<DenunciaEventoEstado> denunciasEventoEstado;

    @OneToMany(mappedBy = "responsable")
    private List<SEPEstado> sepEstados;

    @OneToMany(mappedBy = "usuario")
    private List<EncargadoSubEspacio> encargadoSubEspacios;


    public List<String> getPermisos() {
        return this.getRolesUsuario().stream().map(r -> r.getRol().getRolPermisos().stream().map(p -> p.getPermiso().getNombre()).toList()).flatMap(List::stream).toList();
    }

    

    
}
