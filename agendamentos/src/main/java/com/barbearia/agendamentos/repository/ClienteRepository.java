package com.barbearia.agendamentos.repository;

import com.barbearia.agendamentos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca um cliente pelo número de telefone
    Optional<Cliente> findByTelefone(String telefone);
}