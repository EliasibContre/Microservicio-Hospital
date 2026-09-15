package com.cesar.commons.dto.paciente;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para la creacion de un paciente")
public record PacienteRequest(
        @Schema(description = "Nombre del paciente",example = "Eleazar")
        @NotBlank(message = "EL nombre es requerido")
        @Size(max = 50, message = "El nombre debe tener maximo 50 caracteres")
        String nombre,
        @Schema(description = "Apellido paterno del paciente",example = "Gomez")
        @NotBlank(message = "El apellido es oblgatorio")
        @Size(max = 50, message = "El apellido paterno debe de tener maximo 50 caracteres")
        String apellidoPaterno,
        @Schema(description = "Apellido materno del paciente",example = "Rodriguez")
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 50, message = "El apellido materno debe tener maximo 50 caracteres")
        String apellidoMaterno,
        @Schema(description = "edad del paciente",example = "27")
        @NotNull(message = "la edad es requerida")
        @Min(value = 1, message = "La edad mínima es 1")
        @Max(value = 100, message = "La edad máxima es 100")
        Integer edad,
        @Schema(description = "Peso del paciente en kilogramos",example = "55")
        @NotNull(message = "El peso es requerido")
        @DecimalMin(value = "0.1", message = "El peso mínimo es 0.1 kg")
        @DecimalMax(value = "200", message = "El peso máximo es 200 kg")
        Double peso,
        @Schema(description = "Estatura del paciente en metros",example = "1.50")
        @NotNull(message = "La estatura es requerida")
        @DecimalMin(value = "1.0", message = "La estatura mínima es 1.0 m")
        @DecimalMax(value = "2.0", message = "La estatura máxima es 2.0 m")
        Double estatura,
        @Schema(description = "Correo del paciente",example = "paciente@paciente.com")
        @NotBlank(message = "El correo es requerido")
        @Email(message = "El correo debe tener un formato válido")
        @Size(max = 100, message = "El correo admite máximo 100 caracteres")
        String email,
        @Schema(description = "Telefono del paciente",example = "1234567890")
        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener solo 10 digitos")
        String telefono,
        @Schema(description = "Direccion del paciente",example = "Enrique Segoviano 206")
        @NotBlank(message = "La direccion es requerida")
        @Size(min = 1,max = 150, message = "la direccion debe contener texto y no puede superar los 150 caracteres")
        String direccion


) {
}
