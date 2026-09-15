package com.cesar.ms_pacientes.mapper;

import com.cesar.commons.dto.paciente.PacienteResponse;
import com.cesar.commons.dto.paciente.PacienteRequest;
import com.cesar.commons.mapper.CommonMapper;
import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.ms_pacientes.entities.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper <PacienteRequest, PacienteResponse, Paciente> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request==null)return null;
        return Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad().shortValue())
                .peso(request.peso())
                .estatura(request.estatura())
                .email(request.email().trim())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .build();
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null)return null;
        return new PacienteResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getNumeroExpediente(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getEstadoRegistro().getDescripcion());

    }

}
