package org.example.clinicarestauracao.Domain.ValueObjects;

import lombok.Data;
import org.example.clinicarestauracao.Application.Exceptions.Endereco.EnderecoWithInvalidInformationException;

@Data
public class Endereco
{
    private static final String INVALID_DATABASE_FORMAT_MESSAGE = "Endereço em formato inválido.";

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
        String formatado = logradouro.replace("-", "").replace("/", "").strip();

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

        String formatado = bairro.replace("-", "").replace("/", "").strip();

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

        String formatado = cidade.replace("-", "").replace("/", "").strip();

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

        String formatado = estado.replace("-", "").replace("/", "").strip();

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

        String formatado = complemento.replace("-", "").replace("/", "").strip();

        if (formatado.isBlank())
        {
            this.complemento = null;
            return;
        }

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

    public static Endereco fromDatabaseString(String endereco)
    {
        if (endereco == null || endereco.isBlank())
        {
            throw new EnderecoWithInvalidInformationException(INVALID_DATABASE_FORMAT_MESSAGE);
        }

        String enderecoFormatado = endereco.strip();

        if (enderecoFormatado.endsWith(" -"))
        {
            throw new EnderecoWithInvalidInformationException(INVALID_DATABASE_FORMAT_MESSAGE);
        }

        String[] segmentos = enderecoFormatado.split(" - ", -1);

        if (segmentos.length < 3 || segmentos.length > 4)
        {
            throw new EnderecoWithInvalidInformationException(INVALID_DATABASE_FORMAT_MESSAGE);
        }

        int separadorNumero = segmentos[0].lastIndexOf(", ");
        int separadorEstado = segmentos[2].lastIndexOf("/");

        if (separadorNumero <= 0 || separadorNumero == segmentos[0].length() - 2 ||
                separadorEstado <= 0 || separadorEstado == segmentos[2].length() - 1 ||
                segmentos[1].isBlank() || (segmentos.length == 4 && segmentos[3].isBlank()))
        {
            throw new EnderecoWithInvalidInformationException(INVALID_DATABASE_FORMAT_MESSAGE);
        }

        String logradouro = segmentos[0].substring(0, separadorNumero);
        String numero = segmentos[0].substring(separadorNumero + 2);
        String bairro = segmentos[1];
        String cidade = segmentos[2].substring(0, separadorEstado);
        String estado = segmentos[2].substring(separadorEstado + 1);

        try
        {
            int numeroConvertido = Integer.parseInt(numero);

            if (segmentos.length == 4)
            {
                return new Endereco(logradouro, numeroConvertido, bairro, cidade, estado, segmentos[3]);
            }

            return new Endereco(logradouro, numeroConvertido, bairro, cidade, estado);
        }
        catch (NumberFormatException exception)
        {
            throw new EnderecoWithInvalidInformationException(INVALID_DATABASE_FORMAT_MESSAGE);
        }
    }


}
