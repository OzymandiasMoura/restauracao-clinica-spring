package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.CargoRepository;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.springframework.stereotype.Service;

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
}
