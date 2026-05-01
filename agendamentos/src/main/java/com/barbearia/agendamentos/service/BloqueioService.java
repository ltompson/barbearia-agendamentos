package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.repository.BloqueioRepository;
import com.barbearia.agendamentos.entity.Bloqueio;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BloqueioService {

    private final BloqueioRepository repository;

    public BloqueioService(BloqueioRepository repository) {
        this.repository = repository;
    }

    public Bloqueio criar(Bloqueio bloqueio) {
        return repository.save(bloqueio);
    }

    public List<Bloqueio> listarPorData(LocalDate data) {
        return repository.findByData(data);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}