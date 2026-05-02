package com.barbearia.agendamentos.repository;

import com.barbearia.agendamentos.entity.DiaDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DiaDisponivelRepository extends JpaRepository<DiaDisponivel, Long> {
    List<DiaDisponivel> findByData(LocalDate data);
    boolean existsByDataAndBarbeiroId(LocalDate data, Long barbeiroId);
    List<DiaDisponivel> findByBarbeiroId(Long barbeiroId);
}