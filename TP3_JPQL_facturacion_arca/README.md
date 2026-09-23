# facturacion-arca — TP Grupal: Consultas Avanzadas con JPQL

Continuación del TP de ORM/JPA. El modelo de entidades es el mismo, no se modificó.
Se agregan las 20 consultas JPQL del enunciado, una carga de datos de prueba y un `Main` que las ejecuta.

## Cómo correrlo

1. Tener PostgreSQL corriendo y crear la base (una sola vez):
   ```sql
   CREATE DATABASE facturacion_arca;
   ```
2. Si tu usuario/contraseña de Postgres no son `postgres` / `postgres`, cambiarlos en
   `src/main/resources/META-INF/persistence.xml`.
3. Desde la raíz del proyecto:
   ```
   mvn compile exec:java
   ```
   (o correr `com.facturacion.MainJPQL` desde IntelliJ).

Hibernate crea las tablas solo (`hbm2ddl.auto=update`). La primera vez se cargan los datos de prueba;
las siguientes veces detecta que ya están y no los duplica.

## Archivos nuevos

- `ConsultasJPQL.java`: las 20 consultas, un método por consigna. Todas con
  `em.createQuery(jpql, Clase.class)` y parámetros con nombre (`:param`).
- `CargaDatos.java`: datos de prueba armados para que cada consulta devuelva algo
  (usuario con más de 5 facturas, artículos sin marca, artículos nunca vendidos,
  facturas anuladas, importes de las tres categorías, etc.).
- `MainJPQL.java`: ejecuta las 20 consultas e imprime los resultados.
- `Main.java` (del TP anterior) quedó igual.

## Consultas

| # | Tema | Método |
|---|------|--------|
| 1 | Entidades completas | `todasLasFacturas()` |
| 2 | Proyección | `proyeccionFacturas()` |
| 3 | WHERE igualdad | `articulosPorRubro(rubro)` |
| 4 | BETWEEN fechas | `facturasEntreFechas(desde, hasta)` |
| 5 | AND + IS NULL | `facturasEmitidasNoAnuladas(estado, importeMinimo)` |
| 6 | LIKE + LOWER + OR | `buscarClientes(texto, prefijoCuit)` |
| 7 | DISTINCT + ORDER BY | `estadosDistintos()` |
| 8 | COUNT / SUM / AVG | `totalesFacturas()` |
| 9 | IN | `puntosDeVentaPorNumeros(lista)` |
| 10 | Path expression | `facturasPorUsuarioCarga(usuario)` |
| 11 | INNER JOIN | `detallesPorPuntoVenta(numeroPv)` |
| 12 | LEFT JOIN | `articulosConMarca()` |
| 13 | JOIN multinivel | `facturasConMarca(marca)` |
| 14 | Subconsulta en WHERE | `facturasSobrePromedio()` |
| 15 | GROUP BY | `facturacionPorPuntoVenta()` |
| 16 | HAVING | `usuariosConMasDeNFacturas(n)` |
| 17 | GROUP BY sobre relaciones | `ventasPorMarca()` |
| 18 | EXISTS | `marcasConArticulosFacturados()` |
| 19 | NOT EXISTS | `articulosNuncaFacturados()` |
| 20 | CASE WHEN | `facturasPorCategoria(limiteBajo, limiteAlto)` |

## Decisiones tomadas

- `FacturaVentaDetalle` no apunta directo a `Articulo`: el camino es
  `detalle.listaPrecioArticulo.articulo` (y de ahí `.marca`). Por eso las consultas 13, 17, 18 y 19 navegan por ahí.
- Consultas 8 y 15: "facturas emitidas" se tomó como todas las facturas registradas, no solo las de estado `EMITIDA`.
- Consulta 20: exactamente $50.000 cuenta como MEDIO VALOR ("entre $10.000 y $50.000", inclusive).
- Las consultas que devuelven columnas sueltas usan `Object[].class` (o `String.class` si es una sola columna).
