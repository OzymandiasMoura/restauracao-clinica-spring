package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "Funcionarios")
public class Funcionario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nome;
    @Column(unique = true, nullable = false)
    private String cpf;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private LocalDate dataNascimento;
    @Column(unique = true, nullable = false)
    private boolean ativo;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public void setNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {

        }
        this.nome = nome;
    }
}
