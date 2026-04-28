package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Servico;
import com.barbearia.agendamentos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public List<Servico> listarAtivos() {
        return servicoRepository.findAll()
                .stream()
                .filter(s -> s.getAtivo())
                .toList();
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    public Servico atualizar(Long id, Servico servico) {
        Servico existing = buscarPorId(id);
        existing.setNome(servico.getNome());
        existing.setPreco(servico.getPreco());
        existing.setDuracaoMinutos(servico.getDuracaoMinutos());
        existing.setAtivo(servico.getAtivo());
        return servicoRepository.save(existing);
    }
}