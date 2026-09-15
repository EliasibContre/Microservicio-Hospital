package com.cesar.msv.medicos.controller;

import com.cesar.commons.controller.CrudController;
import com.cesar.commons.dto.medico.MedicoRequest;
import com.cesar.commons.dto.medico.MedicoResponse;
import com.cesar.msv.medicos.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API medicos", description = "MEtodos para gestionar medicos")
public class MedicoController extends CrudController<MedicoRequest, MedicoResponse, MedicoService> {
    public MedicoController(MedicoService service){
        super(service);
    }
    @GetMapping("/id-medico/{id}")
    public ResponseEntity<MedicoResponse>obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El id debe ser positivo")Long id
    ){
        return ResponseEntity.ok(services.obtenerMedicoPorIdSinEstado(id));
    }
    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    @Operation(summary = "ACtuializar la disponibilidad de medicos" + "(No es endpoint libre, debe gestionarlo con elsistema de citas)")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable @Positive(message = "El id debe ser positivo")Long idMEdico,
            @PathVariable @Positive(message = "El id debe ser positivo")Long idDisponibilidad
    ){
        services.actualizarDisponibilidadMedico(idMEdico,idDisponibilidad);
        return ResponseEntity.noContent().build();
    }
}
