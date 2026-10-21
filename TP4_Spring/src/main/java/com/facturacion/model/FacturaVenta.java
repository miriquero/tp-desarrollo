package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "factura_venta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "detalles")
@ToString(exclude = "detalles")
public class FacturaVenta extends AuditoriaApp {
    private Long numero;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaEmision;

    @ManyToOne @JoinColumn(nullable = true) private Cliente cliente;
    @ManyToOne @JoinColumn(nullable = false) private CondicionIva condicionIva;
    @ManyToOne @JoinColumn(nullable = false) private TipoMoneda tipoMoneda;
    @ManyToOne @JoinColumn(nullable = false) private PuntoVenta puntoVenta;

    private double importeCobrado;
    private double importeSaldo;
    @Column(nullable = false) private double importeTotal;
    private String cae;
    @Temporal(TemporalType.TIMESTAMP) private Date caeFechaVencimiento;
    private String resultadoAfip;
    private String motivoRechazo;
    @Column(nullable = false) private String estado;
    @Temporal(TemporalType.TIMESTAMP) private Date fechaAnulacion;
    private String observaciones;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<FacturaVentaDetalle> detalles = new ArrayList<>();

    public void agregarDetalle(FacturaVentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }
}
