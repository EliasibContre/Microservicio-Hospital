package com.cesar.msv.medicos.service;

import com.cesar.commons.clients.CitaClient;
import com.cesar.commons.dto.medico.MedicoRequest;
import com.cesar.commons.dto.medico.MedicoResponse;

import com.cesar.commons.enums.DisponibilidadMedico;
import com.cesar.commons.enums.EspecialidadMedico;
import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.commons.exceptions.RecursoNoEncontradoException;
import com.cesar.msv.medicos.entity.Medico;
import com.cesar.msv.medicos.mapper.MedicoMapper;
import com.cesar.msv.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.RecognitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService{

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando todos los medicos");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico con id: {}", id);
        return medicoMapper.entidadAResponse(medicoRepository.findById(id)
                .orElseThrow(()-> new RecursoNoEncontradoException(
                        "Medico sin estado no encontrado con id: " + id)));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando nuevo medico: {}",request.nombre());
        Medico medico = medicoMapper.requestAEntidad(request);
        validarDatosUnicos(request);
        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));
        medicoRepository.save(medico);
        log.info("Nuevo medico registrado: {}", medico.getNombre());
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico =obtenerMedicoActivoPorId(id);
        validarMedicosCitasBloqueantes(id);
        log.info("Actualizando medico con id: {}", id);
        validarCambiossUnicos(request,id);
        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));
        log.info("medico actualizado: {}", medico.getNombre());
        return medicoMapper.entidadAResponse(medico);
    }
    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico =obtenerMedicoActivoPorId(idMedico);
        log.info("Actualizando medico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadMedico(idDisponibilidad);
        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();
        medico.actualizarDisponibilidad(nuevaDisponibilidad);
        log.info("Disponibilidad del medico con id {} cambio de {} a {}", idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }

    @Override
    public void actualizarDisponibilidadManual(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoPorId(idMedico);
        DisponibilidadMedico nuevaDisponibilidad =
                DisponibilidadMedico.obtenerDisponibilidadMedico(idDisponibilidad);
        if (citaClient.tieneCitasMedicoActivo(idMedico))
            throw new IllegalStateException("No se puede cambiar manual la disponibilidad de un medic con citas pendeintes. confirmadas o en curso");
        medico.actualizarDisponibilidad(nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico =obtenerMedicoActivoPorId(id);
        validarMedicosCitasBloqueantes(id);
        log.info("Eliminando medico con id: {}", id);
        medico.eliminar();
        log.info("Medico eliminado exitoso");
    }

    private Medico obtenerMedicoActivoPorId(Long id){
        log.info("Buscando medico con id: {}", id);
        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(()-> new RecursoNoEncontradoException(
                        "Medico activo no encontrado con id: " + id));


    }
    private void validarMedicosCitasBloqueantes(Long idMedico){
        log.info("consultando si medicos tienen citas confirmadas o en curso");
        if (citaClient.tieneCitasQueBloqueanMedico(idMedico))
            throw new IllegalStateException("El medico tiene citas confirmadas o en curso y no se pueden modificar ni eliminar");
    }
    private void validarDatosUnicos(MedicoRequest request){
        log.info("Validando datos del medico");
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email(),EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe medico registrado con ese email" + request.email());
        log.info("Validando datos del medico");
        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono(),EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe medico registrado con ese telefono: " + request.telefono());
        log.info("Validando datos del medico");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional(),EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe medico registrado con esa cedula: " + request.cedulaProfesional());
    }

    private void validarCambiossUnicos(MedicoRequest request, Long id){
        log.info("Validando datos del medico");
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email(),EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe medico registrado con ese email" + request.email());
        log.info("Validando datos del medico");
        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono(),EstadoRegistro.ACTIVO,id))
            throw new IllegalArgumentException("Ya existe medico registrado con ese email" + request.telefono());
        log.info("Validando datos del medico");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional(),EstadoRegistro.ACTIVO,id))
            throw new IllegalArgumentException("Ya existe medico registrado con ese email" + request.cedulaProfesional());
    }
}
