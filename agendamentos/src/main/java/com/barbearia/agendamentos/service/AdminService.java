package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.repository.AdminRepository;
import com.barbearia.agendamentos.repository.BarbeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final BarbeiroRepository barbeiroRepository;

    public Admin salvar(Admin admin) {

        if (admin.getNome() == null || admin.getNome().isBlank()
                || admin.getUsuario() == null || admin.getUsuario().isBlank()
                || admin.getSenha() == null || admin.getSenha().isBlank()) {
            throw new IllegalArgumentException(
                    "Nome, usuario e senha sao obrigatorios."
            );
        }

        if (adminRepository.findByUsuario(admin.getUsuario().trim()).isPresent()) {
            throw new IllegalArgumentException("Usuario ja cadastrado.");
        }

        // Valida o barbeiro associado ao funcionario.
        validarBarbeiro(admin.getBarbeiroId());

        admin.setNome(admin.getNome().trim());
        admin.setUsuario(admin.getUsuario().trim());
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));

        // Cadastro por este serviço sempre cria funcionário.
        admin.setRole("FUNCIONARIO");
        admin.setAtivo(true);

        return adminRepository.save(admin);
    }

    public Admin buscarPorUsuario(String usuario) {
        return adminRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Admin nao encontrado"));
    }

    public List<Admin> listarTodos() {
        return adminRepository.findAll();
    }

    public Admin toggleAtivo(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin nao encontrado"));

        admin.setAtivo(!Boolean.TRUE.equals(admin.getAtivo()));

        return adminRepository.save(admin);
    }

    public void deletar(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Admin nao encontrado");
        }

        adminRepository.deleteById(id);
    }

    public Admin atualizar(Long id, Admin dados) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin nao encontrado"));

        if (dados.getNome() == null || dados.getNome().isBlank()
                || dados.getUsuario() == null || dados.getUsuario().isBlank()) {
            throw new IllegalArgumentException(
                    "Nome e usuario sao obrigatorios."
            );
        }

        String novoUsuario = dados.getUsuario().trim();

        if (!admin.getUsuario().equals(novoUsuario)
                && adminRepository.findByUsuario(novoUsuario).isPresent()) {
            throw new IllegalArgumentException("Usuario ja cadastrado.");
        }

        // Valida o novo barbeiro associado ao funcionario.
        validarBarbeiro(dados.getBarbeiroId());

        admin.setNome(dados.getNome().trim());
        admin.setUsuario(novoUsuario);
        admin.setBarbeiroId(dados.getBarbeiroId());

        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            admin.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        // O perfil e o estado da conta nao podem ser alterados pela edicao.
        return adminRepository.save(admin);
    }

    private void validarBarbeiro(Long barbeiroId) {

        if (barbeiroId == null) {
            throw new IllegalArgumentException(
                    "O barbeiro associado e obrigatorio."
            );
        }

        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Barbeiro nao encontrado."
                ));

        if (!Boolean.TRUE.equals(barbeiro.getAtivo())) {
            throw new IllegalArgumentException(
                    "Nao e possivel associar um funcionario a um barbeiro inativo."
            );
        }
    }
}