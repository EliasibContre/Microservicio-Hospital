package com.cesar.commons.dto.paciente;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DAtos de un paciente asociado a la cita")
public record DatosPaciente(
        @Schema(description = "Nombre del paciente asociado a la cita", example = "Cesar E Mendez")
        String nombre,
        @Schema(description = "Numero expediente del paciente", example = "1231321312312312")
        String numeroExpediente,
        @Schema(description = "Edad del paciente", example = "23")
        String edad,
        @Schema(description = "Peso del paciente", example = "80.5")
        String peso,
        @Schema(description = "Estatura del paciente", example = "1.70")
        String estatura,
        @Schema(description = "Imc del paciente", example = "23.3 peso normal")
        String imc,
        @Schema(description = "Telefono del paciente", example = "1234567890")
        String telefono

) { }
