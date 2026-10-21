package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "factura_venta_detalle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"factura", "listaPrecioArticulo"})
@ToString(exclude = {"factura", "listaPrecioArticulo"})
public class FacturaVentaDetalle extends EntityId {
    @ManyToOne @JoinColumn(nullable = false) private FacturaVenta factura;
    @ManyToOne @JoinColumn(nullable = false) private ListaPrecioArticulo listaPrecioArticulo;
    private String descripcion;
    @Column(nullable = false) private double cantidad;
    @Column(nullable = false) private double precioUnitario;
    private double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;
    @Column(nullable = false) private double importeSubtotal;
}
