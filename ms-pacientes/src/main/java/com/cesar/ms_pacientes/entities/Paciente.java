package com.cesar.ms_pacientes.entities;

import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.commons.utils.StringCustomUtils;
import com.cesar.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private static void validarDatos(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            Short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion) {

        StringCustomUtils.validarTamanio(
                nombre, 1, 50, "El nombre debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(
                apellidoPaterno, 1, 50,
                "El apellido paterno debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(
                apellidoMaterno, 1, 50,
                "El apellido materno debe contener entre 1 y 50 caracteres");

        ValoresNumericosUtils.validarRangoShort(
                edad, (short) 1, (short) 100,
                "La edad debe estar entre 1 y 100 años");

        ValoresNumericosUtils.validarRangoDouble(
                peso, 0.1, 200.0,
                "El peso debe estar entre 0.1 y 200 kg");

        ValoresNumericosUtils.validarRangoDouble(
                estatura, 1.0, 2.0,
                "La estatura debe estar entre 1.0 y 2.0 m");

        if (!Double.isFinite(peso) || !Double.isFinite(estatura)) {
            throw new IllegalArgumentException(
                    "El peso y la estatura deben ser valores finitos");
        }

        StringCustomUtils.validarTamanio(
                email, 1, 100,
                "El email debe contener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(
                telefono, 10, 10,
                "El teléfono debe contener exactamente 10 dígitos");

        if (!telefono.matches("[0-9]{10}")) {
            throw new IllegalArgumentException(
                    "El teléfono debe contener únicamente dígitos numéricos");
        }

        StringCustomUtils.validarTamanio(
                direccion, 1, 150,
                "La dirección debe contener entre 1 y 150 caracteres");
    }


    private void validarNoEliminado() {
        if(this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("El paciente ya tiene estatus eliminado");
    }

    public void actualizar(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            Short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion) {

        validarNoEliminado();

        validarDatos(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                edad,
                peso,
                estatura,
                email,
                telefono,
                direccion
        );

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = calcularImc(peso, estatura);
        this.email = email.trim();
        this.telefono = telefono;
        this.direccion = direccion.trim();
    }



    public void eliminar() {
        validarNoEliminado();

        estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    private static double calcularImc(Double peso, Double estatura) {
        return peso / (estatura * estatura);
    }

    private static String generarNumeroExpediente(String telefono) {
        StringBuilder expediente = new StringBuilder(20);

        for (char digito : telefono.toCharArray()) {
            expediente.append(digito).append('X');
        }

        return expediente.toString();
    }


    public static Paciente crear(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            Short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion) {

        validarDatos(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                edad,
                peso,
                estatura,
                email,
                telefono,
                direccion
        );

        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .imc(calcularImc(peso, estatura))
                .email(email.trim())
                .numeroExpediente(generarNumeroExpediente(telefono))
                .telefono(telefono)
                .direccion(direccion.trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }





}
