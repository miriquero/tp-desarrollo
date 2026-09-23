package com.facturacion;

import com.facturacion.model.*;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class ConsultasJPQL {

    private final EntityManager em;

    public ConsultasJPQL(EntityManager em) {
        this.em = em;
    }

    // Nivel 1

    // 1 - todas las facturas
    public List<FacturaVenta> todasLasFacturas() {
        String jpql = "SELECT f FROM FacturaVenta f ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    // 2 - proyeccion
    public List<Object[]> proyeccionFacturas() {
        String jpql = "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f ORDER BY f.numero";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // 3 - where por rubro
    public List<Articulo> articulosPorRubro(String denominacionRubro) {
        String jpql = "SELECT a FROM Articulo a WHERE a.rubro.denominacion = :rubro";
        return em.createQuery(jpql, Articulo.class)
                .setParameter("rubro", denominacionRubro)
                .getResultList();
    }

    // 4 - between fechas
    public List<FacturaVenta> facturasEntreFechas(Date desde, Date hasta) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.fechaEmision BETWEEN :desde AND :hasta "
                + "ORDER BY f.fechaEmision";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    // Nivel 2

    // 5 - and / is null
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

    // 6 - like y lower
    public List<Cliente> buscarClientes(String textoParcial, String prefijoCuit) {
        String jpql = "SELECT c FROM Cliente c "
                + "WHERE LOWER(c.denominacion) LIKE LOWER(:texto) "
                + "OR c.cuitCuil LIKE :prefijo";
        return em.createQuery(jpql, Cliente.class)
                .setParameter("texto", "%" + textoParcial + "%")
                .setParameter("prefijo", prefijoCuit + "%")
                .getResultList();
    }

    // 7 - distinct y order by
    public List<String> estadosDistintos() {
        String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
        return em.createQuery(jpql, String.class).getResultList();
    }

    // 8 - count, sum, avg (tomo todas las facturas)
    public Object[] totalesFacturas() {
        String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getSingleResult();
    }

    // 9 - in
    public List<PuntoVenta> puntosDeVentaPorNumeros(List<Integer> numeros) {
        String jpql = "SELECT pv FROM PuntoVenta pv WHERE pv.numero IN (:numeros) ORDER BY pv.numero";
        return em.createQuery(jpql, PuntoVenta.class)
                .setParameter("numeros", numeros)
                .getResultList();
    }

    // Nivel 3

    // 10 - navegacion implicita
    public List<FacturaVenta> facturasPorUsuarioCarga(String nombreUsuario) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :usuario ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("usuario", nombreUsuario)
                .getResultList();
    }

    // 11 - inner join
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

    // 12 - left join
    public List<Object[]> articulosConMarca() {
        String jpql = "SELECT a.denominacion, m.denominacion FROM Articulo a "
                + "LEFT JOIN a.marca m "
                + "ORDER BY a.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // 13 - join multinivel
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

    // 14 - subconsulta en where
    public List<FacturaVenta> facturasSobrePromedio() {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2) "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    // Nivel 4

    // 15 - group by
    public List<Object[]> facturacionPorPuntoVenta() {
        String jpql = "SELECT pv.descripcion, COUNT(f), SUM(f.importeTotal) "
                + "FROM FacturaVenta f JOIN f.puntoVenta pv "
                + "GROUP BY pv.id, pv.descripcion "
                + "ORDER BY pv.descripcion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // 16 - having
    public List<Object[]> usuariosConMasDeNFacturas(long minimo) {
        String jpql = "SELECT u.usuario, u.nombre, u.apellido, COUNT(f) "
                + "FROM FacturaVenta f JOIN f.usuarioCarga u "
                + "GROUP BY u.id, u.usuario, u.nombre, u.apellido "
                + "HAVING COUNT(f) > :minimo";
        return em.createQuery(jpql, Object[].class)
                .setParameter("minimo", minimo)
                .getResultList();
    }

    // 17 - group by por marca
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

    // Nivel 5

    // 18 - exists
    public List<Marca> marcasConArticulosFacturados() {
        String jpql = "SELECT m FROM Marca m "
                + "WHERE EXISTS ("
                + "   SELECT d FROM FacturaVentaDetalle d "
                + "   WHERE d.listaPrecioArticulo.articulo.marca = m"
                + ") ORDER BY m.denominacion";
        return em.createQuery(jpql, Marca.class).getResultList();
    }

    // 19 - not exists
    public List<Articulo> articulosNuncaFacturados() {
        String jpql = "SELECT a FROM Articulo a "
                + "WHERE NOT EXISTS ("
                + "   SELECT d FROM FacturaVentaDetalle d "
                + "   WHERE d.listaPrecioArticulo.articulo = a"
                + ") ORDER BY a.denominacion";
        return em.createQuery(jpql, Articulo.class).getResultList();
    }

    // 20 - case when (50000 justo cuenta como medio)
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
