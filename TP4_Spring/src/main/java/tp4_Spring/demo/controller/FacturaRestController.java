package tp4_Spring.demo.controller;

import com.facturacion.dto.FacturaReporteDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tp4_Spring.demo.repository.FacturaRepository;
import tp4_Spring.demo.export.ReporteArchivoGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaRestController {

    private final FacturaRepository facturaR;

    public FacturaRestController(FacturaRepository facturaR) {
        this.facturaR = facturaR;
    }

    @GetMapping
    public List<FacturaReporteDTO> buscarFacturas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo) {

        return facturaR.buscarFacturasFiltradas(
                inicioDelDia(fechaDesde), finDelDia(fechaHasta), estado, montoMinimo);
    }

    @GetMapping(value = "/reporte.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo) throws IOException {

        byte[] archivo = ReporteArchivoGenerator.generarPdf(buscarFacturasConFiltros(
                fechaDesde, fechaHasta, estado, montoMinimo));
        return descarga(archivo, "reporte-facturas.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping(value = "/reporte.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> descargarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo) throws IOException {

        byte[] archivo = ReporteArchivoGenerator.generarExcel(buscarFacturasConFiltros(
                fechaDesde, fechaHasta, estado, montoMinimo));
        return descarga(archivo, "reporte-facturas.xlsx", MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    private List<FacturaReporteDTO> buscarFacturasConFiltros(LocalDate fechaDesde, LocalDate fechaHasta,
                                                               String estado, Double montoMinimo) {
        return facturaR.buscarFacturasFiltradas(inicioDelDia(fechaDesde), finDelDia(fechaHasta), estado, montoMinimo);
    }

    private ResponseEntity<byte[]> descarga(byte[] archivo, String nombreArchivo, MediaType tipoContenido) {
        return ResponseEntity.ok()
                .contentType(tipoContenido)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(archivo);
    }

    private Date inicioDelDia(LocalDate fecha) {
        return fecha == null ? null : Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date finDelDia(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1));
    }
}
