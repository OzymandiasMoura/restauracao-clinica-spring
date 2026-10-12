package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>
{
    Optional<Funcionario> findFuncionarioByCpf(String cpf);
    Optional<Funcionario> findFuncionarioByEmail(String email);
    Optional<Funcionario> findFuncionarioByUser(User user);
    Optional<Funcionario> findFuncionarioById(Long id);
    boolean existsByCargoAndAtivoTrue(Cargo cargo);

}
