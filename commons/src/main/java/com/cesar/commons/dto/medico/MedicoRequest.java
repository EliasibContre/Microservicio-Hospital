package com.cesar.commons.dto.medico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.rmi.MarshalException;

@Schema(description = "Datos necesarios para registrar o actualizar un médico")
public record MedicoRequest(
        @Schema(description = "Nombre del médico", example = "Carlos")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del médico", example = "García")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del médico", example = "López")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del médico en años", example = "35", minimum = "18", maximum = "100")
        @NotNull(message = "La edad es requerida")
        @Min(value = 18, message = "La edad mínima es 18")
        @Max(value = 100, message = "La edad máxima es 100")
        Short edad,

        @Schema(description = "Correo electrónico del médico", example = "carlos.garcia@example.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (correo@dominio.com)")
        String email,

        @Schema(description = "Teléfono de contacto de diez dígitos", example = "5512345678")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener exactamente 10 dígitos numéricos")
        String telefono,

        @Schema(description = "Cédula profesional de 12 dígitos, según la regla del proyecto",
                example = "123456789012")
        @NotBlank(message = "La cédula es requerida")
        @Pattern(regexp = "^[0-9]{12}$", message = "La cédula debe contener exactamente 12 dígitos numéricos")
        String cedulaProfesional,

        @Schema(description = "Identificador de la especialidad del médico", example = "1", minimum = "1")
        @NotNull(message = "El id de especialidad es requerido")
        @Positive(message = "El id de especialidad debe ser positivo")
        Long idEspecialidad
) {
}

