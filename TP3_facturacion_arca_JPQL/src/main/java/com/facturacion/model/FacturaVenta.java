package com.facturacion.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "factura_venta")
public class FacturaVenta extends AuditoriaApp {

    private Long numero;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaEmision;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CondicionIva condicionIva;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TipoMoneda tipoMoneda;

    @ManyToOne
    @JoinColumn(nullable = false)
    private PuntoVenta puntoVenta;

    private double importeCobrado;

    private double importeSaldo;

    @Column(nullable = false)
    private double importeTotal;

    private String cae;

    @Temporal(TemporalType.TIMESTAMP)
    private Date caeFechaVencimiento;

    private String resultadoAfip;

    private String motivoRechazo;

    @Column(nullable = false)
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAnulacion;

    private String observaciones;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<FacturaVentaDetalle> detalles = new ArrayList<>();

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public CondicionIva getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(CondicionIva condicionIva) {
        this.condicionIva = condicionIva;
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public double getImporteCobrado() {
        return importeCobrado;
    }

    public void setImporteCobrado(double importeCobrado) {
        this.importeCobrado = importeCobrado;
    }

    public double getImporteSaldo() {
        return importeSaldo;
    }

    public void setImporteSaldo(double importeSaldo) {
        this.importeSaldo = importeSaldo;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public Date getCaeFechaVencimiento() {
        return caeFechaVencimiento;
    }

    public void setCaeFechaVencimiento(Date caeFechaVencimiento) {
        this.caeFechaVencimiento = caeFechaVencimiento;
    }

    public String getResultadoAfip() {
        return resultadoAfip;
    }

    public void setResultadoAfip(String resultadoAfip) {
        this.resultadoAfip = resultadoAfip;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<FacturaVentaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<FacturaVentaDetalle> detalles) {
        this.detalles = detalles;
    }

    /**
     * Metodo helper para mantener la asociacion bidireccional consistente.
     */
    public void agregarDetalle(FacturaVentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }
}
