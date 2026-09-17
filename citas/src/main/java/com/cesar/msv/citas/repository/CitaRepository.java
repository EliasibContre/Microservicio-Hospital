package com.cesar.msv.citas.repository;

import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.msv.citas.entity.Cita;
import com.cesar.msv.citas.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository  extends JpaRepository<Cita, Long> {

    List<Cita>findByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idMedico,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estados,
            Long idCita);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente,
            EstadoRegistro estadoRegistro,
            List<EstadoCita>estados
    );

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estados
    );
    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idPaciente,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estados,
            Long idCita
    );

}
