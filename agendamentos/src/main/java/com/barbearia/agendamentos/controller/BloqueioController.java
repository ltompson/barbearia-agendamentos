package com.barbearia.agendamentos.controller;

import com.barbearia.agendamentos.service.BloqueioService;
import com.barbearia.agendamentos.entity.Bloqueio;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bloqueios")
@CrossOrigin
public class BloqueioController {

    private final BloqueioService service;

    public BloqueioController(BloqueioService service) {
        this.service = service;
    }

    @PostMapping
    public Bloqueio criar(@RequestBody Bloqueio bloqueio) {
        return service.criar(bloqueio);
    }

    @GetMapping
    public List<Bloqueio> listar(@RequestParam String data) {
        return service.listarPorData(LocalDate.parse(data));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
