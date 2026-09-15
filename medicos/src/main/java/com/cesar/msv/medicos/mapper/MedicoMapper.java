package com.cesar.msv.medicos.mapper;

import com.cesar.commons.dto.medico.MedicoRequest;
import com.cesar.commons.dto.medico.MedicoResponse;
import com.cesar.commons.enums.DisponibilidadMedico;
import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.commons.mapper.CommonMapper;
import com.cesar.msv.medicos.entity.Medico;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;
//COLOCAR COMPONENT
@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {
    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        if (request==null)return null;
        return Medico.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .email(request.email().toLowerCase().trim())
                .telefono(request.telefono().trim())
                .cedulaProfesional(request.cedulaProfesional().trim())
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
       if (entidad==null)return null;
       return new MedicoResponse(
               entidad.getId(),
               String.join(" ",
                       entidad.getNombre(),
                       entidad.getApellidoPaterno(),
                       entidad.getApellidoMaterno()),
               entidad.getEdad(),
               entidad.getEmail(),
               entidad.getTelefono(),
               entidad.getCedulaProfesional(),
               entidad.getEspecialidad().getDescripcion(),
               entidad.getDisponibilidad().getDescripcion(),
               entidad.getDisponibilidad().getCodigo());
    }
}
