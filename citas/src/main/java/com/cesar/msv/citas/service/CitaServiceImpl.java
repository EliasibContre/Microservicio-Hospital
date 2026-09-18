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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{


    private static final List<EstadoCita> ESTADOS_CITAS_ABIERTAS = List.of(
            EstadoCita.PENDIENTE,
            EstadoCita.CONFIRMADA,
            EstadoCita.EN_CURSO
    );

    private static final List<EstadoCita> ESTADOS_BLOQUEO_MODIFICACION = List.of(
            EstadoCita.CONFIRMADA,
            EstadoCita.EN_CURSO
    );

    @Value("${seguridad.interna.clave}")
    private String claveInterna;
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    @PostConstruct
    private void validarConfiguracionInterna() {
        if (claveInterna == null || claveInterna.isBlank()) {
            throw new IllegalStateException("La clave interna es obligatoria");
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
//multiples ifs en cambiaMedico
@Override
public CitaResponse actualizar(CitaRequest request, Long id) {
    Cita cita = obtenerCitaOException(id);
    Long idMedicoAnterior = cita.getIdMedico();
    PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
    validarPacienteSinOtrasCitasActivas(request.idPaciente(), cita.getId());
    MedicoResponse medico = obtenerMedicoParaActualizacion(
            idMedicoAnterior,
            request.idMedico()
    );
    cita.actualizar(
            request.idPaciente(),
            request.idMedico(),
            request.fechaCita(),
            request.sintomas()
    );
    gestionarCambioMedico(idMedicoAnterior, cita);
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
    @Transactional(readOnly = true)
    public boolean tieneCitasQueBloqueanPaciente(Long idPaciente) {
        log.info("Consultando si el pacient tiene citas confirmadas o en curso");
        return citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                ESTADOS_BLOQUEO_MODIFICACION
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneCitasQueBloqueanMedico(Long idMedico) {
        log.info("Consultando si el medico tiene citas confirmadas o en curso");
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                ESTADOS_BLOQUEO_MODIFICACION
        );

    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneCitasActivasMedico(Long idMedico) {
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                ESTADOS_CITAS_ABIERTAS
        );

    }

    @Override
    public void eliminar(Long id) {
        Cita cita= obtenerCitaOException(id);
        log.info("ELiminado cita con cita con id: ", id);
        cita.eliminar();
        if (cita.getEstadoCita()==EstadoCita.PENDIENTE ){
            boolean tieneOtrasCitasActivas =
                    citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                            cita.getIdMedico(),
                            EstadoRegistro.ACTIVO,
                            ESTADOS_CITAS_ABIERTAS,
                            cita.getId()
                    );
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
        boolean tieneOtrasActivas =
                citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idPaciente,
                        EstadoRegistro.ACTIVO,
                        ESTADOS_CITAS_ABIERTAS,
                        idCita
                );
        if (tieneOtrasActivas)
                    throw new IllegalStateException("El paciente ya tiene otra cita pendiente confirmada o en curso");
    }
    private void validarPacientesSinCitasActivas(Long idPaciente){
        log.info("Validando si el paciente tiene citas activas");

        boolean tieneCitasActivas =
                citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                        idPaciente,
                        EstadoRegistro.ACTIVO,
                        ESTADOS_CITAS_ABIERTAS
                );
        //consultamos unicamente citas no eliminadas, si encuentra alguna lanza la excepcion, si no, deja continuar
        if (tieneCitasActivas)
            throw new IllegalStateException("El paciente ya tiene una cita pendiente, confirmada o en curso");
    }
    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponibilidad del medico en servicio remoto...");
        medicoClient.actualizarDisponibilidadMedico(idMedico,idDisponibilidad,claveInterna);
        log.info("Disponibilidad del metodo actualizada en el servicio");
    }



//separar metodo en subfunciones
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


    private MedicoResponse obtenerMedicoParaActualizacion(
            Long idMedicoAnterior, Long idMedicoSolicitado) {

        MedicoResponse medico = obtenerMedicoActivo(idMedicoSolicitado);

        if (!idMedicoAnterior.equals(idMedicoSolicitado)) {
            validarMedicoActivoDisponible(medico);
        }

        return medico;
    }

    private void gestionarCambioMedico(Long idMedicoAnterior, Cita cita) {
        if (idMedicoAnterior.equals(cita.getIdMedico())) {
            return;
        }

        liberarMedicoSiNoTieneOtrasCitas(idMedicoAnterior, cita.getId());

        cambiarDisponibilidadMedicoSegunEstadoCita(
                cita.getIdMedico(),
                cita.getEstadoCita(),
                cita.getId()
        );
    }

    private void liberarMedicoSiNoTieneOtrasCitas(
            Long idMedico, Long idCitaExcluida) {

        boolean tieneOtrasCitas =
                citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idMedico,
                        EstadoRegistro.ACTIVO,
                        ESTADOS_CITAS_ABIERTAS,
                        idCitaExcluida
                );

        if (tieneOtrasCitas) {
            return;
        }

        actualizarDisponibilidadMedico(
                idMedico,
                DisponibilidadMedico.DISPONIBLE.getCodigo()
        );
    }



}
