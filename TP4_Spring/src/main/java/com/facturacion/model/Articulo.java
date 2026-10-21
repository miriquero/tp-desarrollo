package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articulo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Articulo extends AuditoriaApp {
    @ManyToOne private Rubro rubro;
    @Column(nullable = false) private String codigo;
    @Column(nullable = false) private String denominacion;
    @ManyToOne private Marca marca;
}
