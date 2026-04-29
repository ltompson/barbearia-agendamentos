package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Agendamento;
import com.barbearia.agendamentos.service.AgendamentoService;
import com.barbearia.agendamentos.service.ClienteService;
import com.barbearia.agendamentos.entity.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final ClienteService clienteService;

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