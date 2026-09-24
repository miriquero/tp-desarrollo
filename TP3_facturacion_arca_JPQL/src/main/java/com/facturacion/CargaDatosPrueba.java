package com.facturacion;

import com.facturacion.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;

/** Carga una sola vez los datos necesarios para demostrar las consultas JPQL. */
public class CargaDatosPrueba {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();
        try {
            Long cantidadFacturas = em.createQuery("SELECT COUNT(f) FROM FacturaVenta f", Long.class)
                    .getSingleResult();
            if (cantidadFacturas > 0) {
                System.out.println("Ya existen facturas. No se cargaron datos duplicados.");
                return;
            }

            em.getTransaction().begin();
            Date ahora = new Date();
            Usuario admin = usuario("admin", "Juan", "Perez");
            Usuario ana = usuario("ana", "Ana", "Lopez");
            em.persist(admin);
            em.persist(ana);

            PuntoVenta centro = puntoVenta(1, "Casa Central", admin, ahora);
            PuntoVenta norte = puntoVenta(2, "Sucursal Norte", admin, ahora);
            PuntoVenta online = puntoVenta(5, "Tienda Online", ana, ahora);
            em.persist(centro); em.persist(norte); em.persist(online);

            Rubro ferreteria = rubro("Ferretería", 10, admin, ahora);
            Rubro electronica = rubro("Electrónica", 20, admin, ahora);
            em.persist(ferreteria); em.persist(electronica);
            Marca acme = marca("Acme", 100, admin, ahora);
            Marca tecno = marca("TecnoSur", 200, admin, ahora);
            em.persist(acme); em.persist(tecno);

            Articulo martillo = articulo("ART-001", "Martillo", ferreteria, acme, admin, ahora);
            Articulo televisor = articulo("ART-002", "Televisor", electronica, tecno, admin, ahora);
            Articulo cable = articulo("ART-003", "Cable HDMI", electronica, null, admin, ahora);
            em.persist(martillo); em.persist(televisor); em.persist(cable);

            ListaPrecio lista = listaPrecio(admin, ahora);
            em.persist(lista);
            ListaPrecioArticulo precioMartillo = precio(lista, martillo, 3000, admin, ahora);
            ListaPrecioArticulo precioTelevisor = precio(lista, televisor, 60000, admin, ahora);
            ListaPrecioArticulo precioCable = precio(lista, cable, 8000, admin, ahora);
            em.persist(precioMartillo); em.persist(precioTelevisor); em.persist(precioCable);

            Cliente juan = cliente("Juan Servicios", "20-12345678-9", "juan@ejemplo.com", admin, ahora, em);
            Cliente maria = cliente("María Electrónica", "27-87654321-0", "maria@ejemplo.com", ana, ahora, em);
            CondicionIva condicion = condicionIva(admin, ahora);
            TipoMoneda moneda = moneda(admin, ahora);
            em.persist(condicion); em.persist(moneda);

            factura(em, 1, 60, juan, condicion, moneda, centro, admin, "EMITIDA", null, precioMartillo, 1, 3000, ahora);
            factura(em, 2, 50, juan, condicion, moneda, centro, admin, "EMITIDA", null, precioTelevisor, 1, 12000, ahora);
            factura(em, 3, 40, maria, condicion, moneda, norte, admin, "EMITIDA", null, precioTelevisor, 1, 60000, ahora);
            factura(em, 4, 30, juan, condicion, moneda, norte, admin, "ANULADA", fechaHaceDias(29), precioMartillo, 2, 25000, ahora);
            factura(em, 5, 20, maria, condicion, moneda, online, admin, "EMITIDA", null, precioMartillo, 2, 8000, ahora);
            factura(em, 6, 10, juan, condicion, moneda, centro, admin, "EMITIDA", null, precioMartillo, 5, 15000, ahora);
            factura(em, 7, 5, maria, condicion, moneda, online, ana, "EMITIDA", null, precioTelevisor, 1, 52000, ahora);

            em.getTransaction().commit();
            System.out.println("Datos de prueba cargados correctamente. Ahora ejecuta Main.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

    private static Usuario usuario(String usuario, String nombre, String apellido) {
        Usuario u = new Usuario();
        u.setUsuario(usuario); u.setClave("1234"); u.setNombre(nombre); u.setApellido(apellido);
        return u;
    }

    private static PuntoVenta puntoVenta(int numero, String descripcion, Usuario usuario, Date fecha) {
        PuntoVenta p = new PuntoVenta();
        p.setNumero(numero); p.setDescripcion(descripcion); p.setTipoEmision("CAE"); p.setDomicilioComercial("Domicilio " + numero);
        auditar(p, usuario, fecha); return p;
    }

    private static Rubro rubro(String denominacion, int codigo, Usuario usuario, Date fecha) {
        Rubro r = new Rubro(); r.setDenominacion(denominacion); r.setCodigo(codigo); auditar(r, usuario, fecha); return r;
    }

    private static Marca marca(String denominacion, int codigo, Usuario usuario, Date fecha) {
        Marca m = new Marca(); m.setDenominacion(denominacion); m.setCodigo(codigo); auditar(m, usuario, fecha); return m;
    }

    private static Articulo articulo(String codigo, String denominacion, Rubro rubro, Marca marca, Usuario usuario, Date fecha) {
        Articulo a = new Articulo();
        a.setCodigo(codigo); a.setDenominacion(denominacion); a.setRubro(rubro); a.setMarca(marca); auditar(a, usuario, fecha);
        return a;
    }

    private static ListaPrecio listaPrecio(Usuario usuario, Date fecha) {
        ListaPrecio l = new ListaPrecio(); l.setCodigo("GENERAL"); l.setDenominacion("Lista general"); auditar(l, usuario, fecha); return l;
    }

    private static ListaPrecioArticulo precio(ListaPrecio lista, Articulo articulo, double valor, Usuario usuario, Date fecha) {
        ListaPrecioArticulo p = new ListaPrecioArticulo();
        p.setListaPrecio(lista); p.setArticulo(articulo); p.setPrecioVenta(valor); auditar(p, usuario, fecha); return p;
    }

    private static Cliente cliente(String denominacion, String cuit, String email, Usuario usuario, Date fecha, EntityManager em) {
        Contacto contacto = new Contacto(); contacto.setEmail(email); contacto.setTelefono("2615550000"); contacto.setCelular("2615551111"); em.persist(contacto);
        Domicilio domicilio = new Domicilio(); domicilio.setNombreCalle("San Martin"); domicilio.setNumeroCalle("100"); em.persist(domicilio);
        Cliente c = new Cliente();
        c.setDenominacion(denominacion); c.setCuitCuil(cuit); c.setContacto(contacto); c.setDomicilio(domicilio); auditar(c, usuario, fecha); em.persist(c);
        return c;
    }

    private static CondicionIva condicionIva(Usuario usuario, Date fecha) {
        CondicionIva c = new CondicionIva(); c.setCodigoAfip(5); c.setDenominacion("Consumidor Final"); auditar(c, usuario, fecha); return c;
    }

    private static TipoMoneda moneda(Usuario usuario, Date fecha) {
        TipoMoneda m = new TipoMoneda(); m.setCodigoAfip("PES"); m.setDenominacion("Peso argentino"); m.setSimbolo("$"); auditar(m, usuario, fecha); return m;
    }

    private static void factura(EntityManager em, long numero, int haceDias, Cliente cliente, CondicionIva condicion,
                                TipoMoneda moneda, PuntoVenta puntoVenta, Usuario usuario, String estado,
                                Date fechaAnulacion, ListaPrecioArticulo precio, double cantidad, double total, Date fechaAuditoria) {
        FacturaVenta f = new FacturaVenta();
        f.setNumero(numero); f.setFechaEmision(fechaHaceDias(haceDias)); f.setCliente(cliente); f.setCondicionIva(condicion); f.setTipoMoneda(moneda); f.setPuntoVenta(puntoVenta);
        f.setImporteTotal(total); f.setImporteCobrado(estado.equals("EMITIDA") ? total : 0); f.setImporteSaldo(estado.equals("EMITIDA") ? 0 : total);
        f.setEstado(estado); f.setFechaAnulacion(fechaAnulacion); auditar(f, usuario, fechaAuditoria);
        FacturaVentaDetalle d = new FacturaVentaDetalle();
        d.setListaPrecioArticulo(precio); d.setDescripcion(precio.getArticulo().getDenominacion()); d.setCantidad(cantidad); d.setPrecioUnitario(total / cantidad);
        d.setImporteNeto(total / 1.21); d.setImporteIva(total - d.getImporteNeto()); d.setImporteSubtotal(total); f.agregarDetalle(d);
        em.persist(f);
    }

    private static void auditar(AuditoriaApp entidad, Usuario usuario, Date fecha) {
        entidad.setFechaAlta(fecha); entidad.setFechaModificacion(fecha); entidad.setUsuarioCarga(usuario); entidad.setUsuarioModificacion(usuario);
    }

    private static Date fechaHaceDias(int dias) {
        Calendar calendar = Calendar.getInstance(); calendar.add(Calendar.DAY_OF_YEAR, -dias); return calendar.getTime();
    }
}
