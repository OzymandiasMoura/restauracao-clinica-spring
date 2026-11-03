
package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "preco", nullable = false)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @Column(name = "quantidade_minima", nullable = false)
    private int quantidadeMinima;

    public Medicamento(
            String nome,
            BigDecimal preco,
            int quantidadeEstoque,
            int quantidadeMinima
    ) {
        setNome(nome);
        setPreco(preco);
        setQuantidadeEstoque(quantidadeEstoque);
        setQuantidadeMinima(quantidadeMinima);
    }

    public void setNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new MedicamentoWithInvalidInformationException(
                    "O nome do medicamento não pode ser nulo ou vazio."
            );
        }

        String nomeFormatado = nome.trim()
                .replaceAll("\\s+", " ");

        if (nomeFormatado.length() < 3) {
            throw new MedicamentoWithInvalidInformationException(
                    "O nome deve possuir pelo menos 3 caracteres."
            );
        }

        if (nomeFormatado.length() > 255) {
            throw new MedicamentoWithInvalidInformationException(
                    "O nome não pode ultrapassar 255 caracteres."
            );
        }

        this.nome = nomeFormatado;
    }
    public void setPreco(BigDecimal preco) {
        if (preco == null) {
            throw new MedicamentoWithInvalidInformationException(
                    "O preço é obrigatório."
            );
        }

        if (preco.signum() < 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "O preço não pode ser negativo."
            );
        }

        if (preco.scale() > 2) {
            throw new MedicamentoWithInvalidInformationException(
                    "O preço deve possuir no máximo duas casas decimais."
            );
        }

        this.preco = preco;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {

        if (quantidadeEstoque < 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade em estoque não pode ser negativa."
            );
        }

        this.quantidadeEstoque = quantidadeEstoque;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {

        if (quantidadeMinima < 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade mínima não pode ser negativa."
            );
        }

        this.quantidadeMinima = quantidadeMinima;
    }
    public void adicionarEstoque(int quantidade) {

        if (quantidade <= 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade a adicionar deve ser maior que zero."
            );
        }

        if (quantidade > Integer.MAX_VALUE - this.quantidadeEstoque) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade ultrapassa o limite permitido de estoque."
            );
        }

        this.quantidadeEstoque += quantidade;
    }

    public void baixarEstoque(int quantidade) {

        if (quantidade <= 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade a baixar deve ser maior que zero."
            );
        }

        if (quantidade > this.quantidadeEstoque) {
            throw new MedicamentoWithInvalidInformationException(
                    "Estoque insuficiente. Disponível: "
                            + this.quantidadeEstoque + " unidades."
            );
        }

        this.quantidadeEstoque -= quantidade;
    }
    public boolean estoqueEmAlerta() {
        return this.quantidadeEstoque <= this.quantidadeMinima;
    }
}
