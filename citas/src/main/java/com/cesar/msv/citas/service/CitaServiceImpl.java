package com.cesar.msv.citas.service;

import com.cesar.commons.clients.CitaClient;
import com.cesar.commons.clients.MedicoClient;
import com.cesar.commons.clients.PacienteClient;
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
    private final PacienteClient pacienteClient;



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
        return citaMapper.entidadAResponse(
                cita,obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita...");
        PacienteResponse paciente= obtenerPacienteActivo(request.idPaciente());
        validarPacientesSinCitasActivas(request.idPaciente());
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoActivoDisponible(medico);
        Cita cita =citaMapper.requestAEntidad(request);
        citaRepository.save(cita);
        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(),cita.getEstadoCita(),cita.getId());

        return citaMapper.entidadAResponse(cita,paciente,medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOException(id);

        Long idMedicoAnterior = cita.getIdMedico();
        boolean cambiaMedico = !idMedicoAnterior.equals(request.idMedico());

        PacienteResponse paciente=obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinOtrasCitasActivas(request.idPaciente(), cita.getId());

        MedicoResponse medico=obtenerMedicoActivo(request.idMedico());
        if (cambiaMedico)
            validarMedicoActivoDisponible(medico);
        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
        if (cambiaMedico){
            boolean anteriorTieneActivas=
                    citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                            idMedicoAnterior,
                            EstadoRegistro.ACTIVO,
                            List.of(
                                    EstadoCita.PENDIENTE,
                                    EstadoCita.CONFIRMADA,
                                    EstadoCita.EN_CURSO
                            ),
                            cita.getId()
                    );
            if (!anteriorTieneActivas) {
                actualizarDisponibilidadMedico(idMedicoAnterior, DisponibilidadMedico.DISPONIBLE.getCodigo());
            }
            cambiarDisponibilidadMedicoSegunEstadoCita(
                    cita.getIdMedico(),
                    cita.getEstadoCita(),
                    cita.getId());
        }

        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOException(idCita);
        log.info("Actualizando cita con id {}: ",idCita);
        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));
        citaRepository.save(cita);
        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(),cita.getEstadoCita(), cita.getId());
        log.info("Estado de la cita {} actualizado correctamente", idCita);
    }

    @Override
    public boolean tieneCitasQueBloqueanPaciente(Long idPaciente) {
        log.info("Consultando si el pacient tiene citas confirmadas o en curso");
        return citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(idPaciente
                ,EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                ));
    }

    @Override
    public boolean tieneCitasQueBloqueanMedico(Long idMedico) {
        log.info("Consultando si el medico tiene citas confirmadas o en curso");
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                ));
    }

    @Override
    public void eliminar(Long id) {
        Cita cita= obtenerCitaOException(id);
        log.info("ELiminado cita con cita con id: ", id);
        cita.eliminar();
        if (cita.getEstadoCita()==EstadoCita.PENDIENTE ){
            boolean tieneOtrasCitasActivas=citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                    cita.getIdMedico(),
                    EstadoRegistro.ACTIVO,
                    List.of(EstadoCita.PENDIENTE,
                            EstadoCita.CONFIRMADA,
                            EstadoCita.EN_CURSO),
                    cita.getId());
            if (!tieneOtrasCitasActivas)
                actualizarDisponibilidadMedico(cita.getIdMedico(),
                        DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
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

    private PacienteResponse obtenerPacienteActivo (Long id){
        log.info("Buscando paciente con el id {} en el servicio remoto...", id);
        return pacienteClient.obtenerPacienteActivo(id);
    }
    private PacienteResponse obtenerPacienteSinEstado(Long id){
        log.info("Buscando paciente sin estado con id {} en el servicio remoto", id);
        return pacienteClient.obtenerPacienteSinFiltrarEstado(id);
    }
    private void validarMedicoActivoDisponible (MedicoResponse medico){
        log.info("validando si el medico activo esta disponible");
        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new IllegalStateException("El medico no esta disponible para una consulta");

    }

    private void validarPacienteSinOtrasCitasActivas(Long idPaciente, Long idCita){
        log.info("Validando paciente con otras citas");
        boolean tieneOtrasActivas=
                citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idPaciente,
                        EstadoRegistro.ACTIVO,
                        List.of(
                                EstadoCita.PENDIENTE,
                                EstadoCita.CONFIRMADA,
                                EstadoCita.EN_CURSO
                        ),
                        idCita);
                if (tieneOtrasActivas)
                    throw new IllegalStateException("El paciente ya tiene otra cita pendiente confirmada o en curso");
    }
    private void validarPacientesSinCitasActivas(Long idPaciente){
        log.info("Validando si el paciente tiene citas activas");
        boolean tieneCitasActivas=
                citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                        idPaciente,
                        EstadoRegistro.ACTIVO,
                        List.of(
                                EstadoCita.PENDIENTE,
                                EstadoCita.CONFIRMADA,
                                EstadoCita.EN_CURSO
                        ));
        //consultamos unicamente citas no eliminadas, si encuentra alguna lanza la excepcion, si no, deja continuar
        if (tieneCitasActivas)
            throw new IllegalStateException("El paciente ya tiene una cita pendiente, confirmada o en curso");
    }
    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponibilidad del medico en servicio remoto...");
        medicoClient.actualizarDisponibilidadMedico(idMedico,idDisponibilidad);
        log.info("Disponibilidad del metodo actualizada en el servicio");
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita, Long idCita){
        switch (estadoCita){
            case PENDIENTE, CONFIRMADA -> actualizarDisponibilidadMedico(idMedico,DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            case EN_CURSO -> actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            case FINALIZADA,CANCELADA ->{
                boolean tieneOtraEnCurso= citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idMedico,
                        EstadoRegistro.ACTIVO,
                        List.of(EstadoCita.EN_CURSO),
                        idCita
                );
                DisponibilidadMedico disponibilidad;
                if (tieneOtraEnCurso){
                    disponibilidad=DisponibilidadMedico.EN_CONSULTA;
                }else {
                    boolean tieneOtrasPendientesOConfirmadas=
                            citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                                    idMedico,
                                    EstadoRegistro.ACTIVO,
                                    List.of(
                                            EstadoCita.PENDIENTE,
                                            EstadoCita.CONFIRMADA
                                    ),
                                    idCita
                            );
                    disponibilidad=tieneOtrasPendientesOConfirmadas
                            ? DisponibilidadMedico.NO_DISPONIBLE
                            :DisponibilidadMedico.DISPONIBLE;
                }
                actualizarDisponibilidadMedico(
                        idMedico,
                        disponibilidad.getCodigo()
                );

            }
        }
    }


}
