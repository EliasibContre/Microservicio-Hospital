package com.cesar.ms_pacientes.repositories;

import com.cesar.ms_pacientes.entities.Paciente;
import com.cesar.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByEstadoRegistroOrderByIdAsc(EstadoRegistro estado);
    Optional<Paciente> findByIdAndEstadoRegistro(Long id, EstadoRegistro estado);
}
