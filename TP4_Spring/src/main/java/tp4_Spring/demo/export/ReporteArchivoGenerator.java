package tp4_Spring.demo.export;

import com.facturacion.dto.FacturaReporteDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.List;

/** Crea los archivos descargables a partir de los DTOs del reporte. */
public final class ReporteArchivoGenerator {

    private static final String[] ENCABEZADOS = {
            "Numero", "Fecha", "Cliente", "Condicion IVA", "Punto de venta", "Importe total", "Cantidad de items"
    };
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");
    private static final PDFont FUENTE_NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FUENTE_NEGRITA = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private ReporteArchivoGenerator() {
    }

    public static byte[] generarExcel(List<FacturaReporteDTO> reportes) throws IOException {
        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Facturas");
            CellStyle encabezado = estiloEncabezado(libro);
            CellStyle fecha = libro.createCellStyle();
            fecha.setDataFormat(libro.createDataFormat().getFormat("dd/mm/yyyy"));
            CellStyle importe = libro.createCellStyle();
            importe.setDataFormat(libro.createDataFormat().getFormat("$#,##0.00"));

            Row filaEncabezado = hoja.createRow(0);
            for (int columna = 0; columna < ENCABEZADOS.length; columna++) {
                Cell celda = filaEncabezado.createCell(columna);
                celda.setCellValue(ENCABEZADOS[columna]);
                celda.setCellStyle(encabezado);
            }

            int numeroFila = 1;
            for (FacturaReporteDTO reporte : reportes) {
                Row fila = hoja.createRow(numeroFila++);
                crearCelda(fila, 0, reporte.getNumeroFactura(), null);
                crearCelda(fila, 1, reporte.getFechaEmision(), fecha);
                crearCelda(fila, 2, reporte.getClienteDenominacion(), null);
                crearCelda(fila, 3, reporte.getCondicionIva(), null);
                crearCelda(fila, 4, reporte.getPuntoVentaDescripcion(), null);
                crearCelda(fila, 5, reporte.getImporteTotal(), importe);
                crearCelda(fila, 6, reporte.getCantidadItems(), null);
            }

            for (int columna = 0; columna < ENCABEZADOS.length; columna++) {
                hoja.autoSizeColumn(columna);
            }
            libro.write(salida);
            return salida.toByteArray();
        }
    }

    public static byte[] generarPdf(List<FacturaReporteDTO> reportes) throws IOException {
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PaginaPdf pagina = crearPagina(documento);
            for (FacturaReporteDTO reporte : reportes) {
                if (pagina.filaActual >= 27) {
                    pagina.cerrar();
                    pagina = crearPagina(documento);
                }
                dibujarFila(pagina, reporte);
                pagina.filaActual++;
            }
            pagina.cerrar();
            documento.save(salida);
            return salida.toByteArray();
        }
    }

    private static CellStyle estiloEncabezado(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        return estilo;
    }

    private static void crearCelda(Row fila, int columna, Object valor, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        if (valor instanceof Number numero) {
            celda.setCellValue(numero.doubleValue());
        } else if (valor instanceof java.util.Date fecha) {
            celda.setCellValue(fecha);
        } else {
            celda.setCellValue(valor == null ? "" : valor.toString());
        }
        if (estilo != null) {
            celda.setCellStyle(estilo);
        }
    }

    private static PaginaPdf crearPagina(PDDocument documento) throws IOException {
        PDPage pagina = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        documento.addPage(pagina);
        PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
        escribirTexto(contenido, "Reporte de facturas", 40, 555, 16, FUENTE_NEGRITA);
        escribirTexto(contenido, String.format("%-9s %-12s %-24s %-16s %-18s %12s %8s",
                "Numero", "Fecha", "Cliente", "IVA", "Punto venta", "Total", "Items"),
                40, 530, 8, FUENTE_NEGRITA);
        return new PaginaPdf(contenido);
    }

    private static void dibujarFila(PaginaPdf pagina, FacturaReporteDTO reporte) throws IOException {
        String fecha = reporte.getFechaEmision() == null ? "" : FORMATO_FECHA.format(reporte.getFechaEmision());
        String fila = String.format("%-9s %-12s %-24s %-16s %-18s %12.2f %8d",
                textoPdf(reporte.getNumeroFactura()), textoPdf(fecha),
                recortar(textoPdf(reporte.getClienteDenominacion()), 23),
                recortar(textoPdf(reporte.getCondicionIva()), 15),
                recortar(textoPdf(reporte.getPuntoVentaDescripcion()), 17),
                reporte.getImporteTotal(), reporte.getCantidadItems());
        escribirTexto(pagina.contenido, fila, 40, 512 - pagina.filaActual * 16, 8, FUENTE_NORMAL);
    }

    private static void escribirTexto(PDPageContentStream contenido, String texto, float x, float y,
                                      float tamanio, PDFont fuente) throws IOException {
        contenido.beginText();
        contenido.setFont(fuente, tamanio);
        contenido.newLineAtOffset(x, y);
        contenido.showText(texto);
        contenido.endText();
    }

    private static String textoPdf(Object valor) {
        String texto = valor == null ? "" : valor.toString();
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private static String recortar(String texto, int longitudMaxima) {
        return texto.length() <= longitudMaxima ? texto : texto.substring(0, longitudMaxima - 3) + "...";
    }

    private static final class PaginaPdf {
        private final PDPageContentStream contenido;
        private int filaActual;

        private PaginaPdf(PDPageContentStream contenido) {
            this.contenido = contenido;
        }

        private void cerrar() throws IOException {
            contenido.close();
        }
    }
}
