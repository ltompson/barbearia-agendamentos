package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Cliente;
import com.barbearia.agendamentos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Busca cliente pelo telefone ou cria um novo se não existir
    public Cliente buscarOuCriarPorTelefone(String nome, String telefone) {
        return clienteRepository.findByTelefone(telefone)
                .orElseGet(() -> {
                    Cliente novo = new Cliente();
                    novo.setNome(nome);
                    novo.setTelefone(telefone);
                    return clienteRepository.save(novo);
                });
    }
}