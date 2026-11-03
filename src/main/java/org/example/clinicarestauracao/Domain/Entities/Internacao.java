package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Data
public class Internacao
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "acolhido_id", nullable = false)
    private Acolhido acolhido;
    @ManyToOne(optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;
    @Column(nullable = false)
    private LocalDate dataEntrada;
    @Column(nullable = false)
    private LocalDate dataSaidaPrevista;
    @Column(nullable = true)
    private LocalDate dataSaida;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatmentStage etapaTratamento;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensalidade;
    @Column(nullable = false)
    private Boolean ativo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private AltaTypes alta;

    public Internacao(Long id, Acolhido acolhido, Convenio convenio, LocalDate dataEntrada, LocalDate dataSaidaPrevista, LocalDate dataSaida, TreatmentStage etapaTratamento, BigDecimal valorMensalidade, Boolean ativo, AltaTypes alta)
    {
        setId(id);
        setAcolhido(acolhido);
        setConvenio(convenio);
        setDataEntrada(dataEntrada);
        setDataSaidaPrevista(dataSaidaPrevista);
        setDataSaida(dataSaida);
        setEtapaTratamento(etapaTratamento);
        setValorMensalidade(valorMensalidade);
        setAtivo(ativo);
        setAlta(alta);
    }

    public Internacao(Acolhido acolhido, Convenio convenio, LocalDate dataEntrada, TreatmentStage etapaTratamento, BigDecimal valorMensalidade)
    {
        setAcolhido(acolhido);
        validarAcolhidoParaNovaInternacao(acolhido);
        setConvenio(convenio);
        validarConvenioParaNovaInternacao(convenio);
        setDataEntrada(dataEntrada);
        setDataSaidaPrevista(dataEntrada.plusMonths(9));
        setEtapaTratamento(etapaTratamento);
        setValorMensalidade(valorMensalidade);
        setAtivo(true);
    }

    public Internacao(Acolhido acolhido, Convenio convenio, LocalDate dataEntrada, TreatmentStage etapaTratamento)
    {
        setAcolhido(acolhido);
        validarAcolhidoParaNovaInternacao(acolhido);
        setConvenio(convenio);
        validarConvenioParaNovaInternacao(convenio);
        setDataEntrada(dataEntrada);
        setDataSaidaPrevista(dataEntrada.plusMonths(9));
        setEtapaTratamento(etapaTratamento);
        if (convenio.isPagamento())
        {
            throw new InternacaoWithInvalidInformationException("Convênio exige valor de mensalidade.");
        }
        setValorMensalidade(new BigDecimal("0.00"));
        setAtivo(true);
    }

    private void setId(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new InternacaoWithInvalidInformationException("Id da internação é inválido.");
        }
        this.id = id;
    }

    private void setAcolhido(Acolhido acolhido)
    {
        if (acolhido == null)
        {
            throw new InternacaoWithInvalidInformationException("Acolhido é obrigatório.");
        }
        this.acolhido = acolhido;
    }

    private void validarAcolhidoParaNovaInternacao(Acolhido acolhido)
    {
        if (!Boolean.TRUE.equals(acolhido.getAtivo()))
        {
            throw new InternacaoWithInvalidInformationException("Acolhido não pode estar desativado.");
        }
    }

    private void setConvenio(Convenio convenio)
    {
        if (convenio == null)
        {
            throw new InternacaoWithInvalidInformationException("Convênio é obrigatório.");
        }

        this.convenio = convenio;
    }

    private void validarConvenioParaNovaInternacao(Convenio convenio)
    {
        if (!convenio.isAtivo())
        {
            throw new InternacaoWithInvalidInformationException("Convênio não pode estar desativado.");
        }
    }

    private void setDataEntrada(LocalDate dataEntrada)
    {
        if (dataEntrada == null)
        {
            throw new InternacaoWithInvalidInformationException("Data de entrada é obrigatória.");
        }
        if (dataEntrada.isAfter(LocalDate.now()))
        {
            throw new InternacaoWithInvalidInformationException("Data de entrada não pode ser futura.");
        }
        if (dataEntrada.isBefore(acolhido.getDataNascimento()))
        {
            throw new InternacaoWithInvalidInformationException("Data de entrada não pode ser anterior ao nascimento do acolhido.");
        }

        this.dataEntrada = dataEntrada;
    }

    public void setDataSaidaPrevista(LocalDate dataSaidaPrevista)
    {
        if (dataSaidaPrevista == null)
        {
            throw new InternacaoWithInvalidInformationException("Data de saída prevista é obrigatória.");
        }
        if (dataSaidaPrevista.isBefore(dataEntrada))
        {
            throw new InternacaoWithInvalidInformationException("Data de saída prevista não pode ser anterior a data de entrada.");
        }

        this.dataSaidaPrevista = dataSaidaPrevista;
    }

    private void setDataSaida(LocalDate dataSaida)
    {
        if (dataSaida != null && dataSaida.isBefore(dataEntrada))
        {
            throw new InternacaoWithInvalidInformationException("Data de saída não pode ser anterior a data de entrada.");
        }
        if (dataSaida != null && dataSaida.isAfter(LocalDate.now()))
        {
            throw new InternacaoWithInvalidInformationException("Não é possível adicionar uma data de saída futura.");
        }

        this.dataSaida = dataSaida;
    }

    public void setEtapaTratamento(TreatmentStage treatmentStage)
    {
        if (treatmentStage == null)
        {
            throw new InternacaoWithInvalidInformationException("Etapa do tratamento não pode ser nula.");
        }

        this.etapaTratamento = treatmentStage;
    }

    public void setValorMensalidade(BigDecimal valorMensalidade)
    {
        if (valorMensalidade == null)
        {
            throw new InternacaoWithInvalidInformationException("Valor da mensalidade não pode ser nula.");
        }

        BigDecimal valorArredondado = valorMensalidade.setScale(2, RoundingMode.HALF_UP);

        if (!convenio.isPagamento() && valorArredondado.compareTo(BigDecimal.ZERO) != 0)
        {
            throw new InternacaoWithInvalidInformationException("Convênio não aceita valor da mensalidade da internação diferente de 0");
        }

        if (convenio.isPagamento() && valorArredondado.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new InternacaoWithInvalidInformationException("Valor da mensalidade deve ser maior que zero.");
        }

        this.valorMensalidade = valorArredondado;
    }

    private void setAlta(AltaTypes altaTypes)
    {
        this.alta =  altaTypes;
    }

    private void setAtivo(Boolean ativo)
    {
        if (ativo == null)
        {
            throw new InternacaoWithInvalidInformationException("Ativo não pode ser nulo.");
        }
        this.ativo = ativo;
    }

    public void endInternacao(AltaTypes alta, LocalDate dataSaida)
    {
        if(this.dataSaida != null && this.alta != null && this.ativo == false)
        {
            throw new InternacaoWithInvalidInformationException("Internação já foi encerrada.");
        }
        if (dataSaida == null)
        {
            throw new InternacaoWithInvalidInformationException("Data de saída é obrigatória para gerar a alta.");
        }
        if (alta == null)
        {
            throw new InternacaoWithInvalidInformationException("Tipo é obrigatório para gerar a alta.");
        }

        setEtapaTratamento(TreatmentStage.ALTA_CONCEDIDA);
        setDataSaida(dataSaida);
        setAlta(alta);
        setAtivo(false);
    }

    public void updateTreatmentStage(LocalDate dataReferencia)
    {
        if (dataReferencia == null)
        {
            throw new InternacaoWithInvalidInformationException("Data de referência é obrigatória.");
        }

        if (!Boolean.TRUE.equals(ativo))
        {
            return;
        }

        if (dataReferencia.isAfter(dataSaidaPrevista))
        {
            etapaTratamento = TreatmentStage.ALTA_VENCIDA;
            return;
        }

        LocalDate inicioProximaAlta = dataSaidaPrevista.minusDays(30);

        if (!dataReferencia.isBefore(inicioProximaAlta))
        {
            etapaTratamento = TreatmentStage.PROXIMO_ALTA;
            return;
        }

        etapaTratamento = TreatmentStage.EM_TRATAMENTO;
    }
}
