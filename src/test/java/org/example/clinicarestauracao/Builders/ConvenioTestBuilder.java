package org.example.clinicarestauracao.Builders;

import lombok.Getter;
import org.example.clinicarestauracao.Domain.Entities.Convenio;

@Getter
public class ConvenioTestBuilder
{
    Long id = 1L;
    String descricao = "Convênio";
    String cnpj = "11222333000181";
    int maxVagas = 20;
    boolean ativo = true;
    boolean pagamento = true;
    String cor = "#2E86C1";

    public ConvenioTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public ConvenioTestBuilder setDescricao(String descricao)
    {
        this.descricao = descricao;
        return this;
    }

    public ConvenioTestBuilder setCNPJ(String cnpj)
    {
        this.cnpj = cnpj;
        return this;
    }

    public ConvenioTestBuilder setMaxVagas(int maxVagas)
    {
        this.maxVagas = maxVagas;
        return this;
    }

    public ConvenioTestBuilder setAtivo(boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public ConvenioTestBuilder setPagamento(boolean pagamento)
    {
        this.pagamento = pagamento;
        return this;
    }

    public ConvenioTestBuilder setCor(String cor)
    {
        this.cor = cor;
        return this;
    }


    public Convenio build()
    {
        Convenio convenio = new Convenio(id, descricao, cnpj, maxVagas, pagamento, ativo, cor);
        return convenio;
    }

    public Convenio buildForCreate()
    {
        Convenio convenio = new Convenio(descricao, cnpj ,maxVagas, pagamento, cor);
        return convenio;
    }
}
