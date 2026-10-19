package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InternacaoTestBuilder
{
    private Long id = 1L;
    private Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
    private Convenio convenio = new ConvenioTestBuilder().build();
    private LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
    private LocalDate dataSaidaPrevista = dataEntrada.plusMonths(9);
    private LocalDate dataSaida;
    private TreatmentStage etapaTratamento = TreatmentStage.INICIO;
    private BigDecimal valorMensalidade = new BigDecimal("1200.00");
    private Boolean ativo = true;
    private AltaTypes alta = null;

    public static InternacaoTestBuilder newInternacao()
    {
        return new InternacaoTestBuilder();
    }

    public InternacaoTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public InternacaoTestBuilder setAcolhido(Acolhido acolhido)
    {
        this.acolhido = acolhido;
        return this;
    }

    public InternacaoTestBuilder setConvenio(Convenio convenio)
    {
        this.convenio = convenio;
        return this;
    }

    public InternacaoTestBuilder setDataEntrada(LocalDate dataEntrada)
    {
        this.dataEntrada = dataEntrada;
        return this;
    }

    public InternacaoTestBuilder setDataSaidaPrevista(LocalDate dataSaidaPrevista)
    {
        this.dataSaidaPrevista = dataSaidaPrevista;
        return this;
    }

    public InternacaoTestBuilder setDataSaida(LocalDate dataSaida)
    {
        this.dataSaida = dataSaida;
        return this;
    }

    public InternacaoTestBuilder setEtapaTratamento(TreatmentStage etapaTratamento)
    {
        this.etapaTratamento = etapaTratamento;
        return this;
    }

    public InternacaoTestBuilder setValorMensalidade(BigDecimal valorMensalidade)
    {
        this.valorMensalidade = valorMensalidade;
        return this;
    }

    public InternacaoTestBuilder setAtivo(Boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public InternacaoTestBuilder setAlta(AltaTypes alta)
    {
        this.alta = alta;
        return this;
    }

    public Internacao build()
    {
        return new Internacao(id, acolhido, convenio, dataEntrada, dataSaidaPrevista, dataSaida, etapaTratamento, valorMensalidade, ativo, alta);
    }

    public Internacao buildForCreate()
    {
        return new Internacao(acolhido, convenio, dataEntrada, etapaTratamento, valorMensalidade);
    }

    public Internacao buildForCreateWithoutPayment()
    {
        return new Internacao(acolhido, convenio, dataEntrada, etapaTratamento);
    }
}
