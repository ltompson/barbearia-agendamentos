package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Agendamento;
import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.entity.Cliente;
import com.barbearia.agendamentos.entity.Servico;
import com.barbearia.agendamentos.repository.AgendamentoRepository;
import com.barbearia.agendamentos.repository.BloqueioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final BloqueioRepository bloqueioRepository;
    private final BarbeiroService barbeiroService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;

    // ─── Configuração dos slots do dia ──────────────────────────────────────────
    private static final LocalTime INICIO_EXPEDIENTE = LocalTime.of(8, 0);
    private static final LocalTime FIM_EXPEDIENTE    = LocalTime.of(19, 0);
    private static final int INTERVALO_MINUTOS       = 30;

    // ─── Horários disponíveis ────────────────────────────────────────────────────
    public List<LocalTime> getHorariosDisponiveis(LocalDate data, Long barbeiroId) {

        LocalDateTime inicioDia = data.atTime(INICIO_EXPEDIENTE);
        LocalDateTime fimDia    = data.atTime(FIM_EXPEDIENTE);

        // Horários já agendados (ignora cancelados)
        Set<LocalTime> agendados = agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
                        barbeiroId, inicioDia, fimDia, "CANCELADO")
                .stream()
                .map(a -> a.getDataHora().toLocalTime())
                .collect(Collectors.toSet());

        // Horários bloqueados pelo admin
        Set<LocalTime> bloqueados = bloqueioRepository
                .findByDataAndBarbeiroId(data, barbeiroId)
                .stream()
                .map(b -> b.getHorario())
                .collect(Collectors.toSet());

        // Gera todos os slots e filtra os ocupados
        return gerarSlots().stream()
                .filter(slot -> !agendados.contains(slot))
                .filter(slot -> !bloqueados.contains(slot))
                .toList();
    }

    // ─── Gerador de slots ────────────────────────────────────────────────────────
    private List<LocalTime> gerarSlots() {
        List<LocalTime> slots = new java.util.ArrayList<>();
        LocalTime atual = INICIO_EXPEDIENTE;
        while (atual.isBefore(FIM_EXPEDIENTE)) {
            slots.add(atual);
            atual = atual.plusMinutes(INTERVALO_MINUTOS);
        }
        return slots;
    }

    // ─── Agendar ─────────────────────────────────────────────────────────────────
    public Agendamento agendar(Long clienteId, Long barbeiroId,
                               Long servicoId, LocalDateTime dataHora) {

        Barbeiro barbeiro = barbeiroService.buscarPorId(barbeiroId);
        Servico servico   = servicoService.buscarPorId(servicoId);
        Cliente cliente   = clienteService.buscarPorId(clienteId);

        LocalDateTime fim = dataHora.plusMinutes(servico.getDuracaoMinutos());

        // Verifica conflito (ignora cancelados)
        List<Agendamento> conflitos = agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
                        barbeiroId, dataHora, fim, "CANCELADO");

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Horário indisponível para este barbeiro");
        }

        // Verifica se o horário está bloqueado
        boolean horarioBloqueado = bloqueioRepository
                .findByDataAndBarbeiroId(dataHora.toLocalDate(), barbeiroId)
                .stream()
                .anyMatch(b -> b.getHorario().equals(dataHora.toLocalTime()));

        if (horarioBloqueado) {
            throw new RuntimeException("Este horário está bloqueado pelo administrador");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setBarbeiro(barbeiro);
        agendamento.setServico(servico);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus("AGENDADO");

        return agendamentoRepository.save(agendamento);
    }

    // ─── Listagens ───────────────────────────────────────────────────────────────
    public List<Agendamento> listarPorBarbeiro(Long barbeiroId) {
        // Corrigido: não faz mais findAll() + filter em memória
        LocalDateTime inicio = LocalDateTime.now().minusYears(1);
        LocalDateTime fim    = LocalDateTime.now().plusYears(1);
        return agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
                        barbeiroId, inicio, fim, "NENHUM");
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