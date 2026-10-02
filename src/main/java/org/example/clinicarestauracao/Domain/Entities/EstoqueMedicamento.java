package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.clinicarestauracao.Application.Exceptions.EstoqueMedicamento.EstoqueMedicamentoWithInvalidInformationException;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "EstoqueMedicamento")
public class EstoqueMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @Column(nullable = false)
    private int quantidadeMinimaAlerta;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;

    public EstoqueMedicamento(Long id, int quantidadeEstoque, int quantidadeMinimaAlerta, Medicamento medicamento){
        this.id = id;
        this.medicamento = medicamento;
        setQuantidadeEstoque(quantidadeEstoque);
        setQuantidadeMinimaAlerta(quantidadeMinimaAlerta);
    }

    public void setQuantidadeEstoque(int quantidadeEstoque){

         if (quantidadeEstoque < 0){
            throw new EstoqueMedicamentoWithInvalidInformationException("O estoque não pode ser menor que 0.");
        } else {
            this.quantidadeEstoque = quantidadeEstoque;
        }

    }
    public void setQuantidadeMinimaAlerta(int quantidadeMinimaAlerta) {
        if (quantidadeMinimaAlerta < 0) {
            throw new EstoqueMedicamentoWithInvalidInformationException("A quantidade mínima para alerta não pode ser negativa.");
        } else {
            this.quantidadeMinimaAlerta = quantidadeMinimaAlerta;
        }
    }

}
