package org.example.clinicarestauracao.Application.Services;

import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.ConvenioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.ConvenioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.ConvenioRepository;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConvenioService
{
    private ConvenioRepository repository;

    public Convenio createConvenio(Convenio convenio)
    {
        if(convenio.getCnpj() != null && repository.findConvenioByCnpj(convenio.getCnpj()).isPresent())
        {
            throw new ConvenioWithInvalidInformationException("CNPJ já cadastrado.");
        }
        if(repository.findConvenioByDescricaoIgnoreCase(convenio.getDescricao()).isPresent())
        {
            throw new ConvenioWithInvalidInformationException("Descrição já cadastrada.");
        }
        if(repository.findConvenioByCor(convenio.getCor()).isPresent())
        {
            throw new ConvenioWithInvalidInformationException("Cor já cadastrada.");
        }

        return repository.save(convenio);
    }

    public Convenio findConvenioById(Long id)
    {
        return repository.findById(id).orElseThrow(() -> new ConvenioNotFoundException("Convênio não encontrado."));
    }

    public List<Convenio> findAllConvenios()
    {
        return repository.findAll();
    }

    public Convenio updateConvenio(Long id, Convenio convenio)
    {
        Convenio convenioExistente = findConvenioById(id);

        boolean descricaoFoiAlterada = !convenio.getDescricao().equals(convenioExistente.getDescricao());
        boolean cnpjFoiAlterado = convenio.getCnpj() == null && convenioExistente.getCnpj() != null || convenio.getCnpj() != null && !convenio.getCnpj().equals(convenioExistente.getCnpj());
        boolean corAlterada =  !convenio.getCor().equals(convenioExistente.getCor());

        Optional<Convenio> mesmaDescricao = descricaoFoiAlterada
                ? repository.findConvenioByDescricaoIgnoreCase(convenio.getDescricao())
                : Optional.empty();

        if(mesmaDescricao.isPresent() && !mesmaDescricao.get().getId().equals(convenioExistente.getId()))
        {
            throw new ConvenioWithInvalidInformationException("Descrição já cadastrada.");
        }

        if(cnpjFoiAlterado && convenio.getCnpj()!= null && repository.findConvenioByCnpj(convenio.getCnpj()).isPresent())
        {
            throw new ConvenioWithInvalidInformationException("CNPJ já cadastrado.");
        }
        if(corAlterada && repository.findConvenioByCor(convenio.getCor()).isPresent())
        {
            throw new ConvenioWithInvalidInformationException("Cor já cadastrada.");
        }

        if(descricaoFoiAlterada)
        {
            convenioExistente.setDescricao(convenio.getDescricao());
        }
        if(cnpjFoiAlterado)
        {
            convenioExistente.setCnpj(convenio.getCnpj());
        }
        convenioExistente.setMaxVagas(convenio.getMaxVagas());
        convenioExistente.setPagamento(convenio.isPagamento());
        if(corAlterada)
        {
            convenioExistente.setCor(convenio.getCor());
        }


        return repository.save(convenioExistente);
    }

    public void deleteConvenioById(Long id)
    {
        Convenio convenio = findConvenioById(id);

        if(!convenio.isAtivo())
        {
            return;
        }
        convenio.setAtivo(false);
        repository.save(convenio);
    }

    public void reabrirConvenioById(Long id)
    {
        Convenio convenio = findConvenioById(id);

        if(convenio.isAtivo())
        {
            return;
        }
        convenio.setAtivo(true);
        repository.save(convenio);
    }

    public Convenio findConvenioByCnpj(String cnpj)
    {
        return repository.findConvenioByCnpj(cnpj).orElseThrow(() -> new ConvenioNotFoundException("Convênio não encontrado."));
    }

    public Convenio findConvenioByDescricao(String descricao)
    {
        return repository.findConvenioByDescricaoIgnoreCase(descricao).orElseThrow(() -> new ConvenioNotFoundException("Convênio não encontrado."));
    }

    public Convenio findConvenioByCor(String cor)
    {
        return repository.findConvenioByCor(cor).orElseThrow(() -> new ConvenioNotFoundException("Convênio não encontrado."));
    }
}
