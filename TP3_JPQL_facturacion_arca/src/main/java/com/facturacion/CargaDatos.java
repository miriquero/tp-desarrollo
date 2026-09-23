package com.facturacion;

import com.facturacion.model.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

// datos de prueba para las consultas. si ya estan cargados no hace nada
public class CargaDatos {

    private final EntityManager em;
    private Usuario admin;
    private final Date ahora = new Date();

    public CargaDatos(EntityManager em) {
        this.em = em;
    }

    public void cargarSiHaceFalta() {
        Long existe = em.createQuery(
                        "SELECT COUNT(u) FROM Usuario u WHERE u.usuario = :usuario", Long.class)
                .setParameter("usuario", "mgomez")
                .getSingleResult();
        if (existe > 0) {
            System.out.println("Datos de prueba ya cargados, no se vuelven a insertar.");
            return;
        }

        em.getTransaction().begin();
        try {
            cargar();
            em.getTransaction().commit();
            System.out.println("Datos de prueba cargados.");
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    private void cargar() {
        // usuarios
        admin = usuario("sistema", "Usuario", "Sistema");
        Usuario mgomez = usuario("mgomez", "Mariana", "Gomez");
        Usuario lperez = usuario("lperez", "Lucas", "Perez");

        // puntos de venta
        PuntoVenta pv1 = puntoVenta(1, "Casa Central");
        PuntoVenta pv2 = puntoVenta(2, "Sucursal Godoy Cruz");
        PuntoVenta pv3 = puntoVenta(3, "Venta Online");
        PuntoVenta pv5 = puntoVenta(5, "Sucursal Maipu");

        // rubros y marcas
        Rubro electronica = rubro("Electrónica", 1);
        Rubro ferreteria = rubro("Ferretería", 2);

        Marca samsung = marca("Samsung", 10);
        Marca philips = marca("Philips", 20);
        Marca stanley = marca("Stanley", 30);
        Marca bosch = marca("Bosch", 40); // tiene articulo pero nunca se vende

        // articulos
        Articulo tv = articulo("E-001", "Smart TV 50\"", electronica, samsung);
        Articulo celular = articulo("E-002", "Celular Galaxy A55", electronica, samsung);
        Articulo lampara = articulo("E-003", "Lámpara LED 12W", electronica, philips);
        Articulo auriculares = articulo("E-004", "Auriculares Bluetooth genéricos", electronica, null); // sin marca
        Articulo martillo = articulo("F-001", "Martillo carpintero", ferreteria, stanley);
        articulo("F-002", "Taladro percutor", ferreteria, bosch); // nunca vendido
        articulo("F-003", "Destornillador plano", ferreteria, null); // sin marca y nunca vendido

        // lista de precios
        ListaPrecio lista = new ListaPrecio();
        lista.setCodigo("LP-01");
        lista.setDenominacion("Lista General");
        auditar(lista);
        em.persist(lista);

        ListaPrecioArticulo pTv = precio(lista, tv, 85000);
        ListaPrecioArticulo pCelular = precio(lista, celular, 42000);
        ListaPrecioArticulo pLampara = precio(lista, lampara, 3500);
        ListaPrecioArticulo pAuriculares = precio(lista, auriculares, 8000);
        ListaPrecioArticulo pMartillo = precio(lista, martillo, 6000);

        // condicion iva y moneda
        CondicionIva consumidorFinal = condicionIva(5, "Consumidor Final");
        CondicionIva responsableInscripto = condicionIva(1, "IVA Responsable Inscripto");

        TipoMoneda pesos = new TipoMoneda();
        pesos.setCodigoAfip("PES");
        pesos.setDenominacion("Peso Argentino");
        pesos.setSimbolo("$");
        auditar(pesos);
        em.persist(pesos);

        // clientes
        Cliente cf = cliente("20-00000000-1", "Consumidor Final", "San Martin", "100");
        Cliente andes = cliente("30-71234567-8", "Distribuidora Andes SRL", "Las Heras", "450");
        Cliente juan = cliente("20-31456789-5", "Juan Martínez", "Belgrano", "1220");
        Cliente maria = cliente("27-28987654-3", "María López", "Colón", "75");

        // facturas
        // mgomez: 7 facturas
        factura(1L, fecha(2026, 8, 3), pv1, cf, consumidorFinal, pesos, mgomez, "EMITIDA", null,
                item(pMartillo, 1));
        factura(2L, fecha(2026, 8, 10), pv1, andes, responsableInscripto, pesos, mgomez, "EMITIDA", null,
                item(pTv, 1));
        factura(3L, fecha(2026, 8, 15), pv2, juan, consumidorFinal, pesos, mgomez, "EMITIDA", null,
                item(pCelular, 1), item(pAuriculares, 1));
        factura(4L, fecha(2026, 8, 20), pv2, maria, consumidorFinal, pesos, mgomez, "ANULADA", fecha(2026, 8, 21),
                item(pLampara, 4));
        factura(5L, fecha(2026, 8, 28), pv5, andes, responsableInscripto, pesos, mgomez, "EMITIDA", null,
                item(pTv, 2), item(pLampara, 2));
        factura(6L, fecha(2026, 9, 2), pv1, cf, consumidorFinal, pesos, mgomez, "EMITIDA", null,
                item(pAuriculares, 1));
        factura(7L, fecha(2026, 9, 8), pv5, juan, consumidorFinal, pesos, mgomez, "EMITIDA", null,
                item(pCelular, 1));
        // lperez: 3 facturas
        factura(8L, fecha(2026, 9, 12), pv2, maria, consumidorFinal, pesos, lperez, "EMITIDA", null,
                item(pLampara, 3), item(pMartillo, 1));
        factura(9L, fecha(2026, 9, 15), pv3, andes, responsableInscripto, pesos, lperez, "PENDIENTE", null,
                item(pMartillo, 5));
        factura(10L, fecha(2026, 9, 20), pv1, juan, consumidorFinal, pesos, lperez, "ANULADA", fecha(2026, 9, 20),
                item(pAuriculares, 1));
    }

    private static class Item {
        private final ListaPrecioArticulo precio;
        private final double cantidad;

        Item(ListaPrecioArticulo precio, double cantidad) {
            this.precio = precio;
            this.cantidad = cantidad;
        }

        ListaPrecioArticulo precio() { return precio; }
        double cantidad() { return cantidad; }
    }

    private static Item item(ListaPrecioArticulo precio, double cantidad) {
        return new Item(precio, cantidad);
    }

    private static Date fecha(int anio, int mes, int dia) {
        return Date.from(LocalDate.of(anio, mes, dia).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void auditar(AuditoriaApp e) {
        e.setFechaAlta(ahora);
        e.setFechaModificacion(ahora);
        e.setUsuarioCarga(admin);
        e.setUsuarioModificacion(admin);
    }

    private Usuario usuario(String user, String nombre, String apellido) {
        Usuario u = new Usuario();
        u.setUsuario(user);
        u.setClave(user + "123");
        u.setNombre(nombre);
        u.setApellido(apellido);
        em.persist(u);
        return u;
    }

    private PuntoVenta puntoVenta(int numero, String descripcion) {
        PuntoVenta pv = new PuntoVenta();
        pv.setNumero(numero);
        pv.setDescripcion(descripcion);
        pv.setTipoEmision("CAE");
        pv.setDomicilioComercial("Mendoza");
        auditar(pv);
        em.persist(pv);
        return pv;
    }

    private Rubro rubro(String denominacion, int codigo) {
        Rubro r = new Rubro();
        r.setDenominacion(denominacion);
        r.setCodigo(codigo);
        auditar(r);
        em.persist(r);
        return r;
    }

    private Marca marca(String denominacion, int codigo) {
        Marca m = new Marca();
        m.setDenominacion(denominacion);
        m.setCodigo(codigo);
        auditar(m);
        em.persist(m);
        return m;
    }

    private Articulo articulo(String codigo, String denominacion, Rubro rubro, Marca marca) {
        Articulo a = new Articulo();
        a.setCodigo(codigo);
        a.setDenominacion(denominacion);
        a.setRubro(rubro);
        a.setMarca(marca);
        auditar(a);
        em.persist(a);
        return a;
    }

    private ListaPrecioArticulo precio(ListaPrecio lista, Articulo articulo, double precioVenta) {
        ListaPrecioArticulo lpa = new ListaPrecioArticulo();
        lpa.setListaPrecio(lista);
        lpa.setArticulo(articulo);
        lpa.setPrecioVenta(precioVenta);
        auditar(lpa);
        em.persist(lpa);
        return lpa;
    }

    private CondicionIva condicionIva(int codigoAfip, String denominacion) {
        CondicionIva c = new CondicionIva();
        c.setCodigoAfip(codigoAfip);
        c.setDenominacion(denominacion);
        auditar(c);
        em.persist(c);
        return c;
    }

    private Cliente cliente(String cuit, String denominacion, String calle, String numero) {
        Contacto contacto = new Contacto();
        contacto.setEmail(denominacion.toLowerCase().replaceAll("[^a-z]", "") + "@mail.com");
        contacto.setTelefono("261-4000000");

        Domicilio domicilio = new Domicilio();
        domicilio.setNombreCalle(calle);
        domicilio.setNumeroCalle(numero);

        Cliente c = new Cliente();
        c.setCuitCuil(cuit);
        c.setDenominacion(denominacion);
        c.setContacto(contacto);
        c.setDomicilio(domicilio);
        auditar(c);
        em.persist(c);
        return c;
    }

    private void factura(Long numero, Date fechaEmision, PuntoVenta pv, Cliente cliente,
                         CondicionIva condicionIva, TipoMoneda moneda, Usuario usuarioCarga,
                         String estado, Date fechaAnulacion, Item... items) {
        FacturaVenta f = new FacturaVenta();
        f.setNumero(numero);
        f.setFechaEmision(fechaEmision);
        f.setPuntoVenta(pv);
        f.setCliente(cliente);
        f.setCondicionIva(condicionIva);
        f.setTipoMoneda(moneda);
        f.setEstado(estado);
        f.setFechaAnulacion(fechaAnulacion);

        double total = 0;
        for (Item it : items) {
            double precioUnitario = it.precio().getPrecioVenta();
            double subtotal = precioUnitario * it.cantidad();
            double neto = Math.round(subtotal / 1.21 * 100) / 100.0;

            FacturaVentaDetalle d = new FacturaVentaDetalle();
            d.setListaPrecioArticulo(it.precio());
            d.setDescripcion(it.precio().getArticulo().getDenominacion());
            d.setCantidad(it.cantidad());
            d.setPrecioUnitario(precioUnitario);
            d.setPorcentajeBonificacion(0);
            d.setImporteNeto(neto);
            d.setImporteIva(Math.round((subtotal - neto) * 100) / 100.0);
            d.setImporteSubtotal(subtotal);
            f.agregarDetalle(d);
            total += subtotal;
        }
        f.setImporteTotal(total);
        boolean cobrada = "EMITIDA".equals(estado);
        f.setImporteCobrado(cobrada ? total : 0);
        f.setImporteSaldo(cobrada ? 0 : total);

        f.setFechaAlta(fechaEmision);
        f.setFechaModificacion(fechaEmision);
        f.setUsuarioCarga(usuarioCarga);
        f.setUsuarioModificacion(usuarioCarga);
        em.persist(f); // los detalles se guardan por cascada
    }
}
