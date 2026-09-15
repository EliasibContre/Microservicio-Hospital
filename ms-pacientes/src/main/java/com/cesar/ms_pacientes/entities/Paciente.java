package com.cesar.ms_pacientes.entities;

import com.cesar.commons.enums.EstadoRegistro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;
    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;
    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;
    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;
    @Column(name = "EDAD", nullable = false)
    private Short edad;
    @Column(name = "PESO", nullable = false)
    private Double peso;
    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;
    @Column(name = "IMC", nullable = false)
    private Double imc;
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;
    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numeroExpediente;
    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;
    @Column(name = "DIRECCION",nullable = false,length = 150)
    private String direccion;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false, columnDefinition = "estado_registro")
    private EstadoRegistro estadoRegistro;

}
