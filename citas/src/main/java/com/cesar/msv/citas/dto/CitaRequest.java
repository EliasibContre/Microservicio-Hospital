package com.cesar.msv.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


@Schema(description = "Datos necesarios para registrar o actualiar una cita")
public record CitaRequest(
        @Schema(description = "Id del paciente para crear o actualizar", example = "5")
        @NotNull(message = "El id de paciente es requerido")
        @Positive(message = "El id del paciente debe ser positivo")
        Long idPaciente,
        @Schema(description = "Id del medico para crear o actualizar", example = "1", minimum = "5")
        @NotNull(message = "El id de medico es requerido")
        @Positive(message = "El id del medico debe ser positivo")
        Long idMedico,
        @Schema(description = "Fecha y hora programada para la cita, estas deben ser actuales o futuras",
        example = "25/09/2026 10:30",
        type = "string",
        format = "date-time")
        @NotNull(message = "La fecha de la cita es requerido")
        @FutureOrPresent(message = "la fecha de la cita debe ser futura")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        @Schema(description = "Datos necesarios para registrar o actualiar los sintomas")
        @NotBlank(message = "El id de paciente es requerido")
        @Size(min = 20, max = 500, message = "oa descripcion de los sintomas debe tener 20 a 500 caracteres")
        String sintomas
) {
}
