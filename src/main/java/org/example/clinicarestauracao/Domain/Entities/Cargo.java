package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;

@Entity
@NoArgsConstructor
@Data
@Table(name = "Cargos")
public class Cargo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private boolean ativo;

    public Cargo(String nome)
    {
        setNome(nome);
        setAtivo(true);
    }

    public Cargo(Long id, String nome, boolean ativo)
    {
        setId(id);
        setNome(nome);
        setAtivo(ativo);
    }

    public void setNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {
            throw new CargoWithInvalidInformationException("Nome do cargo não pode ser nulo ou vazio.");
        }

        String nomeNormalized = nome.strip();

        if (nomeNormalized.length() < 3)
        {
            throw new CargoWithInvalidInformationException("Nome deve ter pelo menos 3 caracteres.");
        }

        this.nome = nomeNormalized;
    }

    public void deactivate()
    {
        setAtivo(false);
    }

    public void activate()
    {
        setAtivo(true);
    }
}
