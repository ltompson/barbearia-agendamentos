package com.barbearia.agendamentos.repository;

import com.barbearia.agendamentos.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByBarbeiroIdAndDataHoraBetween(
            Long barbeiroId,
            LocalDateTime inicio,
            LocalDateTime fim
    );
}