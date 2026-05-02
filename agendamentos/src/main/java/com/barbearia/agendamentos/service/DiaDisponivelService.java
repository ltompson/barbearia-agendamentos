package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.DiaDisponivel;
import com.barbearia.agendamentos.repository.DiaDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaDisponivelService {

    private final DiaDisponivelRepository repository;

    public DiaDisponivel criar(DiaDisponivel dia) {
        return repository.save(dia);
    }

    public List<DiaDisponivel> listarPorData(LocalDate data) {
        return repository.findByData(data);
    }

    public boolean isDisponivelParaBarbeiro(LocalDate data, Long barbeiroId) {
        return repository.existsByDataAndBarbeiroId(data, barbeiroId);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public List<DiaDisponivel> listarPorBarbeiro(Long barbeiroId) {
        return repository.findByBarbeiroId(barbeiroId);
    }
}