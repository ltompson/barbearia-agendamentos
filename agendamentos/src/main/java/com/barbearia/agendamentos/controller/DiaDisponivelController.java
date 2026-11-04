
package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.DiaDisponivel;
import com.barbearia.agendamentos.service.DiaDisponivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dias-disponiveis")
@CrossOrigin
@RequiredArgsConstructor
public class DiaDisponivelController {

    private final DiaDisponivelService service;

    @PostMapping
    public DiaDisponivel criar(
            @RequestBody DiaDisponivel dia,
            Authentication authentication
    ) {
        return service.criar(dia, authentication.getName());
    }

    // Consulta publica utilizada pelos clientes
    @GetMapping
    public List<DiaDisponivel> listar(@RequestParam String data) {
        return service.listarPorData(LocalDate.parse(data));
    }

    // Exclusao protegida
    @DeleteMapping("/{id}")
    public void deletar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        service.deletar(id, authentication.getName());
    }

    // Consulta publica utilizada no agendamento
    @GetMapping("/barbeiro/{barbeiroId}")
    public List<DiaDisponivel> listarPorBarbeiroPublico(
            @PathVariable Long barbeiroId
    ) {
        return service.listarPorBarbeiro(barbeiroId);
    }

    // Consulta autenticada para o painel administrativo
    @GetMapping("/gerenciar/barbeiro/{barbeiroId}")
    public List<DiaDisponivel> listarParaGerenciamento(
            @PathVariable Long barbeiroId,
            Authentication authentication
    ) {
        return service.listarPorBarbeiro(
                barbeiroId,
                authentication.getName()
        );
    }
}
