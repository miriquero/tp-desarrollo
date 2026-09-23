package com.facturacion;

import com.facturacion.model.*;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * TP Grupal - Consultas Avanzadas con JPQL.
 *
 * Todas las consultas:
 *  - usan em.createQuery(jpql, ClaseDestino.class)
 *  - reciben los valores por parametros con nombre (:parametro)
 *  - se escriben sobre entidades y atributos Java, no sobre tablas/columnas.
 *
 * Nota del modelo: FacturaVentaDetalle no apunta directo a Articulo, el camino es
 * detalle.listaPrecioArticulo.articulo (y de ahi .marca / .rubro).
 */
public class ConsultasJPQL {

    private final EntityManager em;

    public ConsultasJPQL(EntityManager em) {
        this.em = em;
    }

    // =====================================================================
    // Nivel 1: Consultas Basicas y Proyecciones
    // =====================================================================

    /** 1. Todas las facturas de venta. */
    public List<FacturaVenta> todasLasFacturas() {
        String jpql = "SELECT f FROM FacturaVenta f ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    /** 2. Solo numero, fecha de emision e importe total. Cada fila: [numero, fechaEmision, importeTotal]. */
    public List<Object[]> proyeccionFacturas() {
        String jpql = "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f ORDER BY f.numero";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 3. Articulos de un rubro con una denominacion dada (ej. "Electrónica"). */
    public List<Articulo> articulosPorRubro(String denominacionRubro) {
        String jpql = "SELECT a FROM Articulo a WHERE a.rubro.denominacion = :rubro";
        return em.createQuery(jpql, Articulo.class)
                .setParameter("rubro", denominacionRubro)
                .getResultList();
    }

    /** 4. Facturas emitidas entre dos fechas (inclusive). */
    public List<FacturaVenta> facturasEntreFechas(Date desde, Date hasta) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.fechaEmision BETWEEN :desde AND :hasta "
                + "ORDER BY f.fechaEmision";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    // =====================================================================
    // Nivel 2: Condicionales Combinados, Operadores de Texto y Agregaciones
    // =====================================================================

    /** 5. Estado dado, importe mayor al minimo y no anuladas (fechaAnulacion nula). */
    public List<FacturaVenta> facturasEmitidasNoAnuladas(String estado, double importeMinimo) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.estado = :estado "
                + "AND f.importeTotal > :importeMinimo "
                + "AND f.fechaAnulacion IS NULL";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("estado", estado)
                .setParameter("importeMinimo", importeMinimo)
                .getResultList();
    }

    /** 6. Clientes cuya denominacion contiene un texto (sin importar mayusculas) o cuyo CUIT/CUIL empieza con un prefijo. */
    public List<Cliente> buscarClientes(String textoParcial, String prefijoCuit) {
        String jpql = "SELECT c FROM Cliente c "
                + "WHERE LOWER(c.denominacion) LIKE LOWER(:texto) "
                + "OR c.cuitCuil LIKE :prefijo";
        return em.createQuery(jpql, Cliente.class)
                .setParameter("texto", "%" + textoParcial + "%")
                .setParameter("prefijo", prefijoCuit + "%")
                .getResultList();
    }

    /** 7. Estados distintos de las facturas, orden alfabetico ascendente. */
    public List<String> estadosDistintos() {
        String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
        return em.createQuery(jpql, String.class).getResultList();
    }

    /**
     * 8. Cantidad de facturas, suma de importes y promedio en un solo arreglo: [COUNT, SUM, AVG].
     * Se toman todas las facturas registradas (la consigna dice "facturas emitidas" en el sentido
     * de facturas generadas por el sistema, no del estado "EMITIDA").
     */
    public Object[] totalesFacturas() {
        String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getSingleResult();
    }

    /** 9. Puntos de venta cuyo numero esta en la lista recibida (ej. 1, 2, 5). */
    public List<PuntoVenta> puntosDeVentaPorNumeros(List<Integer> numeros) {
        String jpql = "SELECT pv FROM PuntoVenta pv WHERE pv.numero IN (:numeros) ORDER BY pv.numero";
        return em.createQuery(jpql, PuntoVenta.class)
                .setParameter("numeros", numeros)
                .getResultList();
    }

    // =====================================================================
    // Nivel 3: Navegacion de Entidades, JOINs y Subconsultas Simples
    // =====================================================================

    /** 10. Facturas cargadas por un usuario, navegando f.usuarioCarga.usuario (path expression). */
    public List<FacturaVenta> facturasPorUsuarioCarga(String nombreUsuario) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :usuario ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("usuario", nombreUsuario)
                .getResultList();
    }

    /** 11. Detalles de facturas emitidas por un punto de venta (INNER JOIN explicito). */
    public List<FacturaVentaDetalle> detallesPorPuntoVenta(int numeroPuntoVenta) {
        String jpql = "SELECT d FROM FacturaVentaDetalle d "
                + "INNER JOIN d.factura f "
                + "INNER JOIN f.puntoVenta pv "
                + "WHERE pv.numero = :numeroPv "
                + "ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVentaDetalle.class)
                .setParameter("numeroPv", numeroPuntoVenta)
                .getResultList();
    }

