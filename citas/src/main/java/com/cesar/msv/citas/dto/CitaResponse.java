package com.cesar.msv.citas.dto;

import com.cesar.commons.dto.medico.DatosMedico;
import com.cesar.commons.dto.paciente.DatosPaciente;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informacion de una cita medica registrada")
public record CitaResponse(
        @Schema(description = "Identificador de la cita", example = "1")
        Long id,
        @Schema(description = "Informacion del paciente asociado a la cita")
        DatosPaciente paciente,
        @Schema(description = "Informacion del medico asociado a la cita")
        DatosMedico medico,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        @Schema(description = "Descripcion de los sintomas indicados por el paciente",
        example = "El paciente presenta irritacion en la garganta")
        String sintomas,
        @Schema(description = "Estado de la cita actual", example = "confirmada por el paciente")
        String estadoCita
) {
}
