package com.cesar.msv.medicos.service;

import com.cesar.commons.dto.medico.MedicoRequest;
import com.cesar.commons.dto.medico.MedicoResponse;
import com.cesar.commons.service.CrudServices;

public interface MedicoService extends CrudServices<MedicoRequest, MedicoResponse> {
    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);
    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
