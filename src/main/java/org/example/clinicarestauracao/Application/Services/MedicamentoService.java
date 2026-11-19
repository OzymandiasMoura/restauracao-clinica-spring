
package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.MedicamentoRepository;
import org.example.clinicarestauracao.Application.Interfaces.MovimentacaoEstoqueRepository;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public List<Medicamento> findAll() {
        return medicamentoRepository.findAll();
    }


    public Medicamento findMedicamentoById(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(
                        "Medicamento não encontrado com o ID: " + id
                ));
    }

    public Medicamento findMedicamentoByNome(String nome) {
        return medicamentoRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new MedicamentoNotFoundException(
                        "Medicamento não encontrado com o nome: " + nome
                ));
    }

    @Transactional
    public Medicamento create(Medicamento medicamento) {
        validarNomeDuplicado(medicamento.getNome());
        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public Medicamento update(Long id, Medicamento dados) {

        Medicamento medicamento = findMedicamentoById(id);

        medicamento.setNome(dados.getNome());
        medicamento.setPreco(dados.getPreco());
        medicamento.setQuantidadeMinima(dados.getQuantidadeMinima());

        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public void delete(Long id) {

        Medicamento medicamento = findMedicamentoById(id);

        if (movimentacaoRepository.existsByMedicamentoId(id)) {
            throw new MedicamentoWithInvalidInformationException(
                    "Não é possível excluir um medicamento que possui histórico de movimentações."
            );
        }

        medicamentoRepository.delete(medicamento);
    }

    public List<Medicamento> listarEstoquesEmAlerta() {
        return medicamentoRepository.findEstoquesEmAlerta();
    }

    private void validarNomeDuplicado(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new MedicamentoWithInvalidInformationException(
                    "O nome do medicamento é obrigatório."
            );
        }

        if (medicamentoRepository.existsByNomeIgnoreCase(nome.trim())) {
            throw new MedicamentoWithInvalidInformationException(
                    "Já existe um medicamento cadastrado com esse nome."
            );
        }
    }
}
