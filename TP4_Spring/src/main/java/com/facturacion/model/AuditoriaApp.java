package com.facturacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class AuditoriaApp extends EntityId {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaBaja;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date fechaModificacion;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioCarga;

    @ManyToOne
    protected Usuario usuarioBaja;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected Usuario usuarioModificacion;
}
