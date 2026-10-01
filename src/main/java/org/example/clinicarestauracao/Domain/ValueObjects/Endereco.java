package org.example.clinicarestauracao.Domain.ValueObjects;

import lombok.Data;
import org.example.clinicarestauracao.Application.Exceptions.Endereco.EnderecoWithInvalidInformationException;

@Data
public class Endereco
{
    String logradouro;
    Integer numero;
    String bairro;
    String cidade;
    String estado;
    String complemento;

    public Endereco(String logradouro, Integer numero, String bairro, String cidade, String estado)
    {
        setLogradouro(logradouro);
        setNumero(numero);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
        setComplemento("");
    }

    public Endereco(String logradouro, Integer numero, String bairro, String cidade, String estado,  String complemento)
    {
        setLogradouro(logradouro);
        setNumero(numero);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
        setComplemento(complemento);
    }

    public void setLogradouro(String logradouro)
    {
        if (logradouro == null)
        {
            throw new EnderecoWithInvalidInformationException("Logradouro não pode ser nulo ou vazio.");
        }
        String formatado = logradouro.strip();

        if (formatado.isBlank())
        {
            throw new EnderecoWithInvalidInformationException("Logradouro não pode ser nulo ou vazio.");
        }
        if (formatado.length() < 3)
        {
            throw new EnderecoWithInvalidInformationException("Logradouro deve ter no mínimo 3 caracteres.");
        }
        else if (formatado.length() > 250)
        {
            throw new EnderecoWithInvalidInformationException("Logradouro pode ter no máximo 250 caracteres.");
        }

        this.logradouro = formatado;
    }

    public void setNumero(Integer numero)
    {
        if (numero == null)
        {
            throw new EnderecoWithInvalidInformationException("Numero não pode ser nulo.");
        }
        if (numero <= 0)
        {
            throw new EnderecoWithInvalidInformationException("Numero não pode ser menor ou igual a zero.");
        }

        this.numero = numero;
    }

    public void setBairro(String bairro)
    {
        if (bairro == null)
        {
            throw new EnderecoWithInvalidInformationException("Bairro não pode ser nulo ou vazio.");
        }

        String formatado = bairro.strip();

        if (formatado.isBlank())
        {
            throw new EnderecoWithInvalidInformationException("Bairro não pode ser nulo ou vazio.");
        }
        if (formatado.length() < 3)
        {
            throw new EnderecoWithInvalidInformationException("Bairro deve ter no mínimo 3 caracteres.");
        }
        else if (formatado.length() > 250)
        {
            throw new EnderecoWithInvalidInformationException("Bairro pode ter no máximo 250 caracteres.");
        }

        this.bairro = formatado;
    }

    public void setCidade(String cidade)
    {
        if (cidade == null)
        {
            throw new EnderecoWithInvalidInformationException("Cidade não pode ser nulo ou vazio.");
        }

        String formatado = cidade.strip();

        if (formatado.isBlank())
        {
            throw new EnderecoWithInvalidInformationException("Cidade não pode ser nulo ou vazio.");
        }
        if (formatado.length() < 3)
        {
            throw new EnderecoWithInvalidInformationException("Cidade deve ter no mínimo 3 caracteres.");
        }
        else if (formatado.length() > 250)
        {
            throw new EnderecoWithInvalidInformationException("Cidade pode ter no máximo 250 caracteres.");
        }

        this.cidade = formatado;
    }

    public void setEstado(String estado)
    {
        if (estado == null)
        {
            throw new EnderecoWithInvalidInformationException("Estado não pode ser nulo ou vazio.");
        }

        String formatado = estado.strip();

        if (formatado.isBlank())
        {
            throw new EnderecoWithInvalidInformationException("Estado não pode ser nulo ou vazio.");
        }
        if (formatado.length() != 2)
        {
            throw new EnderecoWithInvalidInformationException("Estado deve ter 2 caracteres.");
        }

        this.estado = formatado.toUpperCase();
    }

    public void setComplemento(String complemento)
    {
        if (complemento == null || complemento.isBlank())
        {
            this.complemento = null;
            return;
        }

        String formatado = complemento.strip();

        if (formatado.length() < 3)
        {
            throw new EnderecoWithInvalidInformationException("Complemento deve ter no mínimo 3 caracteres.");
        }
        else if (formatado.length() > 250)
        {
            throw new EnderecoWithInvalidInformationException("Complemento pode ter no máximo 250 caracteres.");
        }

        this.complemento = formatado;
    }

    public String toDatabaseString()
    {
        String enderecoFormatado = "%s, %d - %s - %s/%s".formatted(logradouro, numero, bairro, cidade, estado);

        if (complemento != null)
        {
            enderecoFormatado += " - " + complemento;
        }

        return enderecoFormatado;
    }
}
