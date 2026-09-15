package com.cesar.ms_pacientes.services;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
@Transactional
public class PacienteServiceImpl implements PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    public List<PacienteResponse> listar() {
        return pacienteRepository.findByEstadoRegistroOrderByIdAsc(EstadoRegistro.ACTIVO)
                .stream().map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(buscarMedicoActivoPorId(id));
    }

    @Override
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id)
                .orElseThrow(()->new RecursoNoEncontradoException(
                        "Medico sin estado no encontrado con id: " + id)));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        //double imc = calcularImc(request);
        Paciente paciente = pacienteMapper.requestAEntidad(request).toBuilder()
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .numeroExpediente(
                        UUID.randomUUID().toString().replace("-","").substring(0,20))
                .imc(calcularImc(request))
                .build();
        return pacienteMapper.entidadAResponse(pacienteRepository.save(paciente));
    }


    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = buscarMedicoActivoPorId(id);
        Paciente actualizado = pacienteMapper.requestAEntidad(request).toBuilder()
                .id(paciente.getId())
                .numeroExpediente(paciente.getNumeroExpediente())
                .estadoRegistro(paciente.getEstadoRegistro())
                .imc(calcularImc(request))
                .build();
        return pacienteMapper.entidadAResponse(pacienteRepository.save(actualizado));
    }


   /* @Transactional
    public PacienteResponse crear(@NotNull @Valid PacienteRequest request) {
        double imc = calcularImc(request);
        Paciente base = Paciente.builder().estadoRegistro(EstadoRegistro.ACTIVO)
                .numeroExpediente(UUID.randomUUID().toString().replace("-", "").substring(0, 20))
                .imc(imc).build();
        return mapper.toResponse(repository.save(mapper.conDatos(request, base)));
    }*/

   /* @Transactional
    public PacienteResponse actualizar(@Positive Long id, @NotNull @Valid PacienteRequest request) {
        Paciente actual = buscar(id);
        Paciente actualizado = mapper.conDatos(request, actual).toBuilder()
                .imc(calcularImc(request)).build();
        return mapper.toResponse(repository.save(actualizado));
    }*/

    @Override
    public void eliminar(Long id) {
        Paciente paciente = buscarMedicoActivoPorId(id);
        paciente.el
    }

    private Paciente buscarMedicoActivoPorId(Long id) {
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new PacienteNoEncontradoException(id));
    }

    private double calcularImc(PacienteRequest request) {
        double peso = request.peso();
        double estatura = request.estatura();
        if (!Double.isFinite(peso) || !Double.isFinite(estatura)
                || peso < 0.1 || peso > 200 || estatura < 1 || estatura > 2) {
            throw new ReglaNegocioException("Peso o estatura fuera del rango permitido");
        }
        double imc = peso / (estatura * estatura);
        if (imc < 10 || imc > 50) {
            throw new ReglaNegocioException("El IMC calculado debe estar entre 10 y 50");
        }
        return imc;
    }
}
