package com.cesar.commons.dto.medico;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Información del médico devuelta por la API")
public record MedicoResponse(
        @Schema(description = "Identificador del médico", example = "1")
        Long id,

        @Schema(description = "Nombre del médico", example = "Carlos")
        String nombre,

        @Schema(description = "Edad del médico en años", example = "35")
        Short edad,

        @Schema(description = "Correo electrónico del médico", example = "carlos.garcia@example.com")
        String email,

        @Schema(description = "Teléfono de contacto del médico", example = "5512345678")
        String telefono,

        @Schema(description = "Número de cédula profesional", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "Nombre de la especialidad médica", example = "Cardiología")
        String especialidad,

        @Schema(description = "Descripción de la disponibilidad del médico", example = "Disponible")
        String disponibilidad,

        @Schema(description = "Identificador de la disponibilidad asociada al médico", example = "1")
        Long idDisponibilidad
) { }
