package com.facturacion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "domicilio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Domicilio extends EntityId {
    private String nombreCalle;
    private String numeroCalle;
}