    /** 12. Denominacion de cada articulo y de su marca, incluyendo articulos sin marca (LEFT JOIN). Fila: [articulo, marca]. */
    public List<Object[]> articulosConMarca() {
        String jpql = "SELECT a.denominacion, m.denominacion FROM Articulo a "
                + "LEFT JOIN a.marca m "
                + "ORDER BY a.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 13. Facturas con al menos un detalle de un articulo de la marca dada (JOIN multinivel). */
    public List<FacturaVenta> facturasConMarca(String denominacionMarca) {
        String jpql = "SELECT DISTINCT f FROM FacturaVenta f "
                + "JOIN f.detalles d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "WHERE m.denominacion = :marca";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("marca", denominacionMarca)
                .getResultList();
    }

    /** 14. Facturas cuyo importe total es mayor al promedio de todas (subconsulta en WHERE). */
    public List<FacturaVenta> facturasSobrePromedio() {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2) "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    // =====================================================================
    // Nivel 4: Agrupamiento (GROUP BY) y Filtros de Grupo (HAVING)
    // =====================================================================

    /** 15. Por punto de venta: descripcion, cantidad de facturas y total facturado. Fila: [descripcion, COUNT, SUM]. */
    public List<Object[]> facturacionPorPuntoVenta() {
        String jpql = "SELECT pv.descripcion, COUNT(f), SUM(f.importeTotal) "
                + "FROM FacturaVenta f JOIN f.puntoVenta pv "
                + "GROUP BY pv.id, pv.descripcion "
                + "ORDER BY pv.descripcion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 16. Usuarios de carga con mas de N facturas registradas. Fila: [usuario, nombre, apellido, COUNT]. */
    public List<Object[]> usuariosConMasDeNFacturas(long minimo) {
        String jpql = "SELECT u.usuario, u.nombre, u.apellido, COUNT(f) "
                + "FROM FacturaVenta f JOIN f.usuarioCarga u "
                + "GROUP BY u.id, u.usuario, u.nombre, u.apellido "
                + "HAVING COUNT(f) > :minimo";
        return em.createQuery(jpql, Object[].class)
                .setParameter("minimo", minimo)
                .getResultList();
    }

    /** 17. Por marca: unidades vendidas (SUM cantidad) y subtotal acumulado. Fila: [marca, SUM cantidad, SUM subtotal]. */
    public List<Object[]> ventasPorMarca() {
        String jpql = "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) "
                + "FROM FacturaVentaDetalle d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "GROUP BY m.id, m.denominacion "
                + "ORDER BY m.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // =====================================================================
    // Nivel 5: Subconsultas Correlacionadas, EXISTS, NOT EXISTS y CASE WHEN
    // =====================================================================

    /** 18. Marcas con al menos un articulo facturado (EXISTS correlacionado con m). */
    public List<Marca> marcasConArticulosFacturados() {
        String jpql = "SELECT m FROM Marca m "
                + "WHERE EXISTS ("
                + "   SELECT d FROM FacturaVentaDetalle d "
                + "   WHERE d.listaPrecioArticulo.articulo.marca = m"
                + ") ORDER BY m.denominacion";
        return em.createQuery(jpql, Marca.class).getResultList();
    }

    /** 19. Articulos que nunca aparecieron en un detalle de factura (NOT EXISTS correlacionado con a). */
    public List<Articulo> articulosNuncaFacturados() {
        String jpql = "SELECT a FROM Articulo a "
                + "WHERE NOT EXISTS ("
                + "   SELECT d FROM FacturaVentaDetalle d "
                + "   WHERE d.listaPrecioArticulo.articulo = a"
                + ") ORDER BY a.denominacion";
        return em.createQuery(jpql, Articulo.class).getResultList();
    }

    /**
     * 20. Numero, importe total y categoria calculada con CASE WHEN, de mayor a menor importe.
     *     > limiteAlto              -> "ALTO VALOR"
     *     entre limiteBajo y limiteAlto (inclusive) -> "MEDIO VALOR"
     *     < limiteBajo              -> "BAJO VALOR"
     * Fila: [numero, importeTotal, categoria].
     */
    public List<Object[]> facturasPorCategoria(double limiteBajo, double limiteAlto) {
        String jpql = "SELECT f.numero, f.importeTotal, "
                + "CASE "
                + "   WHEN f.importeTotal > :limiteAlto THEN 'ALTO VALOR' "
                + "   WHEN f.importeTotal >= :limiteBajo THEN 'MEDIO VALOR' "
                + "   ELSE 'BAJO VALOR' "
                + "END "
                + "FROM FacturaVenta f "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, Object[].class)
                .setParameter("limiteAlto", limiteAlto)
                .setParameter("limiteBajo", limiteBajo)
                .getResultList();
    }
}
