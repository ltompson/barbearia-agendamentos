
package com.barbearia.agendamentos.service;

import com.barbearia.agendamentos.entity.Admin;
import com.barbearia.agendamentos.entity.Barbeiro;
import com.barbearia.agendamentos.entity.DiaDisponivel;
import com.barbearia.agendamentos.repository.BarbeiroRepository;
import com.barbearia.agendamentos.repository.DiaDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaDisponivelService {

    private final DiaDisponivelRepository repository;
    private final BarbeiroRepository barbeiroRepository;
    private final AdminService adminService;

    public DiaDisponivel criar(DiaDisponivel dia, String usuario) {

        if (dia == null) {
            throw new IllegalArgumentException(
                    "Os dados da disponibilidade sao obrigatorios."
            );
        }

        if (dia.getData() == null) {
            throw new IllegalArgumentException(
                    "A data e obrigatoria."
            );
        }

        if (dia.getBarbeiroId() == null) {
            throw new IllegalArgumentException(
                    "O barbeiro e obrigatorio."
            );
        }

        if (dia.getData().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Nao e permitido cadastrar datas passadas."
            );
        }

        Admin admin = validarUsuario(usuario);

        Long barbeiroId;

        if ("ADMIN".equalsIgnoreCase(admin.getRole())) {
            barbeiroId = dia.getBarbeiroId();
        } else if ("FUNCIONARIO".equalsIgnoreCase(admin.getRole())) {

            if (admin.getBarbeiroId() == null) {
                throw new AccessDeniedException(
                        "Funcionario sem barbeiro associado."
                );
            }

            barbeiroId = admin.getBarbeiroId();

        } else {
            throw new AccessDeniedException(
                    "Perfil sem permissao."
            );
        }

        Barbeiro barbeiro = barbeiroRepository
                .findById(barbeiroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Barbeiro nao encontrado."
                ));

        if (!Boolean.TRUE.equals(barbeiro.getAtivo())) {
            throw new IllegalArgumentException(
                    "Nao e permitido cadastrar disponibilidade para barbeiro inativo."
            );
        }

        boolean duplicado =
                repository.existsByDataAndHorarioAndBarbeiroId(
                        dia.getData(),
                        dia.getHorario(),
                        barbeiroId
                );

        if (duplicado) {
            throw new IllegalArgumentException(
                    "Esta disponibilidade ja esta cadastrada."
            );
        }

        dia.setBarbeiroId(barbeiroId);

        return repository.save(dia);
    }

    public List<DiaDisponivel> listarPorData(LocalDate data) {
        return repository.findByData(data);
    }

    public boolean isDisponivelParaBarbeiro(
            LocalDate data,
            Long barbeiroId
    ) {
        return repository.existsByDataAndBarbeiroId(data, barbeiroId);
    }

    public void deletar(Long id, String usuario) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Identificador da disponibilidade obrigatorio."
            );
        }

        DiaDisponivel dia = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Disponibilidade nao encontrada."
                ));

        Admin admin = validarUsuario(usuario);

        if ("FUNCIONARIO".equalsIgnoreCase(admin.getRole())) {

            if (admin.getBarbeiroId() == null
                    || !admin.getBarbeiroId().equals(dia.getBarbeiroId())) {
                throw new AccessDeniedException(
                        "Voce nao pode excluir a disponibilidade de outro barbeiro."
                );
            }

        } else if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new AccessDeniedException(
                    "Perfil sem permissao."
            );
        }

        repository.delete(dia);
    }

    public List<DiaDisponivel> listarPorBarbeiro(Long barbeiroId) {
        return repository.findByBarbeiroId(barbeiroId);
    }

    public List<DiaDisponivel> listarPorBarbeiro(
            Long barbeiroId,
            String usuario
    ) {

        Admin admin = validarUsuario(usuario);

        if ("FUNCIONARIO".equalsIgnoreCase(admin.getRole())) {

            if (admin.getBarbeiroId() == null
                    || !admin.getBarbeiroId().equals(barbeiroId)) {
                throw new AccessDeniedException(
                        "Voce nao pode consultar a disponibilidade de outro barbeiro."
                );
            }

        } else if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new AccessDeniedException(
                    "Perfil sem permissao."
            );
        }

        return repository.findByBarbeiroId(barbeiroId);
    }

    private Admin validarUsuario(String usuario) {

        Admin admin = adminService.buscarPorUsuario(usuario);

        if (!Boolean.TRUE.equals(admin.getAtivo())) {
            throw new AccessDeniedException("Conta inativa.");
        }

        return admin;
    }
}
