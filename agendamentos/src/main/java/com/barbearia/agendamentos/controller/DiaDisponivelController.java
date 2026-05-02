package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.entity.DiaDisponivel;
import com.barbearia.agendamentos.service.DiaDisponivelService;
import lombok.RequiredArgsConstructor;
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
    public DiaDisponivel criar(@RequestBody DiaDisponivel dia) {
        return service.criar(dia);
    }

    @GetMapping
    public List<DiaDisponivel> listar(@RequestParam String data) {
        return service.listarPorData(LocalDate.parse(data));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/barbeiro/{barbeiroId}")
    public List<DiaDisponivel> listarPorBarbeiro(@PathVariable Long barbeiroId) {
        return service.listarPorBarbeiro(barbeiroId);
    }
}