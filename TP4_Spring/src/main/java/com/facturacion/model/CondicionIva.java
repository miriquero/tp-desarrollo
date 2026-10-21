package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "condicion_iva")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CondicionIva extends AuditoriaApp {
    @Column(nullable = false) private int codigoAfip;
    @Column(nullable = false) private String denominacion;
}
