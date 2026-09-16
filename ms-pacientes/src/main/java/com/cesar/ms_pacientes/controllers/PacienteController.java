package com.cesar.ms_pacientes.controllers;

import com.cesar.commons.dto.paciente.PacienteRequest;
import com.cesar.commons.dto.paciente.PacienteResponse;
import com.cesar.ms_pacientes.services.PacienteServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "CRUD de pacientes activos")
public class PacienteController {
    private final PacienteServiceImpl service;

    @PostMapping
    public ResponseEntity<PacienteResponse> registrar(@Valid @RequestBody PacienteRequest request) {
        PacienteResponse paciente = service.registrar(request);
        return ResponseEntity.created(URI.create("/api/pacientes/" + paciente.id())).body(paciente);
    }

    @GetMapping
    public List<PacienteResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public PacienteResponse obtener(@PathVariable @Positive Long id) { return service.obtenerPorId(id); }

    @PutMapping("/{id}")
    public PacienteResponse actualizar(@PathVariable @Positive Long id,
            @Valid @RequestBody PacienteRequest request) { return service.actualizar(request,id); }


    @GetMapping("/sin-estado/{id}")
    public PacienteResponse obtenerPorIdSinEstado(@PathVariable @Positive Long id){
        return  service.obtenerPacientePorIdSinEstado(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
