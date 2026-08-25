package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.ModalidadeWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Validation.CnpjValidator;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Modalidade
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String descricao;
    @Column(unique = true, length = 14)
    private String cnpj;
    @Column(nullable = false)
    private int maxVagas;
    @Column(nullable = false)
    private boolean ativo;
    @Column(nullable = false)
    private boolean pagamento;
    @Column(nullable = false, length = 7, unique = true)
    private String cor;

    public Modalidade(String descricao, String cnpj, int maxVagas, boolean pagamento, String cor)
    {
        setDescricao(descricao);
        setCnpj(cnpj);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(true);
        setCor(cor);
    }
    public Modalidade(long id, String descricao, String cnpj, int maxVagas, boolean pagamento, boolean ativo, String cor)
    {
        setId(id);
        setDescricao(descricao);
        setCnpj(cnpj);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(ativo);
        setCor(cor);
    }
    public Modalidade(long id, String descricao,  int maxVagas, boolean pagamento, boolean ativo, String cor)
    {
        setId(id);
        setDescricao(descricao);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(ativo);
        setCor(cor);
    }
    public Modalidade(String descricao,  int maxVagas, boolean pagamento, boolean ativo, String cor)
    {
        setDescricao(descricao);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(ativo);
        setCor(cor);
    }

    public void setDescricao(String descricao)
    {
        if(descricao == null || descricao.isBlank())
        {
            throw new ModalidadeWithInvalidInformationException("Descrição não pode ser nulo ou vazia");
        }
        else if(descricao.length()<3)
        {
            throw new ModalidadeWithInvalidInformationException("Descrição não pode ter menos de 3 caracteres");
        }
        else
        {
            this.descricao = descricao;
        }
    }

    public void setCnpj(String cnpj)
    {
        if (cnpj == null || cnpj.isBlank())
        {
            this.cnpj = null;
            return;
        }
        if(!CnpjValidator.validate(cnpj))
        {
            throw new ModalidadeWithInvalidInformationException("CNPJ é invalido.");
        }
        else
        {
            this.cnpj = CnpjValidator.normalize(cnpj);
        }
    }

    public void setMaxVagas(int  maxVagas)
    {
        if (maxVagas < 0)
        {
            throw new ModalidadeWithInvalidInformationException("Numero de vagas deve ser 0 ou mais.");
        }
        else
        {
            this.maxVagas = maxVagas;
        }
    }

    public void setCor(String cor)
    {
        if (cor == null || cor.isBlank())
        {
            throw new ModalidadeWithInvalidInformationException("Cor não pode ser nula ou vazia.");
        }
        if(!cor.matches("^#[0-9A-Fa-f]{6}$"))
        {
            throw new ModalidadeWithInvalidInformationException("Cor deve estar no formato hexadecimal #RRGGBB.");
        }

        this.cor = cor.toUpperCase();
    }
}
