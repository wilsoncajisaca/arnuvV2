package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Comment("Tabla que almacena al paseador con la mascota ")
@Entity
@Table(name = "paseo")
public class Paseo {
	@Id
    @Comment("Codigo del paseo")
    @Column(name = "idpaseo", length = 100)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id=-1;
	
	@DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date fecha;

    @ManyToOne()
    @Comment("Codigo de personas")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "idpersonapasedor")
    @ToString.Exclude
    private Personadetalle idpersonapasedor;

    @ManyToOne()
    @Comment("Codigo de personas")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "idpersonacliente")
    @ToString.Exclude
    private Personadetalle idpersonacliente;
    
    @ManyToOne()
    @Comment("Codigo de mascotas")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "idMascota")
    @ToString.Exclude
    private MascotaDetalle idMascota;

    @ManyToOne()
    @Comment("Codigo de tarifas")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "idtarifario")
    @ToString.Exclude
    private Tarifario idtarifario;

    @Comment("Fecha real de inicio del paseo")
    @Column(name = "fecharealinicio")
    //@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date fecharealinicio;

    @Comment("Fecha real de fin del paseo")
    @Column(name = "fecharealfin")
    //@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date fecharealfin;
/*
    @Comment("Fecha de inicio del paseo formato YYYYMMDD")
    @Column(name = "fechainicio", precision = 8)
    private BigDecimal fechainicio;

    @Comment("Fecha de fin del paseo formato YYYYMMDD")
    @Column(name = "fechafin", precision = 8)
    private BigDecimal fechafin;

    @Comment("Hora de inicio del paseo")
    @Column(name = "horainicio")
    private LocalTime horainicio;

    @Comment("Hora de fin del paseo")
    @Column(name = "horafin")
    private LocalTime horafin;
*/
    @Comment("Precio total del paseo")
    @Column(name = "preciototal", precision = 10, scale = 7)
    private BigDecimal preciototal;

    @Comment("Observacion del cliente sobre el paseo")
    @Column(name = "observacionpaseo", length = 200)
    private String observacionpaseo;

    @Comment("Observacion del cliente sobre el paseador y su servicio")
    @Column(name = "observacionpaseador", length = 200)
    private String observacionpaseador;
    
    @Column
    private String estado;
    
    @OneToMany(mappedBy = "idpaseo")
	private List<Calificacion> calificacionpaseos;

}
