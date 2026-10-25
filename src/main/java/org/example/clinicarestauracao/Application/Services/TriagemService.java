package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.TriagemMapper;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.ConvenioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemNotFoundException;
import org.example.clinicarestauracao.Application.Interfaces.ConvenioRepository;
import org.example.clinicarestauracao.Application.Interfaces.TriagemRepository;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;

import org.example.clinicarestauracao.Domain.Enums.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final ConvenioRepository convenioRepository;

    @Transactional
    public TriagemResponseDto create(TriagemRequestDto dto) {

        Convenio convenio = convenioRepository.findById(dto.convenioId())
                .orElseThrow(() -> new TriagemNotFoundException("Modalidade não encontrada."));
        Triagem triagem = TriagemMapper.requestDtoToEntity(dto, convenio);
        Triagem triagemSalva = triagemRepository.save(triagem);
        return TriagemMapper.entityToResponseDto(triagemSalva);
    }

    public List<TriagemResponseDto> listAll() {
        return triagemRepository.findAll()
                .stream()
                .map(TriagemMapper::entityToResponseDto)
                .toList();
    }

    public TriagemResponseDto findById(Long id) {
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new TriagemNotFoundException("Triagem não encontrada."));

        return TriagemMapper.entityToResponseDto(triagem);
    }

    public TriagemResponseDto findByName(String nomePossivelAcolhido) {
        Triagem triagem = triagemRepository.findByNomePossivelAcolhidoIgnoreCase(nomePossivelAcolhido)
                .orElseThrow(() -> new TriagemNotFoundException("Triagem não encontrada."));

        return TriagemMapper.entityToResponseDto(triagem);
    }

    public List<TriagemResponseDto> findByStatus(Status status) {
        return triagemRepository.findByStatus(status)
                .stream()
                .map(TriagemMapper::entityToResponseDto)
                .toList();
    }

    @Transactional
    public TriagemResponseDto update(Long id, TriagemRequestDto dto) {
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new TriagemNotFoundException("Triagem não encontrada."));

        Convenio convenio = convenioRepository.findById(dto.convenioId())
                .orElseThrow(() -> new ConvenioNotFoundException("Convênio não encontrado."));

        if (triagem.getStatus() != Status.AGENDADA) {
            throw new TriagemNotFoundException(
                    "Apenas triagens agendadas podem ser alteradas."
            );
        }
        triagem.setConvenio(convenio);
        triagem.setNomePossivelAcolhido(dto.nomePossivelAcolhido());
        triagem.setTelefone(dto.telefone());
        triagem.setDataAgendada(dto.dataAgendada());

        Triagem triagemAtualizada = triagemRepository.save(triagem);

        return TriagemMapper.entityToResponseDto(triagemAtualizada);
    }

    @Transactional
    public void delete(Long id) {
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new TriagemNotFoundException("Triagem não encontrada."));
        triagemRepository.delete(triagem);
    }

    @Transactional
    public TriagemResponseDto concluir(Long id) {

        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() ->
                        new TriagemNotFoundException("Triagem não encontrada.")
                );
        triagem.concluir();

        Triagem triagemAtualizada = triagemRepository.save(triagem);

        return TriagemMapper.entityToResponseDto(triagemAtualizada);
    }

    @Transactional
    public void atualizarTriagensVencidas() {

        List<Triagem> triagens = triagemRepository
                .findByStatusAndDataAgendadaBefore(
                        Status.AGENDADA,
                        LocalDateTime.now()
                );

        for (Triagem triagem : triagens) {
            triagem.marcarComoVencida();
        }

        triagemRepository.saveAll(triagens);
    }
}