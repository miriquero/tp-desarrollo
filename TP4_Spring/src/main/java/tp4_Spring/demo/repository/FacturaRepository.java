package tp4_Spring.demo.repository;

import com.facturacion.dto.FacturaReporteDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacturaRepository {

    private final EntityManager entityManager;

    public FacturaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<FacturaReporteDTO> buscarFacturasFiltradas(
            Date fechaDesde, Date fechaHasta, String estado, Double montoMinimo) {

        StringBuilder jpql = new StringBuilder(
                "SELECT new com.facturacion.dto.FacturaReporteDTO("
                        + "f.numero, f.fechaEmision, "
                        + "COALESCE(c.denominacion, 'Consumidor Final'), "
                        + "ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d)) "
                        + "FROM FacturaVenta f "
                        + "LEFT JOIN f.cliente c "
                        + "JOIN f.condicionIva ci "
                        + "JOIN f.puntoVenta pv "
                        + "JOIN f.detalles d "
                        + "WHERE 1 = 1 ");

        Map<String, Object> parametros = new HashMap<>();

        if (fechaDesde != null) {
            jpql.append("AND f.fechaEmision >= :fechaDesde ");
            parametros.put("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null) {
            jpql.append("AND f.fechaEmision <= :fechaHasta ");
            parametros.put("fechaHasta", fechaHasta);
        }
        if (estado != null && !estado.isBlank()) {
            jpql.append("AND f.estado = :estado ");
            parametros.put("estado", estado);
        }
        if (montoMinimo != null) {
            jpql.append("AND f.importeTotal >= :montoMinimo ");
            parametros.put("montoMinimo", montoMinimo);
        }

        jpql.append("GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ")
                .append("ci.denominacion, pv.descripcion, f.importeTotal");

        TypedQuery<FacturaReporteDTO> consulta = entityManager.createQuery(jpql.toString(), FacturaReporteDTO.class);
        parametros.forEach(consulta::setParameter);
        return consulta.getResultList();
    }
}
