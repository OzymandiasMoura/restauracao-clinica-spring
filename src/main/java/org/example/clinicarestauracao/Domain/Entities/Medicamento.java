package org.example.clinicarestauracao.Domain.Entities;


import jakarta.persistence.*;
import lombok.*;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;

@Entity
@Getter
@Setter
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

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

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
            throw new MedicamentoWithInvalidInformationException("O nome não pode ser nulo ou vazio.");
        } else if(nome.length() < 3){
            throw new MedicamentoWithInvalidInformationException("O nome não pode ser menor que 3 caracteres.");
        } else{
            this.nome = nome;
        }
    }

    public void setPreco(double preco){
        if (preco < 0){
            throw new MedicamentoWithInvalidInformationException("O preço não pode ser menor que 0.");
        } else {
            this.preco = preco;
        }
    }

    public void inativar() {
        this.ativo = false;
    }
}
