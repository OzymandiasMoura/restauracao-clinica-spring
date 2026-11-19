package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.CargoRepository;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CargoService
{
    private final CargoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final DatabaseAuditContext audit;


    public Cargo createCargo(Cargo cargo)
    {
        if(repository.findCargoByNomeIgnoreCase(cargo.getNome()).isPresent())
        {
            throw new CargoWithInvalidInformationException("Nome do cargo já existente.");
        }
        return repository.save(new Cargo(cargo.getNome()));
    }

    public Cargo findCargoByNome(String nome)
    {
        if(nome == null || nome.isBlank())
        {
            throw new CargoNotFoundException("Nome do cargo não pode ser nulo ou vazio.");
        }

        String nomeNormalizado = nome.strip();

        return repository.findCargoByNomeIgnoreCase(nomeNormalizado).orElseThrow(() -> new CargoNotFoundException("Cargo não encontrado."));
    }

    public Cargo findCargoById(Long id)
    {
        if(id == null || id <= 0)
        {
            throw new CargoNotFoundException("Cargo não encontrado.");
        }

        return repository.findCargoById(id).orElseThrow(() -> new CargoNotFoundException("Cargo não encontrado."));
    }

    public List<Cargo> findAllCargo()
    {
        return repository.findAll();
    }

    public Cargo updateCargo(Cargo cargo, Long id)
    {
        Cargo newCargo = this.findCargoById(id);

        Optional<Cargo> sameName = repository.findCargoByNomeIgnoreCase(cargo.getNome());

        if(sameName.isPresent() && !(sameName.get().getId().equals(newCargo.getId())))
        {
            throw new CargoWithInvalidInformationException("Nome do cargo já existente.");
        }

        newCargo.setNome(cargo.getNome());

        return repository.save(newCargo);
    }

    @Transactional
    public void softDeleteCargoById(Long id)
    {
        Cargo newCargo = this.findCargoById(id);

        if(!newCargo.isAtivo())
        {
            return;
        }

        if (funcionarioRepository.existsByCargoAndAtivoTrue(newCargo))
        {
            throw new CargoWithInvalidInformationException("Cargo possui funcionários ativos.");
        }


        newCargo.deactivate();

        repository.save(newCargo);
    }

    public void reactivateCargoById(Long id)
    {
        Cargo cargo = this.findCargoById(id);

        if(cargo.isAtivo())
        {
            return;
        }

        cargo.activate();
        repository.save(cargo);
    }

    @Transactional
    public void deleteCargo(Long id)
    {
        Cargo cargo = this.findCargoById(id);

        if (funcionarioRepository.existsByCargo(cargo))
        {
            throw new CargoWithInvalidInformationException("Cargo possui funcionário cadastrado.");
        }

        audit.setCurrentActor();

        try
        {
            repository.delete(cargo);
            repository.flush();
        }
        finally
        {
            audit.clearActor();
        }
    }
}
