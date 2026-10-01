package org.example.clinicarestauracao.Domain.ValueObjects;

import org.example.clinicarestauracao.Application.Exceptions.Endereco.EnderecoWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoTest
{
    //Testes setLogradouro

    @Test
    void shouldSetLogradouroSuccessfully()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().build();

        endereco.setLogradouro("Rua do Mato");

        assertEquals("Rua do Mato", endereco.getLogradouro());
    }

    @Test
    void shouldTrimLogradouro()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setLogradouro("   Rua do Mato   ").build();

        assertEquals("Rua do Mato", endereco.getLogradouro());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldRejectBlankAndNullLogradouro(String logradouro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build());

        assertEquals("Logradouro não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ru", " Ru ", "   Ru   "})
    void shouldRejectLogradouroShorterThanThreeCharactersAfterTrim(String logradouro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build());

        assertEquals( "Logradouro deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldAcceptLogradouroWithExactlyThreeCharacters()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setLogradouro("Rua").build();

        assertEquals("Rua", endereco.getLogradouro());
    }

    @Test
    void shouldRejectLogradouroLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String logradouro = "   " + "A".repeat(251) + "   ";
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build());

        assertEquals("Logradouro pode ter no máximo 250 caracteres.",  exception.getMessage());
    }

    @Test
    void shouldAcceptLogradouroWithExactlyTwoHundredFiftyCharacters()
    {
        String logradouro = "   " + "A".repeat(250) + "   ";
        Endereco endereco = EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build();

        assertEquals(logradouro.strip(), endereco.getLogradouro());
        assertEquals(250, endereco.getLogradouro().length());
    }

    //Testes setNumero

    @Test
    void shouldSetNumeroSuccessfully()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setNumero(200).build();

        assertEquals(200, endereco.getNumero());
    }

    @Test
    void shouldRejectNullNumero()
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setNumero(null).build());

        assertEquals("Numero não pode ser nulo.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void shouldRejectNonPositiveNumero(Integer numero)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setNumero(numero).build());

        assertEquals("Numero não pode ser menor ou igual a zero.", exception.getMessage());
    }

    @Test
    void shouldAcceptNumeroEqualToOne()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setNumero(1).build();

        assertEquals(1, endereco.getNumero());
    }

    //Testes setBairro

    @Test
    void shouldSetBairroSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro("Vila Madalena").build();

        assertEquals("Vila Madalena", endereco.getBairro());
    }

    @Test
    void shouldTrimBairro()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro("  Vila Madalena  ").build();

        assertEquals("Vila Madalena", endereco.getBairro());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankBairro(String bairro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setBairro(bairro).build());

        assertEquals("Bairro não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"VM", " VM ", "    VM", "VM     "})
    void shouldRejectBairroShorterThanThreeCharactersAfterTrim(String bairro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setBairro(bairro).build());

        assertEquals("Bairro deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"VMA", "VMA  ", " VMa "})
    void shouldAcceptBairroWithExactlyThreeCharactersWithoutSpaces(String bairro)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro(bairro).build();

        assertEquals(bairro.strip(), endereco.getBairro());
        assertEquals(3, endereco.getBairro().length());
    }

    @Test
    void shouldRejectBairroLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String bairro = "   " + "A".repeat(251) + "   ";
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setBairro(bairro).build());

        assertEquals("Bairro pode ter no máximo 250 caracteres.", exception.getMessage());
    }

    @Test
    void shouldAcceptBairroWithExactlyTwoHundredFiftyCharacters()
    {
        String bairro = "   " + "A".repeat(250) + "   ";

        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro(bairro).build();

        assertEquals(bairro.strip(), endereco.getBairro());
    }

    //Testes setCidade
    @Test
    void shouldSetCidadeSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade("São Paulo").build();

        assertEquals("São Paulo", endereco.getCidade());
    }

    @Test
    void shouldTrimCidade()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade(" São Paulo ").build();

        assertEquals("São Paulo", endereco.getCidade());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankCidade(String cidade)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setCidade(cidade).build());

        assertEquals("Cidade não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sp", " Sp ", " Sp", "Sp "})
    void shouldRejectCidadeShorterThanThreeCharactersAfterTrim(String cidade)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setCidade(cidade).build());

        assertEquals("Cidade deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Spa", " Spa ", " Spa", "Spa "})
    void shouldAcceptCidadeWithExactlyThreeCharactersAfterTrim(String cidade)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade(cidade).build();

        assertEquals(cidade.strip(), endereco.getCidade());
        assertEquals(3, endereco.getCidade().length());
    }

    @Test
    void shouldRejectCidadeLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String cidade = "   " + "A".repeat(251) + "   ";

        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setCidade(cidade).build());

        assertEquals("Cidade pode ter no máximo 250 caracteres.", exception.getMessage());
    }

    @Test
    void shouldAcceptCidadeWithExactlyTwoHundredFiftyCharacters()
    {
        String cidade = "   " + "A".repeat(250) + "   ";

        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade(cidade).build();

        assertEquals(cidade.strip(), endereco.getCidade());
    }

    //Testes setEstado

    @Test
    void shouldSetEstadoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado("SP").build();

        assertEquals("SP", endereco.getEstado());
    }

    @Test
    void shouldTrimEstado()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado(" SP ").build();

        assertEquals("SP", endereco.getEstado());
    }

    @Test
    void shouldNormalizeEstadoToUpperCase()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado("sp").build();

        assertEquals("SP", endereco.getEstado());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldRejectNullOrBlankEstado(String estado)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setEstado(estado).build());

        assertEquals("Estado não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Spa", " Spa ", "S", " S "})
    void shouldRejectEstadoWithLengthDifferentFromTwoAfterTrim(String estado)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setEstado(estado).build());

        assertEquals("Estado deve ter 2 caracteres.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sp", " SP ", " Sp", " SP "})
    void shouldAcceptEstadoWithExactlyTwoCharactersAfterTrim(String estado)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado(estado).build();
        assertEquals(estado.strip().toUpperCase(), endereco.getEstado());
    }

    //Testes setComplemento

    @Test
    void shouldSetComplementoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento("Apartamento 215").build();

        assertEquals("Apartamento 215", endereco.getComplemento());
    }

    @Test
    void shouldTrimComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento(" Apartamento 215 ").build();

        assertEquals("Apartamento 215", endereco.getComplemento());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldSetComplementoToNullWhenNullOrBlank(String complemento)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento(complemento).build();

        assertNull(endereco.getComplemento());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ap", " Ap ", " Ap", "Ap "})
    void shouldRejectComplementoShorterThanThreeCharactersAfterTrim(String complemento)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setComplemento(complemento).build());

        assertEquals("Complemento deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings= {"Apt", " Apt ", "Apt ", " Apt"})
    void shouldAcceptComplementoWithExactlyThreeCharactersAfterTrim(String complemento)
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setComplemento(complemento).build();

        assertEquals(complemento.strip(), endereco.getComplemento());
    }

    @Test
    void shouldRejectComplementoLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String complemento = "   " + "A".repeat(251) + "   ";

        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setComplemento(complemento).build());

        assertEquals("Complemento pode ter no máximo 250 caracteres.", exception.getMessage());
    }

    @Test
    void shouldAcceptComplementoWithExactlyTwoHundredFiftyCharacters()
    {
        String complemento = "   " + "A".repeat(250) + "   ";

        Endereco endereco =  EnderecoTestBuilder.newEndereco().setComplemento(complemento).build();

        assertEquals(complemento.strip(), endereco.getComplemento());
    }

    @Test
    void shouldCreateEnderecoWithoutComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().buildWithoutComplemento();

        assertNull(endereco.getComplemento());
    }

    //Testes toDatabaseString

    @Test
    void shouldConvertEnderecoToDatabaseStringWithComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        String resultado = endereco.toDatabaseString();

        assertEquals("Rua das Flores, 123 - Centro - São Paulo/SP - Apartamento 42", resultado);
    }

    @Test
    void shouldConvertEnderecoToDatabaseStringWithoutComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().buildWithoutComplemento();

        String resultado = endereco.toDatabaseString();

        assertEquals("Rua das Flores, 123 - Centro - São Paulo/SP", resultado);
    }
}

