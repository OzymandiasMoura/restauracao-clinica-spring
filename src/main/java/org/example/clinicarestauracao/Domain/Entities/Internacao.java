package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
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
    @JoinColumn(name = "modalidade_id", nullable = false)
    private Modalidade modalidade;
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

    public Internacao(Long id, Acolhido acolhido, Modalidade modalidade, LocalDate dataEntrada, LocalDate dataSaidaPrevista, LocalDate dataSaida, TreatmentStage etapaTratamento, BigDecimal valorMensalidade, Boolean ativo)
    {
        setId(id);
        setAcolhido(acolhido);
        setModalidade(modalidade);
        setDataEntrada(dataEntrada);
        setDataSaidaPrevista(dataSaidaPrevista);
        setDataSaida(dataSaida);
        setEtapaTratamento(etapaTratamento);
        setValorMensalidade(valorMensalidade);
        setAtivo(ativo);
    }

    public Internacao(Acolhido acolhido, Modalidade modalidade, LocalDate dataEntrada, TreatmentStage etapaTratamento, BigDecimal valorMensalidade)
    {
        setAcolhido(acolhido);
        validarAcolhidoParaNovaInternacao(acolhido);
        setModalidade(modalidade);
        validarModalidadeParaNovaInternacao(modalidade);
        setDataEntrada(dataEntrada);
        setDataSaidaPrevista(dataEntrada.plusMonths(9));
        setEtapaTratamento(etapaTratamento);
        setValorMensalidade(valorMensalidade);
        setAtivo(true);
    }

    public Internacao(Acolhido acolhido, Modalidade modalidade, LocalDate dataEntrada, TreatmentStage etapaTratamento)
    {
        setAcolhido(acolhido);
        validarAcolhidoParaNovaInternacao(acolhido);
        setModalidade(modalidade);
        validarModalidadeParaNovaInternacao(modalidade);
        setDataEntrada(dataEntrada);
        setDataSaidaPrevista(dataEntrada.plusMonths(9));
        setEtapaTratamento(etapaTratamento);
        if (modalidade.isPagamento())
        {
            throw new InternacaoWithInvalidInformationException("Modalidade exige valor de mensalidade.");
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

    private void setModalidade(Modalidade modalidade)
    {
        if (modalidade == null)
        {
            throw new InternacaoWithInvalidInformationException("Modalidade é obrigatória.");
        }

        this.modalidade = modalidade;
    }

    private void validarModalidadeParaNovaInternacao(Modalidade modalidade)
    {
        if (!modalidade.isAtivo())
        {
            throw new InternacaoWithInvalidInformationException("Modalidade não pode estar desativada.");
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

    public void setDataSaida(LocalDate dataSaida)
    {
        if (dataSaida != null && dataSaida.isBefore(dataEntrada))
        {
            throw new InternacaoWithInvalidInformationException("Data de saída não pode ser anterior a data de entrada.");
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

        if (!modalidade.isPagamento() && valorArredondado.compareTo(BigDecimal.ZERO) != 0)
        {
            throw new InternacaoWithInvalidInformationException("Modalidade não aceita valor da mensalidade da internação diferente de 0");
        }

        if (modalidade.isPagamento() && valorArredondado.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new InternacaoWithInvalidInformationException("Valor da mensalidade deve ser maior que zero.");
        }

        this.valorMensalidade = valorArredondado;
    }

}
