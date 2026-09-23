package com.facturacion;

import com.facturacion.model.Articulo;
import com.facturacion.model.Cliente;
import com.facturacion.model.CondicionIva;
import com.facturacion.model.Contacto;
import com.facturacion.model.Domicilio;
import com.facturacion.model.FacturaVenta;
import com.facturacion.model.FacturaVentaDetalle;
import com.facturacion.model.ListaPrecio;
import com.facturacion.model.ListaPrecioArticulo;
import com.facturacion.model.Marca;
import com.facturacion.model.PuntoVenta;
import com.facturacion.model.Rubro;
import com.facturacion.model.TipoMoneda;
import com.facturacion.model.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Date ahora = new Date();

            // datos maestros, no dependen de la factura asi que los persistimos aparte

            Usuario usuario = new Usuario();
            usuario.setUsuario("admin");
            usuario.setClave("admin123");
            usuario.setNombre("Juan");
            usuario.setApellido("Perez");
            em.persist(usuario);

            PuntoVenta puntoVenta = new PuntoVenta();
            puntoVenta.setNumero(1);
            puntoVenta.setDescripcion("Casa Central");
            puntoVenta.setTipoEmision("CAE");
            puntoVenta.setDomicilioComercial("Av. Siempre Viva 123");
            puntoVenta.setFechaAlta(ahora);
            puntoVenta.setFechaModificacion(ahora);
            puntoVenta.setUsuarioCarga(usuario);
            puntoVenta.setUsuarioModificacion(usuario);
            em.persist(puntoVenta);

            Rubro rubro = new Rubro();
            rubro.setDenominacion("Ferreteria");
            rubro.setCodigo(10);
            rubro.setFechaAlta(ahora);
            rubro.setFechaModificacion(ahora);
            rubro.setUsuarioCarga(usuario);
            rubro.setUsuarioModificacion(usuario);
            em.persist(rubro);

            Marca marca = new Marca();
            marca.setDenominacion("Genérica");
            marca.setCodigo(100);
            marca.setFechaAlta(ahora);
            marca.setFechaModificacion(ahora);
            marca.setUsuarioCarga(usuario);
            marca.setUsuarioModificacion(usuario);
            em.persist(marca);

            Articulo articulo = new Articulo();
            articulo.setCodigo("ART-001");
            articulo.setDenominacion("Martillo");
            articulo.setRubro(rubro);
            articulo.setMarca(marca);
            articulo.setFechaAlta(ahora);
            articulo.setFechaModificacion(ahora);
            articulo.setUsuarioCarga(usuario);
            articulo.setUsuarioModificacion(usuario);
            em.persist(articulo);

            ListaPrecio listaPrecio = new ListaPrecio();
            listaPrecio.setCodigo("LP-01");
            listaPrecio.setDenominacion("Lista General");
            listaPrecio.setFechaAlta(ahora);
            listaPrecio.setFechaModificacion(ahora);
            listaPrecio.setUsuarioCarga(usuario);
            listaPrecio.setUsuarioModificacion(usuario);
            em.persist(listaPrecio);

            ListaPrecioArticulo listaPrecioArticulo = new ListaPrecioArticulo();
            listaPrecioArticulo.setListaPrecio(listaPrecio);
            listaPrecioArticulo.setArticulo(articulo);
            listaPrecioArticulo.setPrecioVenta(1500.0);
            listaPrecioArticulo.setFechaAlta(ahora);
            listaPrecioArticulo.setFechaModificacion(ahora);
            listaPrecioArticulo.setUsuarioCarga(usuario);
            listaPrecioArticulo.setUsuarioModificacion(usuario);
            em.persist(listaPrecioArticulo);

            CondicionIva condicionIva = new CondicionIva();
            condicionIva.setCodigoAfip(5);
            condicionIva.setDenominacion("Consumidor Final");
            condicionIva.setFechaAlta(ahora);
            condicionIva.setFechaModificacion(ahora);
            condicionIva.setUsuarioCarga(usuario);
            condicionIva.setUsuarioModificacion(usuario);
            em.persist(condicionIva);

            TipoMoneda tipoMoneda = new TipoMoneda();
            tipoMoneda.setCodigoAfip("PES");
            tipoMoneda.setDenominacion("Peso Argentino");
            tipoMoneda.setSimbolo("$");
            tipoMoneda.setFechaAlta(ahora);
            tipoMoneda.setFechaModificacion(ahora);
            tipoMoneda.setUsuarioCarga(usuario);
            tipoMoneda.setUsuarioModificacion(usuario);
            em.persist(tipoMoneda);

            // el contacto y el domicilio no hace falta persistirlos aparte, se van con el cliente por la cascada del OneToOne
            Contacto contacto = new Contacto();
            contacto.setEmail("cliente@example.com");
            contacto.setTelefono("261-4000000");
            contacto.setCelular("261-15000000");

            Domicilio domicilio = new Domicilio();
            domicilio.setNombreCalle("San Martin");
            domicilio.setNumeroCalle("950");

            Cliente cliente = new Cliente();
            cliente.setCuitCuil("20-12345678-9");
            cliente.setDenominacion("Consumidor Final SA");
            cliente.setContacto(contacto);
            cliente.setDomicilio(domicilio);
            cliente.setFechaAlta(ahora);
            cliente.setFechaModificacion(ahora);
            cliente.setUsuarioCarga(usuario);
            cliente.setUsuarioModificacion(usuario);
            em.persist(cliente);

            // --- Cabecera de FacturaVenta ---

            FacturaVenta facturaVenta = new FacturaVenta();
            facturaVenta.setNumero(1L);
            facturaVenta.setFechaEmision(ahora);
            facturaVenta.setPuntoVenta(puntoVenta);
            facturaVenta.setCliente(cliente);
            facturaVenta.setCondicionIva(condicionIva);
            facturaVenta.setTipoMoneda(tipoMoneda);
            facturaVenta.setImporteTotal(3000.0);
            facturaVenta.setImporteCobrado(3000.0);
            facturaVenta.setImporteSaldo(0.0);
            facturaVenta.setEstado("EMITIDA");
            facturaVenta.setFechaAlta(ahora);
            facturaVenta.setFechaModificacion(ahora);
            facturaVenta.setUsuarioCarga(usuario);
            facturaVenta.setUsuarioModificacion(usuario);

            FacturaVentaDetalle detalle1 = new FacturaVentaDetalle();
            detalle1.setListaPrecioArticulo(listaPrecioArticulo);
            detalle1.setDescripcion("Martillo x2");
            detalle1.setCantidad(2);
            detalle1.setPrecioUnitario(1500.0);
            detalle1.setImporteNeto(2479.34);
            detalle1.setImporteIva(520.66);
            detalle1.setImporteSubtotal(3000.0);
            facturaVenta.agregarDetalle(detalle1);

            // ojo: persistimos SOLO la cabecera. el detalle entra solo por el cascade = ALL del OneToMany
            em.persist(facturaVenta);

            em.getTransaction().commit();

            System.out.println("Factura Nro " + facturaVenta.getNumero()
                    + " persistida con id=" + facturaVenta.getId()
                    + " junto con " + facturaVenta.getDetalles().size() + " detalle(s) en cascada.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }
}
