package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.Status;

import java.time.LocalDateTime;

public class TriagemTestBuilder
{
    private Long id = 1L;
    private Convenio convenio = new ConvenioTestBuilder().build();
    private String nomePossivelAcolhido = "Maria da Silva";
    private String telefone = "11999999999";
    private LocalDateTime dataAgendada = LocalDateTime.now().plusDays(7);
    private String observacaoEntrevista = "Entrevista inicial";
    private Status status = Status.AGENDADA;
    private LocalDateTime dataConclusao = null;

    public static TriagemTestBuilder newTriagem()
    {
        return new TriagemTestBuilder();
    }

    public TriagemTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public TriagemTestBuilder setConvenio(Convenio convenio)
    {
        this.convenio = convenio;
        return this;
    }

    public TriagemTestBuilder setNomePossivelAcolhido(String nomePossivelAcolhido)
    {
        this.nomePossivelAcolhido = nomePossivelAcolhido;
        return this;
    }

    public TriagemTestBuilder setTelefone(String telefone)
    {
        this.telefone = telefone;
        return this;
    }

    public TriagemTestBuilder setDataAgendada(LocalDateTime dataAgendada)
    {
        this.dataAgendada = dataAgendada;
        return this;
    }

    public TriagemTestBuilder setObservacaoEntrevista(String observacaoEntrevista)
    {
        this.observacaoEntrevista = observacaoEntrevista;
        return this;
    }

    public TriagemTestBuilder setStatus(Status status)
    {
        this.status = status;
        return this;
    }

    public TriagemTestBuilder setDataConclusao(LocalDateTime dataConclusao)
    {
        this.dataConclusao = dataConclusao;
        return this;
    }

    public Triagem build()
    {
        return new Triagem(id, convenio, nomePossivelAcolhido, telefone, dataAgendada, observacaoEntrevista, status, dataConclusao);
    }

    public Triagem buildForCreate()
    {
        return new Triagem(convenio, nomePossivelAcolhido, telefone, dataAgendada, observacaoEntrevista);
    }
}
