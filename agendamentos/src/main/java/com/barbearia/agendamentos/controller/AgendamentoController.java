package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Agendamento;
import com.barbearia.agendamentos.service.AgendamentoService;
import com.barbearia.agendamentos.service.ClienteService;
import com.barbearia.agendamentos.entity.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import com.barbearia.agendamentos.service.DiaDisponivelService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final ClienteService clienteService;
    private final DiaDisponivelService diaDisponivelService;

    // Retorna todos os agendamentos (painel admin)
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos(
            Authentication authentication) {

        String usuario = authentication.getName();

        return ResponseEntity.ok(
                agendamentoService.listarPermitidos(usuario)
        );
    }

    // Retorna agendamentos de um barbeiro específico
    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<Agendamento>> listarPorBarbeiro(
            @PathVariable Long barbeiroId,
            Authentication authentication) {

        String usuario = authentication.getName();

        agendamentoService.validarAcessoAoBarbeiro(usuario, barbeiroId);

        return ResponseEntity.ok(
                agendamentoService.listarPorBarbeiro(barbeiroId)
        );
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<LocalTime>> getHorariosDisponiveis(
            @RequestParam String data,
            @RequestParam Long barbeiroId) {
        LocalDate localDate = LocalDate.parse(data);
        List<LocalTime> horarios = agendamentoService.getHorariosDisponiveis(localDate, barbeiroId);
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/dia-disponivel")
    public ResponseEntity<Boolean> isDiaDisponivel(
            @RequestParam String data,
            @RequestParam Long barbeiroId) {
        LocalDate localDate = LocalDate.parse(data);
        boolean disponivel = diaDisponivelService.isDisponivelParaBarbeiro(localDate, barbeiroId);
        return ResponseEntity.ok(disponivel);
    }

    // Busca agendamento por ID (usado no reagendamento)
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Agendamento agendamento = agendamentoService.buscarPorId(id);

        agendamentoService.validarAcessoAoBarbeiro(
                authentication.getName(),
                agendamento.getBarbeiro().getId()
        );

        return ResponseEntity.ok(agendamento);
    }

    @GetMapping("/dias-bloqueados")
    public ResponseEntity<List<LocalDate>> getDiasBloqueados(@RequestParam Long barbeiroId) {
        return ResponseEntity.ok(agendamentoService.getDiasBloqueados(barbeiroId));
    }

    // Reagenda — cancela o antigo e cria novo
    // Reagenda — cancela o antigo e cria novo
    @PostMapping("/{id}/reagendar")
    public ResponseEntity<Agendamento> reagendar(
            @PathVariable Long id,
            @RequestBody AgendamentoRequest request,
            Authentication authentication) {

        String usuario = authentication.getName();

        // Verifica o barbeiro do agendamento original.
        Agendamento antigo = agendamentoService.buscarPorId(id);

        agendamentoService.validarAcessoAoBarbeiro(
                usuario,
                antigo.getBarbeiro().getId()
        );

        // Verifica também o barbeiro escolhido para o novo horário.
        agendamentoService.validarAcessoAoBarbeiro(
                usuario,
                request.barbeiroId()
        );

        return ResponseEntity.ok(
                agendamentoService.reagendar(
                        id,
                        request.barbeiroId(),
                        request.servicoId(),
                        request.dataHora()
                )
        );
    }

    // Cria um novo agendamento recebendo os dados do cliente diretamente
    @PostMapping
    public ResponseEntity<Agendamento> agendar(@RequestBody AgendamentoRequest request) {

        // Busca ou cria o cliente pelo telefone
        Cliente cliente = clienteService.buscarOuCriarPorTelefone(
                request.nomeCliente(),
                request.telefoneCliente()
        );

        return ResponseEntity.ok(
                agendamentoService.agendar(
                        cliente.getId(),
                        request.barbeiroId(),
                        request.servicoId(),
                        request.dataHora()
                )
        );
    }

    // Cancela um agendamento pelo ID
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Agendamento> cancelar(
            @PathVariable Long id,
            Authentication authentication) {

        Agendamento agendamento = agendamentoService.buscarPorId(id);

        agendamentoService.validarAcessoAoBarbeiro(
                authentication.getName(),
                agendamento.getBarbeiro().getId()
        );

        return ResponseEntity.ok(agendamentoService.cancelar(id));
    }

    // Estrutura dos dados recebidos do Angular
    record AgendamentoRequest(
            String nomeCliente,
            String telefoneCliente,
            Long barbeiroId,
            Long servicoId,
            LocalDateTime dataHora
    ) {}
}