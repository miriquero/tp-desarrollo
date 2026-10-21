package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rubro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Rubro extends AuditoriaApp {
    @Column(nullable = false) private String denominacion;
    @Column(nullable = false) private Integer codigo;
}
