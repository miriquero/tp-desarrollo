package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lista_precio_articulo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListaPrecioArticulo extends AuditoriaApp {
    @ManyToOne @JoinColumn(nullable = false) private ListaPrecio listaPrecio;
    @Column(nullable = false) private double precioVenta;
    @ManyToOne @JoinColumn(nullable = false) private Articulo articulo;
}
