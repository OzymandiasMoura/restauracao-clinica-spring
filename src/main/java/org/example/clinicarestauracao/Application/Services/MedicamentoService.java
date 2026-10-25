
package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.MedicamentoRepository;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public List<Medicamento> findAtivos() {
        return medicamentoRepository.findByAtivoTrue();
    }

    public List<Medicamento> findInativos() {
        return medicamentoRepository.findByAtivoFalse();
    }

    public Medicamento findMedicamentoById(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() ->
                        new MedicamentoNotFoundException(
                                "Medicamento não encontrado com o ID: " + id
                        )
                );
    }

    public Medicamento findMedicamentoByNome(String nome) {
        return medicamentoRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() ->
                        new MedicamentoNotFoundException(
                                "Medicamento não encontrado com o nome: " + nome
                        )
                );
    }

    @Transactional
    public Medicamento create(Medicamento medicamento) {
        validarNomeDuplicado(medicamento.getNome());
        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public Medicamento update(Long id, Medicamento dadosAtualizados) {

        Medicamento medicamento = findMedicamentoById(id);

        if (!medicamento.getNome().equalsIgnoreCase(
                dadosAtualizados.getNome())) {

            validarNomeDuplicado(dadosAtualizados.getNome());
        }

        medicamento.setNome(dadosAtualizados.getNome());
        medicamento.setPreco(dadosAtualizados.getPreco());

        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public void delete(Long id) {

        Medicamento medicamento = findMedicamentoById(id);

        medicamento.inativar();
    }


    @Transactional
    public Medicamento adicionarEstoque(
            Long idMedicamento,
            int quantidadeParaAdicionar
    ) {

        Medicamento medicamento = findMedicamentoById(idMedicamento);

        medicamento.adicionarEstoque(quantidadeParaAdicionar);

        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public Medicamento baixarEstoque(
            Long idMedicamento,
            int quantidadeParaBaixar
    ) {

        Medicamento medicamento = findMedicamentoById(idMedicamento);

        medicamento.baixarEstoque(quantidadeParaBaixar);

        return medicamentoRepository.save(medicamento);
    }


    public List<Medicamento> listarEstoquesEmAlerta() {

        return medicamentoRepository.findEstoquesEmAlerta();
    }

    private void validarNomeDuplicado(String nome) {

        if (medicamentoRepository.existsByNomeIgnoreCase(nome)) {

            throw new MedicamentoWithInvalidInformationException(
                    "Já existe um medicamento cadastrado com esse nome."
            );
        }
    }
}
