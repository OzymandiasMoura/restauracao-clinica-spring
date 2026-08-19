package org.example.clinicarestauracao.Application.Services;

import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.ModalidadeNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.ModalidadeWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.ModalidadeRepository;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ModalidadeService
{
    private ModalidadeRepository repository;

    public Modalidade createModalidade(Modalidade modalidade)
    {
        if(modalidade.getCnpj() != null && repository.findModalidadeByCnpj(modalidade.getCnpj()).isPresent())
        {
            throw new ModalidadeWithInvalidInformationException("CNPJ ja cadastrado");
        }
        if(repository.findModalidadeByDescricao(modalidade.getDescricao()).isPresent())
        {
            throw new ModalidadeWithInvalidInformationException("Descrição ja cadastrada");
        }

        return repository.save(modalidade);
    }

    public Modalidade findModalidadeById(Long id)
    {
        return repository.findById(id).orElseThrow(() -> new ModalidadeNotFoundException("Modalidade não encontrada."));
    }

    public List<Modalidade> findAllModalidades()
    {
        return repository.findAll();
    }

    public Modalidade updateModalidade(Long id, Modalidade modalidade)
    {
        Modalidade m = findModalidadeById(id);

        if(!modalidade.getDescricao().equals(m.getDescricao()))
        {
            if(repository.findModalidadeByDescricao(modalidade.getDescricao()).isPresent())
            {
                throw new ModalidadeWithInvalidInformationException("Descrição ja cadastrada");
            }
            m.setDescricao(modalidade.getDescricao());
        }

        boolean cnpjFoiAlterado = modalidade.getCnpj() == null && m.getCnpj() != null ||
                        modalidade.getCnpj() != null && !modalidade.getCnpj().equals(m.getCnpj());

        if(cnpjFoiAlterado)
        {
            Modalidade m2 =  findModalidadeByCnpj(modalidade.getCnpj());
            if (modalidade.getCnpj()!= null && repository.findModalidadeByCnpj(m2.getCnpj()).isPresent())
            {
                throw new ModalidadeWithInvalidInformationException("CNPJ ja cadastrado");
            }
            m.setCnpj(modalidade.getCnpj());
        }
        m.setMaxVagas(modalidade.getMaxVagas());
        m.setPagamento(modalidade.isPagamento());

        return repository.save(m);
    }

    public void deleteModalidadeById(Long id)
    {
        Modalidade m = findModalidadeById(id);

        if(!m.isAtivo())
        {
            return;
        }
        m.setAtivo(false);
        repository.save(m);
    }

    public void reabrirModalidade(Long id)
    {
        Modalidade m = findModalidadeById(id);

        if(m.isAtivo())
        {
            return;
        }
        m.setAtivo(true);
        repository.save(m);
    }

    public Modalidade findModalidadeByCnpj(String cnpj)
    {
        return repository.findModalidadeByCnpj(cnpj).orElseThrow(() -> new ModalidadeNotFoundException("Modalidade não encontrada."));
    }

    public Modalidade findModalidadeByDescricao(String descricao)
    {
        return repository.findModalidadeByDescricao(descricao).orElseThrow(() -> new ModalidadeNotFoundException("Modalidade não encontrada."));
    }
}
