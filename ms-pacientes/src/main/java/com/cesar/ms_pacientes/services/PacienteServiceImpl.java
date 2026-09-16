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
        return pacienteMapper.entidadAResponse(buscarPacienteActivoPorId(id));
    }

    @Override
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id)
                .orElseThrow(()->new RecursoNoEncontradoException(
                        "Paciente no encontrado con id: " + id)));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        double imc = calcularImc(request);
        Paciente paciente = pacienteMapper.requestAEntidad(request);

        String numeroExpediente=generarNumeroExpediente(request.telefono());
        paciente.asignarImcNumExpEstadoReg(imc, numeroExpediente, EstadoRegistro.ACTIVO);
        return pacienteMapper.entidadAResponse(pacienteRepository.save(paciente));
    }


    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = buscarPacienteActivoPorId(id);
        double imc = calcularImc(request);
        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad().shortValue(),
                request.peso(),
                request.estatura(),
                imc,
                request.email(),
                paciente.getNumeroExpediente(),
                request.telefono(),
                request.direccion(),
                paciente.getEstadoRegistro());
        return pacienteMapper.entidadAResponse(paciente);
    }


    @Override
    public void eliminar(Long id) {
        Paciente paciente = buscarPacienteActivoPorId(id);
        paciente.eliminar();
    }

    private Paciente buscarPacienteActivoPorId(Long id) {
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new PacienteNoEncontradoException(id));
    }

    private String generarNumeroExpediente(String telefono){
        StringBuilder expediente = new StringBuilder();
        for (char digito : telefono.toCharArray()){
            expediente.append(digito).append('X');
        }
        return expediente.toString();
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
