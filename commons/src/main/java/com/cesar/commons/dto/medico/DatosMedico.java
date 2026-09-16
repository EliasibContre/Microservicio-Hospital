package com.cesar.commons.dto.medico;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DAtos de un medico asociado a una cita")

public record DatosMedico(
        @Schema(description = "Nombre del medico asociado a la cita", example = "Evelyn Martinez Diaz")
        String nombre,
        @Schema(description = "Cedula profesiona del medico", example = "123456789012")
        String cedulaProfesional,
        @Schema(description = "Nombre de la especialidad del medico ", example = "Neuro cirugia")
        String especialidad
) {
}
