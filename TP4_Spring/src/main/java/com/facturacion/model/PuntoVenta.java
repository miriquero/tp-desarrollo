package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "punto_venta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PuntoVenta extends AuditoriaApp {
    @Column(nullable = false) private int numero;
    private String descripcion;
    private String tipoEmision;
    private String domicilioComercial;
}
