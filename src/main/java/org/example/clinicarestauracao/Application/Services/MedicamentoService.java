package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Interfaces.MedicamentoRepository;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public List<Medicamento> findAll(){
        return medicamentoRepository.findAll();
    }

    public Medicamento findById(Long id){
        return medicamentoRepository.findById(id).orElseThrow(() ->new RuntimeException("Medicamento não encontrado com o ID: " + id));
    }

    @Transactional
    public Medicamento create(Medicamento medicamento){
        validarNomeDuplicado(medicamento.getNome());
        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public Medicamento update(Long id, Medicamento dadosAtualizados){
        Medicamento exists = findById(id);

        if (!exists.getNome().equalsIgnoreCase(dadosAtualizados.getNome())){
            validarNomeDuplicado(dadosAtualizados.getNome());
        }
        exists.setNome(dadosAtualizados.getNome());
        exists.setPreco(dadosAtualizados.getPreco());

        return medicamentoRepository.save(exists);
    }


    @Transactional
    public void delete(Long id){
        Medicamento exists = findById(id);
        medicamentoRepository.delete(exists);
    }

    private void validarNomeDuplicado(String nome){
        if (medicamentoRepository.existsByNomeIgnoreCase(nome)){
            throw new IllegalArgumentException("Já existe um medicamento cadastrado com esse nome!");
        }
    }
}
