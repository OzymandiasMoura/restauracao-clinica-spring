package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.ConvenioWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Validation.CnpjValidator;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "Convenios")
public class Convenio
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String descricao;
    @Column(unique = true, length = 14)
    private String cnpj;
    @Column(nullable = false)
    private Integer maxVagas;
    @Column(nullable = false)
    private boolean ativo;
    @Column(nullable = false)
    private boolean pagamento;
    @Column(nullable = false, length = 7, unique = true)
    private String cor;

    public Convenio(String descricao, String cnpj, Integer maxVagas, boolean pagamento, String cor)
    {
        setDescricao(descricao);
        setCnpj(cnpj);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(true);
        setCor(cor);
    }
    public Convenio(long id, String descricao, String cnpj, Integer maxVagas, boolean pagamento, boolean ativo, String cor)
    {
        setId(id);
        setDescricao(descricao);
        setCnpj(cnpj);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(ativo);
        setCor(cor);
    }
    public Convenio(long id, String descricao,  int maxVagas, boolean pagamento, boolean ativo, String cor)
    {
        setId(id);
        setDescricao(descricao);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(ativo);
        setCor(cor);
    }
    public Convenio(String descricao,  int maxVagas, boolean pagamento, boolean ativo, String cor)
    {
        setDescricao(descricao);
        setMaxVagas(maxVagas);
        setPagamento(pagamento);
        setAtivo(ativo);
        setCor(cor);
    }

    public void setDescricao(String descricao)
    {
        if (descricao == null)
        {
            throw new ConvenioWithInvalidInformationException("Descrição não pode ser nulo ou vazia");
        }
        String descricaoNormalizada = descricao.strip();
        if(descricaoNormalizada.isBlank())
        {
            throw new ConvenioWithInvalidInformationException("Descrição não pode ser nulo ou vazia");
        }
        else if(descricaoNormalizada.length()<3)
        {
            throw new ConvenioWithInvalidInformationException("Descrição não pode ter menos de 3 caracteres");
        }
        else if(descricaoNormalizada.length()>200)
        {
            throw new ConvenioWithInvalidInformationException("Descrição não pode ter mais que 200 caracteres");
        }
        else
        {
            this.descricao = descricaoNormalizada;
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
            throw new ConvenioWithInvalidInformationException("CNPJ é invalido.");
        }
        else
        {
            this.cnpj = CnpjValidator.normalize(cnpj);
        }
    }

    public void setMaxVagas(Integer maxVagas)
    {
        if (maxVagas == null)
        {
            throw new ConvenioWithInvalidInformationException("Numero de vagas não pode ser nulo.");
        }
        if (maxVagas < 0)
        {
            throw new ConvenioWithInvalidInformationException("Numero de vagas deve ser 0 ou mais.");
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
            throw new ConvenioWithInvalidInformationException("Cor não pode ser nula ou vazia.");
        }
        if(!cor.matches("^#[0-9A-Fa-f]{6}$"))
        {
            throw new ConvenioWithInvalidInformationException("Cor deve estar no formato hexadecimal #RRGGBB.");
        }

        this.cor = cor.toUpperCase();
    }
}
