package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Domain.Enums.TipoEstoque;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EstoqueMedicamento")
public class EstoqueMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantidade_estoque", nullable = false)
    private Long quantidadeEstoque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEstoque tipoEstoque;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;


}
