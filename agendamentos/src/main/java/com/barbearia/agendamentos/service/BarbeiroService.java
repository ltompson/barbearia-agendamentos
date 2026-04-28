package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.repository.BarbeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarbeiroService {

    private final BarbeiroRepository barbeiroRepository;

    public List<Barbeiro> listarAtivos() {
        return barbeiroRepository.findAll()
                .stream()
                .filter(b -> b.getAtivo())
                .toList();
    }

    public Barbeiro buscarPorId(Long id) {
        return barbeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));
    }

    public Barbeiro salvar(Barbeiro barbeiro) {
        return barbeiroRepository.save(barbeiro);
    }

    public Barbeiro atualizar(Long id, Barbeiro barbeiro) {
        Barbeiro existing = buscarPorId(id);
        existing.setNome(barbeiro.getNome());
        existing.setTelefone(barbeiro.getTelefone());
        existing.setAtivo(barbeiro.getAtivo());
        return barbeiroRepository.save(existing);
    }
}