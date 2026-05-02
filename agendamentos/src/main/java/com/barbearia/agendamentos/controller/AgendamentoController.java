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
    public ResponseEntity<List<Agendamento>> listarTodos() {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    // Retorna agendamentos de um barbeiro específico
    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<Agendamento>> listarPorBarbeiro(
            @PathVariable Long barbeiroId) {
        return ResponseEntity.ok(agendamentoService.listarPorBarbeiro(barbeiroId));
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
    public ResponseEntity<Agendamento> cancelar(@PathVariable Long id) {
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