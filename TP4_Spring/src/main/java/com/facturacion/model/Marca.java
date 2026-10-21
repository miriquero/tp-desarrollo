package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marca")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Marca extends AuditoriaApp {
    @Column(nullable = false) private String denominacion;
    @Column(nullable = false) private Integer codigo;
}
