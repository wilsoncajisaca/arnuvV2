package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Parametros implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String codigo;

    @Column
    private String valorText;

    @Column
    private Double valorNumber;

    @Column
    private String descripcion;

    @Column
    private Boolean estado;
}
