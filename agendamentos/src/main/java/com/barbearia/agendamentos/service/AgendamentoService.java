package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Agendamento;
import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.entity.Cliente;
import com.barbearia.agendamentos.entity.Servico;
import com.barbearia.agendamentos.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroService barbeiroService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;

    public Agendamento agendar(Long clienteId, Long barbeiroId,
                               Long servicoId, LocalDateTime dataHora) {

        Barbeiro barbeiro = barbeiroService.buscarPorId(barbeiroId);
        Servico servico = servicoService.buscarPorId(servicoId);
        Cliente cliente = clienteService.buscarPorId(clienteId);

        LocalDateTime fim = dataHora.plusMinutes(servico.getDuracaoMinutos());

        List<Agendamento> conflitos = agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetween(barbeiroId, dataHora, fim);

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Horário indisponível para este barbeiro");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setBarbeiro(barbeiro);
        agendamento.setServico(servico);
        agendamento.setDataHora(dataHora);

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarPorBarbeiro(Long barbeiroId) {
        return agendamentoRepository.findAll()
                .stream()
                .filter(a -> a.getBarbeiro().getId().equals(barbeiroId))
                .toList();
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Agendamento cancelar(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        agendamento.setStatus("CANCELADO");
        return agendamentoRepository.save(agendamento);
    }
}