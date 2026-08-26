package org.example.clinicarestauracao.Builders;

import lombok.Getter;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;

@Getter
public class ModalidadeTestBuilder
{
    Long id = 1L;
    String descricao = "Modalidade";
    String cnpj = "11222333000181";
    int maxVagas = 20;
    boolean ativo = true;
    boolean pagamento = true;
    String cor = "#2E86C1";

    public ModalidadeTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public ModalidadeTestBuilder setDescricao(String descricao)
    {
        this.descricao = descricao;
        return this;
    }

    public ModalidadeTestBuilder setCNPJ(String cnpj)
    {
        this.cnpj = cnpj;
        return this;
    }

    public ModalidadeTestBuilder setMaxVagas(int maxVagas)
    {
        this.maxVagas = maxVagas;
        return this;
    }

    public ModalidadeTestBuilder setAtivo(boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public ModalidadeTestBuilder setPagamento(boolean pagamento)
    {
        this.pagamento = pagamento;
        return this;
    }

    public ModalidadeTestBuilder setCor(String cor)
    {
        this.cor = cor;
        return this;
    }


    public Modalidade build()
    {
        Modalidade mod = new Modalidade(id, descricao, cnpj, maxVagas, pagamento, ativo, cor);
        return mod;
    }

    public Modalidade buildForCreate()
    {
        Modalidade mod = new Modalidade(descricao, cnpj ,maxVagas, pagamento, cor);
        return mod;
    }
}
