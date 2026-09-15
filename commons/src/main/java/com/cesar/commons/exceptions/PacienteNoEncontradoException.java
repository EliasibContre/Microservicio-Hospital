package com.cesar.commons.exceptions;

public class PacienteNoEncontradoException extends RuntimeException {
    public PacienteNoEncontradoException(Long id) {
        super("No se encontró un paciente activo con id " + id);
    }
}
