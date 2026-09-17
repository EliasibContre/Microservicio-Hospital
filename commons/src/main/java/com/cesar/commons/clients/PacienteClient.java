package com.cesar.commons.clients;

import com.cesar.commons.dto.paciente.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pacientes")
public interface PacienteClient {
    @GetMapping("/api/pacientes/{id}")
    PacienteResponse obtenerPacienteActivo(@PathVariable Long id);

    @GetMapping("/api/pacientes/sin-estado/{id}")
    PacienteResponse obtenerPacienteSinFiltrarEstado(@PathVariable Long id);

}
