package com.facturacion;

import com.facturacion.model.Articulo;
import com.facturacion.model.Cliente;
import com.facturacion.model.FacturaVenta;
import com.facturacion.model.FacturaVentaDetalle;
import com.facturacion.model.Marca;
import com.facturacion.model.PuntoVenta;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Ejecuta las 20 consultas solicitadas en el TP de JPQL. */
public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();
        try {
            //consulta1FacturasCompletas(em);
            //consulta2ProyeccionFacturas(em);
            //consulta3ArticulosPorRubro(em);
            //consulta4FacturasPorRango(em);
            //consulta5FacturasEmitidasNoAnuladas(em);
            //consulta6ClientesPorTextoOCuit(em);
            //consulta7EstadosDistintos(em);
            //consulta8ResumenFacturas(em);
            //consulta9PuntosVentaPorNumeros(em);
            //consulta10FacturasPorUsuarioCarga(em);
            //consulta11DetallesPorPuntoVenta(em);
            //consulta12ArticulosYMarcas(em);
            //consulta13FacturasPorMarca(em);
            //consulta14FacturasSobrePromedio(em);
            //consulta15ResumenPorPuntoVenta(em);
            //consulta16UsuariosConMasDeCincoFacturas(em);
            //consulta17VentasPorMarca(em);
            //consulta18MarcasConArticulosFacturados(em);
            //consulta19ArticulosNuncaFacturados(em);
            //consulta20CategoriaPorImporte(em);
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void consulta1FacturasCompletas(EntityManager em) {
        mostrar("1. Todas las facturas", em.createQuery("SELECT f FROM FacturaVenta f", FacturaVenta.class).getResultList());
    }

    private static void consulta2ProyeccionFacturas(EntityManager em) {
        mostrar("2. Numero, fecha e importe", em.createQuery(
                "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f", Object[].class).getResultList());
    }

    private static void consulta3ArticulosPorRubro(EntityManager em) {
        mostrar("3. Articulos del rubro Electronica", em.createQuery(
                "SELECT a FROM Articulo a WHERE a.rubro.denominacion = :rubro", Articulo.class)
                .setParameter("rubro", "Electrónica").getResultList());
    }

    private static void consulta4FacturasPorRango(EntityManager em) {
        mostrar("4. Facturas del ultimo anio", em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.fechaEmision BETWEEN :desde AND :hasta", FacturaVenta.class)
                .setParameter("desde", fechaHaceDias(365)).setParameter("hasta", new Date()).getResultList());
    }

    private static void consulta5FacturasEmitidasNoAnuladas(EntityManager em) {
        mostrar("5. Facturas emitidas, mayores a 10000 y no anuladas", em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.estado = :estado AND f.importeTotal > :importe "
                        + "AND f.fechaAnulacion IS NULL", FacturaVenta.class)
                .setParameter("estado", "EMITIDA").setParameter("importe", 10000.0).getResultList());
    }

    private static void consulta6ClientesPorTextoOCuit(EntityManager em) {
        mostrar("6. Clientes por texto o CUIT", em.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.denominacion) LIKE LOWER(:texto) OR c.cuitCuil LIKE :prefijo",
                Cliente.class)
                .setParameter("texto", "%juan%").setParameter("prefijo", "20-%").getResultList());
    }

    private static void consulta7EstadosDistintos(EntityManager em) {
        mostrar("7. Estados sin duplicados", em.createQuery(
                "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC", String.class).getResultList());
    }

    private static void consulta8ResumenFacturas(EntityManager em) {
        mostrar("8. Cantidad, suma y promedio", em.createQuery(
                "SELECT COUNT(f), COALESCE(SUM(f.importeTotal), 0), COALESCE(AVG(f.importeTotal), 0) FROM FacturaVenta f",
                Object[].class).getResultList());
    }

    private static void consulta9PuntosVentaPorNumeros(EntityManager em) {
        mostrar("9. Puntos de venta 1, 2 y 5", em.createQuery(
                "SELECT p FROM PuntoVenta p WHERE p.numero IN :numeros", PuntoVenta.class)
                .setParameter("numeros", Arrays.asList(1, 2, 5)).getResultList());
    }

    private static void consulta10FacturasPorUsuarioCarga(EntityManager em) {
        mostrar("10. Facturas cargadas por admin", em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :usuario", FacturaVenta.class)
                .setParameter("usuario", "admin").getResultList());
    }

    private static void consulta11DetallesPorPuntoVenta(EntityManager em) {
        mostrar("11. Detalles del punto de venta 1", em.createQuery(
                "SELECT d FROM FacturaVentaDetalle d JOIN d.factura f WHERE f.puntoVenta.numero = :numeroPuntoVenta",
                FacturaVentaDetalle.class)
                .setParameter("numeroPuntoVenta", 1).getResultList());
    }

    private static void consulta12ArticulosYMarcas(EntityManager em) {
        mostrar("12. Articulos con su marca, incluidos los que no tienen", em.createQuery(
                "SELECT a.denominacion, m.denominacion FROM Articulo a LEFT JOIN a.marca m ORDER BY a.denominacion",
                Object[].class).getResultList());
    }

    private static void consulta13FacturasPorMarca(EntityManager em) {
        mostrar("13. Facturas con articulos de marca Acme", em.createQuery(
                "SELECT DISTINCT f FROM FacturaVenta f JOIN f.detalles d JOIN d.listaPrecioArticulo lpa "
                        + "JOIN lpa.articulo a JOIN a.marca m WHERE m.denominacion = :marca", FacturaVenta.class)
                .setParameter("marca", "Acme").getResultList());
    }

    private static void consulta14FacturasSobrePromedio(EntityManager em) {
        mostrar("14. Facturas con importe sobre el promedio", em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.importeTotal > "
                        + "(SELECT AVG(f2.importeTotal) FROM FacturaVenta f2)", FacturaVenta.class).getResultList());
    }

    private static void consulta15ResumenPorPuntoVenta(EntityManager em) {
        mostrar("15. Cantidad y total por punto de venta", em.createQuery(
                "SELECT p.descripcion, COUNT(f), COALESCE(SUM(f.importeTotal), 0) FROM FacturaVenta f "
                        + "JOIN f.puntoVenta p GROUP BY p.descripcion", Object[].class).getResultList());
    }

    private static void consulta16UsuariosConMasDeCincoFacturas(EntityManager em) {
        mostrar("16. Usuarios con mas de cinco facturas", em.createQuery(
                "SELECT u.usuario, COUNT(f) FROM FacturaVenta f JOIN f.usuarioCarga u GROUP BY u.usuario "
                        + "HAVING COUNT(f) > :minimo", Object[].class)
                .setParameter("minimo", 5L).getResultList());
    }

    private static void consulta17VentasPorMarca(EntityManager em) {
        mostrar("17. Unidades y subtotal por marca", em.createQuery(
                "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) FROM FacturaVentaDetalle d "
                        + "JOIN d.listaPrecioArticulo lpa JOIN lpa.articulo a JOIN a.marca m GROUP BY m.denominacion",
                Object[].class).getResultList());
    }

    private static void consulta18MarcasConArticulosFacturados(EntityManager em) {
        mostrar("18. Marcas con articulos facturados", em.createQuery(
                "SELECT m FROM Marca m WHERE EXISTS (SELECT a FROM Articulo a WHERE a.marca = m AND EXISTS "
                        + "(SELECT d FROM FacturaVentaDetalle d JOIN d.listaPrecioArticulo lpa WHERE lpa.articulo = a))",
                Marca.class).getResultList());
    }

    private static void consulta19ArticulosNuncaFacturados(EntityManager em) {
        mostrar("19. Articulos nunca facturados", em.createQuery(
                "SELECT a FROM Articulo a WHERE NOT EXISTS (SELECT d FROM FacturaVentaDetalle d "
                        + "JOIN d.listaPrecioArticulo lpa WHERE lpa.articulo = a)", Articulo.class).getResultList());
    }

    private static void consulta20CategoriaPorImporte(EntityManager em) {
        mostrar("20. Categoria de cada factura", em.createQuery(
                "SELECT f.numero, f.importeTotal, CASE WHEN f.importeTotal > :alto THEN 'ALTO VALOR' "
                        + "WHEN f.importeTotal >= :medio THEN 'MEDIO VALOR' ELSE 'BAJO VALOR' END "
                        + "FROM FacturaVenta f ORDER BY f.importeTotal DESC", Object[].class)
                .setParameter("alto", 50000.0).setParameter("medio", 10000.0).getResultList());
    }

    private static Date fechaHaceDias(int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -dias);
        return calendar.getTime();
    }

    private static void mostrar(String titulo, List<?> resultados) {
        System.out.println("\n--- " + titulo + " ---");
        if (resultados.isEmpty()) {
            System.out.println("Sin resultados. Ejecuta CargaDatosPrueba primero.");
            return;
        }
        for (Object resultado : resultados) {
            System.out.println(formatear(resultado));
        }
    }

    private static String formatear(Object resultado) {
        if (resultado instanceof Object[]) return Arrays.toString((Object[]) resultado);
        if (resultado instanceof FacturaVenta) {
            FacturaVenta factura = (FacturaVenta) resultado;
            return "Factura " + factura.getNumero() + " | " + factura.getEstado() + " | $" + factura.getImporteTotal();
        }
        if (resultado instanceof FacturaVentaDetalle) {
            FacturaVentaDetalle detalle = (FacturaVentaDetalle) resultado;
            return detalle.getDescripcion() + " | cantidad: " + detalle.getCantidad() + " | subtotal: $" + detalle.getImporteSubtotal();
        }
        if (resultado instanceof Articulo) {
            Articulo articulo = (Articulo) resultado;
            return articulo.getCodigo() + " | " + articulo.getDenominacion();
        }
        if (resultado instanceof PuntoVenta) {
            PuntoVenta puntoVenta = (PuntoVenta) resultado;
            return puntoVenta.getNumero() + " | " + puntoVenta.getDescripcion();
        }
        if (resultado instanceof Marca) return ((Marca) resultado).getDenominacion();
        return String.valueOf(resultado);
    }
}
