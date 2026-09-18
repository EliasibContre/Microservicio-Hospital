package com.cesar.ms_pacientes.services;

import com.cesar.commons.clients.CitaClient;
import com.cesar.commons.dto.paciente.PacienteRequest;
import com.cesar.commons.dto.paciente.PacienteResponse;
import com.cesar.commons.exceptions.RecursoNoEncontradoException;
import com.cesar.ms_pacientes.entities.Paciente;
import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.commons.exceptions.*;
import com.cesar.ms_pacientes.mapper.PacienteMapper;
import com.cesar.ms_pacientes.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Transactional
public class PacienteServiceImpl implements PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final CitaClient citaClient;


    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return pacienteRepository.findByEstadoRegistroOrderByIdAsc(EstadoRegistro.ACTIVO)
                .stream().map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(buscarPacienteActivoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id)
                .orElseThrow(()->new RecursoNoEncontradoException(
                        "Paciente no encontrado con id: " + id)));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        Paciente paciente = pacienteMapper.requestAEntidad(request);
        return pacienteMapper.entidadAResponse(
                pacienteRepository.save(paciente)
        );
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = buscarPacienteActivoPorId(id);
        validarPacientesSinCitasBloqueantes(id);

        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad().shortValue(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );

        return pacienteMapper.entidadAResponse(paciente);
    }


    @Override
    public void eliminar(Long id) {
        Paciente paciente = buscarPacienteActivoPorId(id);
        validarPacientesSinCitasBloqueantes(id);
        paciente.eliminar();
    }

    private Paciente buscarPacienteActivoPorId(Long id) {
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new PacienteNoEncontradoException(id));
    }

    private void validarPacientesSinCitasBloqueantes(Long idPaciente){
        if (citaClient.tieneCitasQueBloqueanPaciente(idPaciente))
            throw new IllegalStateException("El paciente tiene citas confirmaDas o en curso, no puede modificarse ni eliminarse");
    }

}
