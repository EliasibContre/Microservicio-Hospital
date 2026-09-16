package com.cesar.msv.citas.service;

import com.cesar.commons.clients.MedicoClient;
import com.cesar.commons.dto.medico.MedicoResponse;
import com.cesar.commons.dto.paciente.PacienteResponse;
import com.cesar.commons.enums.DisponibilidadMedico;
import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.commons.exceptions.RecursoNoEncontradoException;
import com.cesar.msv.citas.dto.CitaRequest;
import com.cesar.msv.citas.dto.CitaResponse;
import com.cesar.msv.citas.entity.Cita;
import com.cesar.msv.citas.enums.EstadoCita;
import com.cesar.msv.citas.mapper.CitaMapper;
import com.cesar.msv.citas.repository.CitaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;




    @Override
    public List<CitaResponse> listar() {
        log.info("Listando citas activas");
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOException(id);
        return citaMapper.entidadAResponse(cita,null,obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita...");
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoActivoDisponible(medico);
        Cita cita =citaMapper.requestAEntidad(request);
        citaRepository.save(cita);
        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(),cita.getEstadoCita());

        return citaMapper.entidadAResponse(cita,null,medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOException(id);
        MedicoResponse medico=obtenerMedicoActivo(request.idMedico());
        return citaMapper.entidadAResponse(cita, null, medico);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOException(idCita);
        log.info("Actualizando cita con id {}: ",idCita);
        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));
        citaRepository.save(cita);
        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(),cita.getEstadoCita());
        log.info("Estado de la cita {} actualizado correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita= obtenerCitaOException(id);
        log.info("ELiminado cita con cita con id: ", id);
        cita.eliminar();
        if (cita.getEstadoCita()==EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());
        log.info("Cita con id {} ha sido maarcada como eliminada", id);

    }

    private MedicoResponse obtenerMedicoActivo(Long id){
        log.info("Buscando medico con id {} en el servicio remoto... ", id);
        return medicoClient.obtenerMedicoActivoPorId(id);
    }
    private MedicoResponse obtenerMedicoSinEstado(Long id){
        log.info("Buscando medico con id {} en el servicio remoto... ", id);
        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private Cita obtenerCitaOException (Long id){
        log.info("Buscando cita con id: {}", id);
        return citaRepository.findById(id).orElseThrow(
                ()->new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }
    private PacienteResponse obtenerPacienteSinEstado(Long id){
        log.info("Buscando paciente sin estado con id {} en el servicio remoto", id);
        return null;
    }
    private void validarMedicoActivoDisponible (MedicoResponse medico){
        log.info("validando si el medico activo esta disponible");
        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new IllegalStateException("El medico no esta disponible para una consulta");

    }
    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponibilidad del medico en servicio remoto...");
        medicoClient.actualizarDisponibilidadMedico(idMedico,idDisponibilidad);
        log.info("Disponibilidad del metodo actualizada en el servicio");
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita){
        switch (estadoCita){
            case PENDIENTE, CONFIRMADA -> actualizarDisponibilidadMedico(idMedico,DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            case EN_CURSO -> actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            case FINALIZADA,CANCELADA ->actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }


}
