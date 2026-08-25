package org.example.clinicarestauracao.Domain.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "preco", nullable = false)
    private double preco;

    public Medicamento(Long id, String nome, double preco) {
        this.id = id;
        setNome(nome);
        setPreco(preco);
    }

    public Medicamento(String nome, double preco) {
        setNome(nome);
        setPreco(preco);
    }

    public void setNome(String nome){
        if (nome == null || nome.isBlank()){
            throw new MedicamentoWithInvalidInformationException("O nome não pode ser nulo ou vazia.");
        } else if(nome.length() < 3){
            throw new MedicamentoWithInvalidInformationException("O nome não pode ser menor que 3 caracteres.");
        } else{
            this.nome = nome;
        }
    }

    public void setPreco(double preco){
        if (preco < 0){
            throw new MedicamentoWithInvalidInformationException("O preço não pode ser negativo.");
        } else {
            this.preco = preco;
        }
    }

}
