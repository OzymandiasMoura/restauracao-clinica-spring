package org.example.clinicarestauracao.Application.Services;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.EstoqueMedicamento.EstoqueMedicamentoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.EstoqueMedicamento.EstoqueMedicamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.EstoqueMedicamentoRepository;
import org.example.clinicarestauracao.Domain.Entities.EstoqueMedicamento;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueMedicamentoService {

    private final EstoqueMedicamentoRepository estoqueMedicamentoRepository;

    private final MedicamentoService medicamentoService;


    public EstoqueMedicamento findByMedicamentoId(Long idMedicamento) {
        return estoqueMedicamentoRepository.findByMedicamentoId(idMedicamento)
                .orElseThrow(() -> new EstoqueMedicamentoNotFoundException("Nenhum estoque encontrado para este medicamento."));
    }
    public List<EstoqueMedicamento> findAll() {
        return estoqueMedicamentoRepository.findAll();
    }

    @Transactional
    public EstoqueMedicamento inicializarEstoque(Long idMedicamento, int quantidadeEstoque, int quantidadeMinimo){
        Medicamento medicamento = medicamentoService.findMedicamentoById(idMedicamento);

        if (estoqueMedicamentoRepository.findByMedicamentoId(idMedicamento).isPresent()){
            throw new EstoqueMedicamentoWithInvalidInformationException("Este medicamento já possui um estoque cadastrado.");
        }
        if (!medicamento.isAtivo()) {
            throw new EstoqueMedicamentoWithInvalidInformationException("Não é possível alterar o estoque de um medicamento inativo.");
        }
        EstoqueMedicamento novoEstoque = new EstoqueMedicamento(null, quantidadeEstoque, quantidadeMinimo, medicamento);

        return estoqueMedicamentoRepository.save(novoEstoque);
    }

    @Transactional
    public EstoqueMedicamento adicionarEstoque(Long idMedicamento, int quantidadeParaAdicionar) {
        EstoqueMedicamento estoque = findByMedicamentoId(idMedicamento);

        if (quantidadeParaAdicionar <= 0) {
            throw new EstoqueMedicamentoWithInvalidInformationException("A quantidade a adicionar deve ser maior que zero.");
        }
        if (!estoque.getMedicamento().isAtivo()) {
            throw new EstoqueMedicamentoWithInvalidInformationException("Não é possível alterar o estoque de um medicamento inativo.");
        }
        estoque.setQuantidadeEstoque(estoque.getQuantidadeEstoque() + quantidadeParaAdicionar);
        return estoque;
    }

    @Transactional
    public EstoqueMedicamento baixarEstoque(Long idMedicamento, int quantidadeParaBaixar) {
        if (quantidadeParaBaixar <= 0) {
            throw new EstoqueMedicamentoWithInvalidInformationException("A quantidade a baixar deve ser maior que zero.");
        }
        EstoqueMedicamento estoque = findByMedicamentoId(idMedicamento);

        if (estoque.getQuantidadeEstoque() < quantidadeParaBaixar) {
            throw new EstoqueMedicamentoWithInvalidInformationException(
                    "Estoque insuficiente! Você tentou baixar " + quantidadeParaBaixar +
                            ", mas só existem " + estoque.getQuantidadeEstoque() + " unidades disponíveis."
            );
        }
        estoque.setQuantidadeEstoque(estoque.getQuantidadeEstoque() - quantidadeParaBaixar);

        return estoqueMedicamentoRepository.save(estoque);
    }

    public List<EstoqueMedicamento> listarEstoquesEmAlerta() {
        return estoqueMedicamentoRepository.findEstoquesEmAlerta();
    }
}
