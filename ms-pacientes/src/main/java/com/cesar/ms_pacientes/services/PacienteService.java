package com.cesar.ms_pacientes.services;

import com.cesar.commons.dto.paciente.PacienteRequest;
import com.cesar.commons.dto.paciente.PacienteResponse;
import com.cesar.commons.service.CrudServices;

public interface PacienteService extends CrudServices<PacienteRequest, PacienteResponse> {
    PacienteResponse obtenerPacientePorIdSinEstado(Long id);
}
