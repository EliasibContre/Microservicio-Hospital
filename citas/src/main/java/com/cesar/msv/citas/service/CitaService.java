package com.cesar.msv.citas.service;

import com.cesar.commons.service.CrudServices;
import com.cesar.msv.citas.dto.CitaRequest;
import com.cesar.msv.citas.dto.CitaResponse;

public interface CitaService extends CrudServices<CitaRequest, CitaResponse> {
    void actualizarEstadoCita(Long id, Long idEstadoCita);
}
