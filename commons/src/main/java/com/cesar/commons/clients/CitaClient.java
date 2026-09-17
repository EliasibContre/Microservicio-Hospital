package com.cesar.commons.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {
    @GetMapping("/pacientes/{idPaciente}/bloqueo")
    boolean tieneCitasQueBloqueanPaciente(@PathVariable Long idPaciente);
    @GetMapping("/medicos/{idMedico}/bloqueo")
    boolean tieneCitasQueBloqueanMedico(@PathVariable Long idMedico);
}
