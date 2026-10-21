package com.facturacion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cliente extends AuditoriaApp {
    @Column(nullable = false) private String cuitCuil;
    @Column(nullable = false) private String denominacion;
    @OneToOne @JoinColumn(nullable = false) private Contacto contacto;
    @OneToOne @JoinColumn(nullable = false) private Domicilio domicilio;
}
