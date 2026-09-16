package com.cesar.msv.citas.repository;

import com.cesar.commons.enums.EstadoRegistro;
import com.cesar.msv.citas.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository  extends JpaRepository<Cita, Long> {

    List<Cita>findByEstadoRegistro(EstadoRegistro estadoRegistro);

}
