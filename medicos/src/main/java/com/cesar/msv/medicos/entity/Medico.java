package com.cesar.msv.medicos.entity;

import com.cesar.commons.enums.DisponibilidadMedico;
import com.cesar.commons.enums.EspecialidadMedico;
import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.commons.utils.StringCustomUtils;
import com.cesar.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEDICOS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;
    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;
    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String  apellidoPaterno;
    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private  String apellidoMaterno;
    @Column(name = "EDAD", nullable = false)
    private Short edad;
    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;
    @Column(name = "TELEFONO", length = 10,nullable = false)
    private String telefono;
    @Column(name = "CEDULA_PROFESIONAL",length = 12, nullable = false)
    private String cedulaProfesional;
    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidad;
    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD",nullable = false)
    private DisponibilidadMedico disponibilidad;
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO")
    private EstadoRegistro estadoRegistro;


    private void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                              String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad){
        StringCustomUtils.validarTamanio(nombre, 1,50, "El nombre es requerido");
        StringCustomUtils.validarTamanio(apellidoPaterno, 1,50, "El apellido es requerido");
        StringCustomUtils.validarTamanio(apellidoMaterno,1,50, "El apellido materno es requerido");
        StringCustomUtils.validarTamanio(email,1,100, "El email es requerido");
        StringCustomUtils.validarTamanio(telefono,10,10,"el telefono es requerido y necesitas 10 caracteres");
        StringCustomUtils.validarTamanio(cedulaProfesional,12,12, "la cedula profesional es requerida y necesita 12 caracteres");
        ValoresNumericosUtils.validarRangoShort(edad,(short)18,(short)100,"la edad requerida debe ser entre 18 y 100");
        if (especialidad==null)
            throw new IllegalArgumentException("La especialidad es requerida");
    }
    private void validarNoEliminado(){
        if (this.estadoRegistro==EstadoRegistro.ELIMINADO)
            throw new IllegalStateException("El medico ya esta eliminado");
    }
    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro=EstadoRegistro.ELIMINADO;
    }
    public void actualizarEspecialidad(EspecialidadMedico especialidad){
        validarNoEliminado();
        if (especialidad==null)
            throw new IllegalArgumentException("La especialidad es requerida");
        this.especialidad=especialidad;
    }
    public void actualizarDisponibilidad(DisponibilidadMedico disponibilidad){
        validarNoEliminado();
        if (disponibilidad==null)
            throw new IllegalArgumentException("Disponibilidad requerida");
        this.disponibilidad=disponibilidad;
    }
    public void actualizar (String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                            String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad){
        validarNoEliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, email, telefono, cedulaProfesional, especialidad);
        actualizarEspecialidad(especialidad);
        this.nombre=nombre.trim();
        this.apellidoPaterno=apellidoPaterno.trim();
        this.apellidoMaterno=apellidoMaterno.trim();
        this.edad=edad;
        this.email=email.trim().toLowerCase();
        this.telefono=telefono.trim();
        this.cedulaProfesional=cedulaProfesional.trim();

    }




}
