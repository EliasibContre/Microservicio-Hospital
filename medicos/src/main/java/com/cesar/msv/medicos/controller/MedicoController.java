package com.cesar.msv.medicos.controller;

import com.cesar.commons.controller.CrudController;
import com.cesar.commons.dto.medico.MedicoRequest;
import com.cesar.commons.dto.medico.MedicoResponse;
import com.cesar.msv.medicos.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@Tag(name = "API medicos", description = "MEtodos para gestionar medicos")
public class MedicoController extends CrudController<MedicoRequest, MedicoResponse, MedicoService> {
    private final String claveInterna;
    public MedicoController(
            MedicoService service,
            @Value("${seguridad.interna.clave}") String claveInterna) {
        super(service);
        if (claveInterna == null || claveInterna.isBlank()) {
            throw new IllegalStateException("La clave interna es obligatoria");
        }
        this.claveInterna = claveInterna;
    }


    @GetMapping("/id-medico/{id}")
    public ResponseEntity<MedicoResponse>obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El id debe ser positivo")Long id
    ){
        return ResponseEntity.ok(services.obtenerMedicoPorIdSinEstado(id));
    }
    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable("idMedico") @Positive Long idMedico,
            @PathVariable("idDisponibilidad") @Positive Long idDisponibilidad,
            @RequestHeader(value = "X-Internal-Key", required = false)
            String claveRecibida) {

        if (claveRecibida == null || !MessageDigest.isEqual(
                claveInterna.getBytes(StandardCharsets.UTF_8),
                claveRecibida.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(403).build();
        }

        services.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{idMedico}/disponibilidad-manual/{idDisponibilidad}")
    public ResponseEntity<Void> actualizarDisponibilidadManual(
            @PathVariable("idMedico") @Positive Long idMedico,
            @PathVariable("idDisponibilidad") @Positive Long idDisponibilidad
    ){
        services.actualizarDisponibilidadManual(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();

    }
}
