package com.cesar.commons.clients;

import com.cesar.commons.dto.medico.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "medicos")
public interface MedicoClient {
    @GetMapping("/{id}")
    MedicoResponse obtenerMedicoActivoPorId(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoSinEstadoPorId(@PathVariable Long id);

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    void actualizarDisponibilidadMedico(
            @PathVariable("idMedico") Long idMedico,
            @PathVariable("idDisponibilidad") Long idDisponibilidad,
            @RequestHeader("X-Internal-Key") String claveInterna);

}
