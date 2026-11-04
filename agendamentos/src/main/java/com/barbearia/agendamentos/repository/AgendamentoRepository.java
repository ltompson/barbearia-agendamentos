
package com.barbearia.agendamentos.repository;

import com.barbearia.agendamentos.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Consulta agendamentos dentro de um intervalo de datas.
    List<Agendamento> findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
            Long barbeiroId,
            LocalDateTime inicio,
            LocalDateTime fim,
            String status
    );

    // Busca todos os agendamentos de um barbeiro dentro do dia.
    // Permite verificar sobreposição considerando a duração dos serviços.
    List<Agendamento> findByBarbeiroIdAndDataHoraGreaterThanEqualAndDataHoraLessThanAndStatusNot(
            Long barbeiroId,
            LocalDateTime inicio,
            LocalDateTime fim,
            String status
    );
}
