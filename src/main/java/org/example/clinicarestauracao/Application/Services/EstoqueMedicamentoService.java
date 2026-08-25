package org.example.clinicarestauracao.Application.Services;


import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento.EstoqueMedicamentoRequestDTO;
import org.example.clinicarestauracao.Application.Interfaces.EstoqueMedicamentoRepository;
import org.example.clinicarestauracao.Domain.Entities.EstoqueMedicamento;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstoqueMedicamentoService {

    private final EstoqueMedicamentoRepository estoqueMedicamentoRepository;


    private final MedicamentoService medicamentoService;




}
