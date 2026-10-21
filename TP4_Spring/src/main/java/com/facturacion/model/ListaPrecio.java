package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lista_precio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListaPrecio extends AuditoriaApp {
    @Column(nullable = false) private String codigo;
    @Column(nullable = false) private String denominacion;
}
