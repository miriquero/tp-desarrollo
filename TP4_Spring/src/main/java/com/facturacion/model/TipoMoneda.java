package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_moneda")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TipoMoneda extends AuditoriaApp {
    @Column(nullable = false) private String codigoAfip;
    @Column(nullable = false) private String denominacion;
    @Column(nullable = false) private String simbolo;
}
