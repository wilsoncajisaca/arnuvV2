package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;
import java.util.Date;

@Data
@Comment("Tabla que almacena sessiones de usuario, cuando este esta conectado a  la aplicacion")
@Entity
@Table(name = "usuariosession")
public class Usuariosession implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Comment("Codigo de usuario.")
	@Column(name = "idusuario")
	private Integer idUsuario;

	@MapsId
	@OneToOne()
	@Comment("Codigo de usuario.")
	@JoinColumn(name = "idusuario")
	@ToString.Exclude
	private Usuariodetalle usuarioDetalle;

	@Comment("Numero de intentos de login")
	@Column(name = "numerointentos")
	private Integer numeroIntentos;

	@Comment("ID de la session del browser o movil")
	@Column(name = "idsession", length = 70)
	private String idSession;

	@Comment("Fecha de inicio de la session")
	@Column(name = "fechainicio")
	@Temporal(TemporalType.DATE)
	private Date fechaInicio;

	@Comment("Fecha de cierre de la session")
	@Column(name = "fechasalida")
	@Temporal(TemporalType.DATE)
	private Date fechaSalida;

	@Comment("1 Indica que el usuario realizo un login a la aplicacion y mantien la aplicacion activa, 0 el usuario esta fuera de la aplicacion.")
	@Convert(converter = NumericBooleanConverter.class)
	private Boolean activo;

	@Comment("I(usuario realiza login con exito), F(Intento fallido de login), L(Usuario relizo logout), S(Sistema realizo logout por inactividad)")
	@Column(name = "estado", length = 1)
	private String estado;

	@Comment("identificacion del dispositivo que ingresa el usuario")
	@Column(name = "useragent", length = 200)
	private String userAgent;

	@Comment("Codigo de terminal publico o privado con la que sale la peticon del usuario, IPV4 y IPV6")
	@Column(name = "iptermialremoto", length = 130)
	private String ipTermialRemoto;

}
