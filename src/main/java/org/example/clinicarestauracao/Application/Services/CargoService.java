package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.CargoRepository;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CargoService
{
    CargoRepository repository;

    public CargoService(CargoRepository cargoRepository)
    {
        this.repository = cargoRepository;
    }

    public Cargo createCargo(Cargo cargo)
    {
        if(repository.findCargoByNome(cargo.getNome()).isPresent())
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

        return repository.findCargoByNome(nomeNormalizado).orElseThrow(() -> new CargoNotFoundException("Cargo não encontrado."));
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

        Optional<Cargo> sameName = repository.findCargoByNome(cargo.getNome());

        if(sameName.isPresent() && !(sameName.get().getId().equals(newCargo.getId())))
        {
            throw new CargoWithInvalidInformationException("Nome do cargo já existente.");
        }

        newCargo.setNome(cargo.getNome());

        return repository.save(newCargo);
    }

    public void softDeleteCargoById(Long id)
    {
        Cargo newCargo = this.findCargoById(id);

        if(!newCargo.isAtivo())
        {
            return;
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
}
