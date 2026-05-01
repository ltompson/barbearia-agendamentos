package com.barbearia.agendamentos.repository;

import com.barbearia.agendamentos.entity.Bloqueio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BloqueioRepository extends JpaRepository<Bloqueio, Long> {

    List<Bloqueio> findByData(LocalDate data);
    List<Bloqueio> findByDataAndBarbeiroId(LocalDate data, Long barbeiroId);
}