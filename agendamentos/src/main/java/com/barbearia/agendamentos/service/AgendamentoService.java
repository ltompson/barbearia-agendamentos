package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.service.AdminService;
import org.springframework.security.access.AccessDeniedException;
import com.barbearia.agendamentos.entity.Agendamento;
import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.entity.Cliente;
import com.barbearia.agendamentos.entity.Servico;
import com.barbearia.agendamentos.entity.Bloqueio;
import com.barbearia.agendamentos.service.AdminService;
import com.barbearia.agendamentos.repository.AgendamentoRepository;
import com.barbearia.agendamentos.repository.BloqueioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final BloqueioRepository bloqueioRepository;
    private final BarbeiroService barbeiroService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;
    private final AdminService adminService;

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

        // Bloqueios do admin (horário vazio = dia inteiro)
        List<Bloqueio> bloqueiosDoDia = bloqueioRepository
                .findByDataAndBarbeiroId(data, barbeiroId);

        if (bloqueiosDoDia.stream().anyMatch(b -> b.getHorario() == null)) {
            return List.of();
        }

        Set<LocalTime> bloqueados = bloqueiosDoDia.stream()
                .map(Bloqueio::getHorario)
                .collect(Collectors.toSet());

        // Gera todos os slots e filtra os ocupados
        return gerarSlots().stream()
                .filter(slot -> !agendados.contains(slot))
                .filter(slot -> !bloqueados.contains(slot))
                .toList();
    }
    // ─── Dias bloqueados (dia inteiro) ───────────────────────────────────────────
    public List<LocalDate> getDiasBloqueados(Long barbeiroId) {
        return bloqueioRepository
                .findByBarbeiroIdAndHorarioIsNullAndDataGreaterThanEqual(barbeiroId, LocalDate.now())
                .stream()
                .map(Bloqueio::getData)
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

        // Verifica se o horário (ou o dia inteiro) está bloqueado
        boolean horarioBloqueado = bloqueioRepository
                .findByDataAndBarbeiroId(dataHora.toLocalDate(), barbeiroId)
                .stream()
                .anyMatch(b -> b.getHorario() == null
                        || b.getHorario().equals(dataHora.toLocalTime()));

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

    public List<Agendamento> listarPermitidos(String usuario) {

        Admin admin = adminService.buscarPorUsuario(usuario);

        if (!Boolean.TRUE.equals(admin.getAtivo())) {
            throw new AccessDeniedException("Conta inativa.");
        }

        if ("ADMIN".equalsIgnoreCase(admin.getRole())) {
            return listarTodos();
        }

        if ("FUNCIONARIO".equalsIgnoreCase(admin.getRole())) {

            if (admin.getBarbeiroId() == null) {
                throw new AccessDeniedException(
                        "Funcionario sem barbeiro associado."
                );
            }

            return listarPorBarbeiro(admin.getBarbeiroId());
        }

        throw new AccessDeniedException("Perfil sem permissao.");
    }

    // ─── Buscar por ID ───────────────────────────────────────────────────────────
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
    }

    // ─── Reagendar ───────────────────────────────────────────────────────────────
    @Transactional
    public Agendamento reagendar(Long idAntigo, Long barbeiroId,
                                 Long servicoId, LocalDateTime novaDataHora) {

        Agendamento antigo = buscarPorId(idAntigo);

        // Cria o novo agendamento primeiro.
        Agendamento novo = agendar(
                antigo.getCliente().getId(),
                barbeiroId,
                servicoId,
                novaDataHora
        );

        // Cancela o antigo somente depois da criação bem-sucedida.
        antigo.setStatus("CANCELADO");
        agendamentoRepository.save(antigo);

        return novo;
    }

    public Agendamento cancelar(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if ("CANCELADO".equalsIgnoreCase(agendamento.getStatus())) {
            throw new IllegalStateException("Este agendamento já está cancelado.");
        }

        agendamento.setStatus("CANCELADO");

        return agendamentoRepository.save(agendamento);
    }

    public void validarAcessoAoBarbeiro(String usuario, Long barbeiroId) {

        Admin admin = adminService.buscarPorUsuario(usuario);

        if (!Boolean.TRUE.equals(admin.getAtivo())) {
            throw new AccessDeniedException("Conta inativa.");
        }

        if ("ADMIN".equalsIgnoreCase(admin.getRole())) {
            return;
        }

        if ("FUNCIONARIO".equalsIgnoreCase(admin.getRole())) {

            if (admin.getBarbeiroId() == null
                    || !admin.getBarbeiroId().equals(barbeiroId)) {
                throw new AccessDeniedException(
                        "Voce nao tem permissao para acessar os agendamentos deste barbeiro."
                );
            }

            return;
        }

        throw new AccessDeniedException("Perfil sem permissao.");
    }
}