package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin salvar(Admin admin) {
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        admin.setAtivo(true);
        if (admin.getRole() == null) admin.setRole("FUNCIONARIO");
        return adminRepository.save(admin);
    }

    public Admin buscarPorUsuario(String usuario) {
        return adminRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
    }

    public List<Admin> listarTodos() {
        return adminRepository.findAll();
    }

    public Admin toggleAtivo(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        admin.setAtivo(!admin.getAtivo());
        return adminRepository.save(admin);
    }

    public void deletar(Long id) {
        adminRepository.deleteById(id);
    }

    public Admin atualizar(Long id, Admin dados) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        admin.setNome(dados.getNome());
        admin.setUsuario(dados.getUsuario());
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            admin.setSenha(passwordEncoder.encode(dados.getSenha()));
        }
        if (dados.getRole() != null) admin.setRole(dados.getRole());
        return adminRepository.save(admin);
    }
}