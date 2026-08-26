package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public Cargo(String nome)
    {
        setNome(nome);
    }

    public Cargo(Long id, String nome)
    {
        setId(id);
        setNome(nome);
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
}
