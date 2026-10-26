package org.example.clinicarestauracao.Domain.Entities;


import jakarta.persistence.*;
import lombok.*;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;

@Entity
@Getter
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

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @Column(nullable = false)
    private int quantidadeMinima;



    public Medicamento(Long id, String nome, double preco, int quantidadeEstoque, int quantidadeMinima) {
        this.id = id;
        setNome(nome);
        setPreco(preco);
        setQuantidadeEstoque(quantidadeEstoque);
        setQuantidadeMinima(quantidadeMinima);
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
    public void setQuantidadeEstoque(int quantidadeEstoque){

        if (quantidadeEstoque < 0){
            throw new MedicamentoWithInvalidInformationException("O estoque não pode ser menor que 0.");
        } else {
            this.quantidadeEstoque = quantidadeEstoque;
        }

    }
    public void setQuantidadeMinima(int quantidadeMinima) {
        if (quantidadeMinima < 0) {
            throw new MedicamentoWithInvalidInformationException("A quantidade mínima para alerta não pode ser negativa.");
        } else {
            this.quantidadeMinima = quantidadeMinima;
        }
    }

    public void adicionarEstoque(int quantidade) {

        if (quantidade <= 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade deve ser maior que zero."
            );

        }

        if (quantidade > Integer.MAX_VALUE - this.quantidadeEstoque) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade ultrapassa o limite permitido."
            );
        }

        this.quantidadeEstoque += quantidade;
    }

    public void baixarEstoque(int quantidade) {

        if (quantidade <= 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade deve ser maior que zero."
            );
        }


        if (quantidade > this.quantidadeEstoque) {
            throw new MedicamentoWithInvalidInformationException(
                    "Estoque insuficiente. Disponível: "
                            + this.quantidadeEstoque
            );
        }

        this.quantidadeEstoque -= quantidade;
    }
}
