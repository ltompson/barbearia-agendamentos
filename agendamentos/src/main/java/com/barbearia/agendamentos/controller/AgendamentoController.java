package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.Agendamento;
import com.barbearia.agendamentos.service.AgendamentoService;
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

    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<Agendamento>> listarPorBarbeiro(
            @PathVariable Long barbeiroId) {
        return ResponseEntity.ok(agendamentoService.listarPorBarbeiro(barbeiroId));
    }

    @PostMapping
    public ResponseEntity<Agendamento> agendar(
            @RequestParam Long clienteId,
            @RequestParam Long barbeiroId,
            @RequestParam Long servicoId,
            @RequestParam LocalDateTime dataHora) {
        return ResponseEntity.ok(
                agendamentoService.agendar(clienteId, barbeiroId, servicoId, dataHora)
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Agendamento> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.cancelar(id));
    }
}