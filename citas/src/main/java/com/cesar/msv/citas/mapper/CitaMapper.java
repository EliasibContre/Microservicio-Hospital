package com.cesar.msv.citas.mapper;

import com.cesar.commons.dto.medico.DatosMedico;
import com.cesar.commons.dto.medico.MedicoResponse;
import com.cesar.commons.dto.paciente.DatosPaciente;
import com.cesar.commons.dto.paciente.PacienteResponse;
import com.cesar.commons.mapper.CommonMapper;
import com.cesar.msv.citas.dto.CitaRequest;
import com.cesar.msv.citas.dto.CitaResponse;
import com.cesar.msv.citas.entity.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {
    @Override
    public Cita requestAEntidad(CitaRequest request) {
        if (request == null) return null;
        return Cita.crear(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        if (entidad == null) return null;
        return new CitaResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
    }
    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        if (entidad == null) return null;
        return new CitaResponse(
                entidad.getId(),
                pacienteResponseADatosPaciente(paciente),
                medicoResponseADatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
    }
    public DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente) {
        if (paciente == null) return null;

        return new DatosPaciente(
                paciente.nombre(),
                paciente.numeroExpediente(),
                paciente.edad() + "años",
                paciente.peso() +" kg",
                paciente.estatura() + "m.",
                String.join(" ",
                        Math.round(paciente.imc()*100.0)/100.0 + "",
                        clacificacionIMC(paciente.imc())),
                paciente.telefono()
        );
    }

    private String clacificacionIMC(double imc){
        if (imc< 18.5)return "Bajo peso";
        if (imc< 25)return "Peso normal";
        if (imc< 30)return "sobrepeso";
        if (imc< 35)return "obesidad grado I";
        if (imc <40)return "obesidad grado II";
        return "obesidad grado III";
    }

    private DatosMedico medicoResponseADatosMedico( MedicoResponse medico){
        if (medico == null) return null;
        return new DatosMedico(
                medico.nombre(),
                medico.cedulaProfesional(),
                medico.especialidad()
        );
    }


}
