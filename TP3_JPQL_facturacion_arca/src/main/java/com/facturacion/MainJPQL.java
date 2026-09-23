package com.facturacion;

import com.facturacion.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainJPQL {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            new CargaDatos(em).cargarSiHaceFalta();
            em.clear();

            ConsultasJPQL c = new ConsultasJPQL(em);

            // Nivel 1
            titulo("NIVEL 1: Consultas Básicas y Proyecciones");

            consigna(1, "Todas las facturas de venta");
            c.todasLasFacturas().forEach(f -> System.out.println("  " + factura(f)));

            consigna(2, "Proyección: número, fecha de emisión e importe total");
            for (Object[] fila : c.proyeccionFacturas()) {
                System.out.printf("  Nro %s | %s | $ %,.2f%n", fila[0], FMT.format((Date) fila[1]), (Double) fila[2]);
            }

            consigna(3, "Artículos del rubro 'Electrónica'");
            c.articulosPorRubro("Electrónica").forEach(a -> System.out.println("  " + articulo(a)));

            consigna(4, "Facturas emitidas entre 01/08/2026 y 31/08/2026");
            c.facturasEntreFechas(fecha(2026, 8, 1), fecha(2026, 8, 31))
                    .forEach(f -> System.out.println("  " + factura(f)));

            // Nivel 2
            titulo("NIVEL 2: Condicionales, Texto y Agregaciones");

            consigna(5, "Estado EMITIDA, importe > $10.000 y sin fecha de anulación");
            c.facturasEmitidasNoAnuladas("EMITIDA", 10000).forEach(f -> System.out.println("  " + factura(f)));

            consigna(6, "Clientes cuya denominación contiene 'LÓPEZ' (sin importar mayúsculas) o CUIT que empieza con '20-'");
            c.buscarClientes("LÓPEZ", "20-")
                    .forEach(cl -> System.out.println("  " + cl.getCuitCuil() + " | " + cl.getDenominacion()));

            consigna(7, "Estados distintos de las facturas (orden ascendente)");
            c.estadosDistintos().forEach(e -> System.out.println("  " + e));

            consigna(8, "COUNT, SUM y AVG de las facturas en un solo arreglo");
            Object[] tot = c.totalesFacturas();
            System.out.printf("  Cantidad: %s | Suma: $ %,.2f | Promedio: $ %,.2f%n", tot[0], (Double) tot[1], (Double) tot[2]);

            consigna(9, "Puntos de venta con número IN (1, 2, 5)");
            c.puntosDeVentaPorNumeros(Arrays.asList(1, 2, 5))
                    .forEach(pv -> System.out.println("  PV " + pv.getNumero() + " | " + pv.getDescripcion()));

            // Nivel 3
            titulo("NIVEL 3: Navegación, JOINs y Subconsultas");

            consigna(10, "Facturas cargadas por el usuario 'lperez' (f.usuarioCarga.usuario)");
            c.facturasPorUsuarioCarga("lperez").forEach(f -> System.out.println("  " + factura(f)));

            consigna(11, "Detalles de facturas del punto de venta 2 (INNER JOIN)");
            for (FacturaVentaDetalle d : c.detallesPorPuntoVenta(2)) {
                System.out.printf("  Factura %s | %s x%.0f | $ %,.2f%n",
                        d.getFactura().getNumero(), d.getDescripcion(), d.getCantidad(), d.getImporteSubtotal());
            }

            consigna(12, "Artículos con su marca, incluidos los que no tienen (LEFT JOIN)");
            for (Object[] fila : c.articulosConMarca()) {
                System.out.println("  " + fila[0] + " | " + (fila[1] == null ? "(sin marca)" : fila[1]));
            }

            consigna(13, "Facturas con al menos un artículo de la marca 'Samsung'");
            c.facturasConMarca("Samsung").forEach(f -> System.out.println("  " + factura(f)));

            consigna(14, "Facturas con importe mayor al promedio (subconsulta)");
            c.facturasSobrePromedio().forEach(f -> System.out.println("  " + factura(f)));

            // Nivel 4
            titulo("NIVEL 4: GROUP BY y HAVING");

            consigna(15, "Por punto de venta: cantidad de facturas y total facturado");
            for (Object[] fila : c.facturacionPorPuntoVenta()) {
                System.out.printf("  %s | %s facturas | $ %,.2f%n", fila[0], fila[1], (Double) fila[2]);
            }

            consigna(16, "Usuarios de carga con más de 5 facturas (HAVING)");
            for (Object[] fila : c.usuariosConMasDeNFacturas(5)) {
                System.out.printf("  %s (%s %s) | %s facturas%n", fila[0], fila[1], fila[2], fila[3]);
            }

            consigna(17, "Por marca: unidades vendidas y subtotal acumulado");
            for (Object[] fila : c.ventasPorMarca()) {
                System.out.printf("  %s | %.0f unidades | $ %,.2f%n", fila[0], (Double) fila[1], (Double) fila[2]);
            }

            // Nivel 5
            titulo("NIVEL 5: EXISTS, NOT EXISTS y CASE WHEN");

            consigna(18, "Marcas con al menos un artículo facturado (EXISTS)");
            c.marcasConArticulosFacturados().forEach(m -> System.out.println("  " + m.getDenominacion()));

            consigna(19, "Artículos nunca facturados (NOT EXISTS)");
            c.articulosNuncaFacturados().forEach(a -> System.out.println("  " + articulo(a)));

            consigna(20, "Facturas por categoría de importe (CASE WHEN), de mayor a menor");
            for (Object[] fila : c.facturasPorCategoria(10000, 50000)) {
                System.out.printf("  Nro %s | $ %,12.2f | %s%n", fila[0], (Double) fila[1], fila[2]);
            }

        } finally {
            em.close();
            emf.close();
        }
    }

    // metodos para imprimir

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println(texto);
        System.out.println("==================================================================");
    }

    private static void consigna(int numero, String texto) {
        System.out.println();
        System.out.println(numero + ". " + texto);
    }

    private static String factura(FacturaVenta f) {
        return String.format("Nro %s | %s | PV %d | %s | $ %,.2f | %s | cargó: %s",
                f.getNumero(), FMT.format(f.getFechaEmision()), f.getPuntoVenta().getNumero(),
                f.getCliente() == null ? "-" : f.getCliente().getDenominacion(),
                f.getImporteTotal(), f.getEstado(), f.getUsuarioCarga().getUsuario());
    }

    private static String articulo(Articulo a) {
        return a.getCodigo() + " | " + a.getDenominacion() + " | rubro: " + a.getRubro().getDenominacion()
                + " | marca: " + (a.getMarca() == null ? "(sin marca)" : a.getMarca().getDenominacion());
    }

    private static Date fecha(int anio, int mes, int dia) {
        return Date.from(LocalDate.of(anio, mes, dia).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
