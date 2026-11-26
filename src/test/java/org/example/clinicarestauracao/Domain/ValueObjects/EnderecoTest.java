package org.example.clinicarestauracao.Domain.ValueObjects;


import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("Deve definir logradouro com sucesso")
    @Test
    void shouldSetLogradouroSuccessfully()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().build();

        endereco.setLogradouro("Rua do Mato");

        assertEquals("Rua do Mato", endereco.getLogradouro());
    }

    @DisplayName("Deve remover espaços excedentes do logradouro")
    @Test
    void shouldTrimLogradouro()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setLogradouro("   Rua do Mato   ").build();

        assertEquals("Rua do Mato", endereco.getLogradouro());
    }

    @DisplayName("Deve remover hífen e barra de logradouro")
    @Test
    void shouldRemoveHyphenAndSlashFromLogradouro()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setLogradouro("Rua-das/Flores").build();

        assertEquals("RuadasFlores", endereco.getLogradouro());
    }

    @DisplayName("Deve rejeitar logradouro nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldRejectBlankAndNullLogradouro(String logradouro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build());

        assertEquals("Logradouro não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar logradouro com menos de três caracteres após remover os espaços")
    @ParameterizedTest
    @ValueSource(strings = {"Ru", " Ru ", "   Ru   "})
    void shouldRejectLogradouroShorterThanThreeCharactersAfterTrim(String logradouro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build());

        assertEquals( "Logradouro deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar logradouro com exatamente três caracteres")
    @Test
    void shouldAcceptLogradouroWithExactlyThreeCharacters()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setLogradouro("Rua").build();

        assertEquals("Rua", endereco.getLogradouro());
    }

    @DisplayName("Deve rejeitar logradouro com mais de 250 caracteres após remover os espaços")
    @Test
    void shouldRejectLogradouroLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String logradouro = "   " + "A".repeat(251) + "   ";
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build());

        assertEquals("Logradouro pode ter no máximo 250 caracteres.",  exception.getMessage());
    }

    @DisplayName("Deve aceitar logradouro com exatamente duzentos e cinquenta caracteres")
    @Test
    void shouldAcceptLogradouroWithExactlyTwoHundredFiftyCharacters()
    {
        String logradouro = "   " + "A".repeat(250) + "   ";
        Endereco endereco = EnderecoTestBuilder.newEndereco().setLogradouro(logradouro).build();

        assertEquals(logradouro.strip(), endereco.getLogradouro());
        assertEquals(250, endereco.getLogradouro().length());
    }

    //Testes setNumero

    @DisplayName("Deve definir número com sucesso")
    @Test
    void shouldSetNumeroSuccessfully()
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setNumero(200).build();

        assertEquals(200, endereco.getNumero());
    }

    @DisplayName("Deve rejeitar número nulo")
    @Test
    void shouldRejectNullNumero()
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setNumero(null).build());

        assertEquals("Numero não pode ser nulo.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar número não positivo")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void shouldRejectNonPositiveNumero(Integer numero)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setNumero(numero).build());

        assertEquals("Numero não pode ser menor ou igual a zero.", exception.getMessage());
    }

    @DisplayName("Deve aceitar o número um")
    @Test
    void shouldAcceptNumeroEqualToOne()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setNumero(1).build();

        assertEquals(1, endereco.getNumero());
    }

    @DisplayName("Deve aceitar número igual ao limite máximo")
    @Test
    void shouldAcceptNumeroEqualToMaximumLimit()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setNumero(10000).build();

        assertEquals(10000, endereco.getNumero());
    }

    @DisplayName("Deve rejeitar número maior que o limite máximo")
    @Test
    void shouldRejectNumeroGreaterThanMaximumLimit()
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setNumero(10001).build());

        assertEquals("Numero de endereço não pode ser maior que 10.000", exception.getMessage());
    }

    @DisplayName("Deve preservar o número após rejeitar um valor acima do limite máximo")
    @Test
    void shouldPreserveNumeroAfterRejectingValueGreaterThanMaximumLimit()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setNumero(500).build();

        assertThrows(EnderecoWithInvalidInformationException.class, () -> endereco.setNumero(10001));

        assertEquals(500, endereco.getNumero());
    }

    //Testes setBairro

    @DisplayName("Deve definir bairro com sucesso")
    @Test
    void shouldSetBairroSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro("Vila Madalena").build();

        assertEquals("Vila Madalena", endereco.getBairro());
    }

    @DisplayName("Deve remover espaços excedentes do bairro")
    @Test
    void shouldTrimBairro()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro("  Vila Madalena  ").build();

        assertEquals("Vila Madalena", endereco.getBairro());
    }

    @DisplayName("Deve remover hífen e barra de bairro")
    @Test
    void shouldRemoveHyphenAndSlashFromBairro()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro("Centro-Sul/Oeste").build();

        assertEquals("CentroSulOeste", endereco.getBairro());
    }

    @DisplayName("Deve rejeitar bairro nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankBairro(String bairro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setBairro(bairro).build());

        assertEquals("Bairro não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar bairro menor que três caracteres depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings = {"VM", " VM ", "    VM", "VM     "})
    void shouldRejectBairroShorterThanThreeCharactersAfterTrim(String bairro)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setBairro(bairro).build());

        assertEquals("Bairro deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar bairro com exatamente três caracteres sem espaços")
    @ParameterizedTest
    @ValueSource(strings = {"VMA", "VMA  ", " VMa "})
    void shouldAcceptBairroWithExactlyThreeCharactersWithoutSpaces(String bairro)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro(bairro).build();

        assertEquals(bairro.strip(), endereco.getBairro());
        assertEquals(3, endereco.getBairro().length());
    }

    @DisplayName("Deve rejeitar bairro maior que duzentos e cinquenta caracteres depois remover espaços")
    @Test
    void shouldRejectBairroLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String bairro = "   " + "A".repeat(251) + "   ";
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setBairro(bairro).build());

        assertEquals("Bairro pode ter no máximo 250 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar bairro com exatamente duzentos e cinquenta caracteres")
    @Test
    void shouldAcceptBairroWithExactlyTwoHundredFiftyCharacters()
    {
        String bairro = "   " + "A".repeat(250) + "   ";

        Endereco endereco = EnderecoTestBuilder.newEndereco().setBairro(bairro).build();

        assertEquals(bairro.strip(), endereco.getBairro());
    }

    //Testes setCidade
    @DisplayName("Deve definir cidade com sucesso")
    @Test
    void shouldSetCidadeSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade("São Paulo").build();

        assertEquals("São Paulo", endereco.getCidade());
    }

    @DisplayName("Deve remover espaços excedentes da cidade")
    @Test
    void shouldTrimCidade()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade(" São Paulo ").build();

        assertEquals("São Paulo", endereco.getCidade());
    }

    @DisplayName("Deve remover hífen e barra de cidade")
    @Test
    void shouldRemoveHyphenAndSlashFromCidade()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade("São-José/do Rio").build();

        assertEquals("SãoJosédo Rio", endereco.getCidade());
    }

    @DisplayName("Deve rejeitar cidade nula ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankCidade(String cidade)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setCidade(cidade).build());

        assertEquals("Cidade não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar cidade menor que três caracteres depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings = {"Sp", " Sp ", " Sp", "Sp "})
    void shouldRejectCidadeShorterThanThreeCharactersAfterTrim(String cidade)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setCidade(cidade).build());

        assertEquals("Cidade deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar cidade com exatamente três caracteres depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings = {"Spa", " Spa ", " Spa", "Spa "})
    void shouldAcceptCidadeWithExactlyThreeCharactersAfterTrim(String cidade)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade(cidade).build();

        assertEquals(cidade.strip(), endereco.getCidade());
        assertEquals(3, endereco.getCidade().length());
    }

    @DisplayName("Deve rejeitar cidade maior que duzentos e cinquenta caracteres depois remover espaços")
    @Test
    void shouldRejectCidadeLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String cidade = "   " + "A".repeat(251) + "   ";

        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setCidade(cidade).build());

        assertEquals("Cidade pode ter no máximo 250 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar cidade com exatamente duzentos e cinquenta caracteres")
    @Test
    void shouldAcceptCidadeWithExactlyTwoHundredFiftyCharacters()
    {
        String cidade = "   " + "A".repeat(250) + "   ";

        Endereco endereco = EnderecoTestBuilder.newEndereco().setCidade(cidade).build();

        assertEquals(cidade.strip(), endereco.getCidade());
    }

    //Testes setEstado

    @DisplayName("Deve definir estado com sucesso")
    @Test
    void shouldSetEstadoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado("SP").build();

        assertEquals("SP", endereco.getEstado());
    }

    @DisplayName("Deve remover espaços excedentes do estado")
    @Test
    void shouldTrimEstado()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado(" SP ").build();

        assertEquals("SP", endereco.getEstado());
    }

    @DisplayName("Deve normalizar estado para maiúsculas caso")
    @Test
    void shouldNormalizeEstadoToUpperCase()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado("sp").build();

        assertEquals("SP", endereco.getEstado());
    }

    @DisplayName("Deve remover hífen e barra de estado")
    @Test
    void shouldRemoveHyphenAndSlashFromEstado()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado("S/P-").build();

        assertEquals("SP", endereco.getEstado());
    }

    @DisplayName("Deve rejeitar estado nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldRejectNullOrBlankEstado(String estado)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setEstado(estado).build());

        assertEquals("Estado não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar estado com comprimento diferente de dois depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings = {"Spa", " Spa ", "S", " S "})
    void shouldRejectEstadoWithLengthDifferentFromTwoAfterTrim(String estado)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setEstado(estado).build());

        assertEquals("Estado deve ter 2 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar estado com exatamente dois caracteres depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings = {"Sp", " SP ", " Sp", " SP "})
    void shouldAcceptEstadoWithExactlyTwoCharactersAfterTrim(String estado)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setEstado(estado).build();
        assertEquals(estado.strip().toUpperCase(), endereco.getEstado());
    }

    //Testes setComplemento

    @DisplayName("Deve definir complemento com sucesso")
    @Test
    void shouldSetComplementoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento("Apartamento 215").build();

        assertEquals("Apartamento 215", endereco.getComplemento());
    }

    @DisplayName("Deve remover espaços complemento")
    @Test
    void shouldTrimComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento(" Apartamento 215 ").build();

        assertEquals("Apartamento 215", endereco.getComplemento());
    }

    @DisplayName("Deve remover hífen e barra de complemento")
    @Test
    void shouldRemoveHyphenAndSlashFromComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento("Apartamento-/-42").build();

        assertEquals("Apartamento42", endereco.getComplemento());
    }

    @DisplayName("Deve definir complemento como nulo quando contiver somente hífens e barras")
    @Test
    void shouldSetComplementoToNullWhenItContainsOnlyHyphensAndSlashes()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento("-/-").build();

        assertNull(endereco.getComplemento());
    }

    @DisplayName("Deve definir complemento como nulo quando for nulo ou estiver em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldSetComplementoToNullWhenNullOrBlank(String complemento)
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setComplemento(complemento).build();

        assertNull(endereco.getComplemento());
    }

    @DisplayName("Deve rejeitar complemento menor que três caracteres depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings = {"Ap", " Ap ", " Ap", "Ap "})
    void shouldRejectComplementoShorterThanThreeCharactersAfterTrim(String complemento)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setComplemento(complemento).build());

        assertEquals("Complemento deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar complemento com exatamente três caracteres depois remover espaços")
    @ParameterizedTest
    @ValueSource(strings= {"Apt", " Apt ", "Apt ", " Apt"})
    void shouldAcceptComplementoWithExactlyThreeCharactersAfterTrim(String complemento)
    {
        Endereco endereco =  EnderecoTestBuilder.newEndereco().setComplemento(complemento).build();

        assertEquals(complemento.strip(), endereco.getComplemento());
    }

    @DisplayName("Deve rejeitar complemento maior que duzentos e cinquenta caracteres depois remover espaços")
    @Test
    void shouldRejectComplementoLongerThanTwoHundredFiftyCharactersAfterTrim()
    {
        String complemento = "   " + "A".repeat(251) + "   ";

        EnderecoWithInvalidInformationException exception = assertThrows(EnderecoWithInvalidInformationException.class, () -> EnderecoTestBuilder.newEndereco().setComplemento(complemento).build());

        assertEquals("Complemento pode ter no máximo 250 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar complemento com exatamente duzentos e cinquenta caracteres")
    @Test
    void shouldAcceptComplementoWithExactlyTwoHundredFiftyCharacters()
    {
        String complemento = "   " + "A".repeat(250) + "   ";

        Endereco endereco =  EnderecoTestBuilder.newEndereco().setComplemento(complemento).build();

        assertEquals(complemento.strip(), endereco.getComplemento());
    }

    @DisplayName("Deve criar endereço sem complemento")
    @Test
    void shouldCreateEnderecoWithoutComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().buildWithoutComplemento();

        assertNull(endereco.getComplemento());
    }

    //Testes toDatabaseString

    @DisplayName("Deve converter endereço para banco de dados texto com complemento")
    @Test
    void shouldConvertEnderecoToDatabaseStringWithComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        String resultado = endereco.toDatabaseString();

        assertEquals("Rua das Flores, 123 - Centro - São Paulo/SP - Apartamento 42", resultado);
    }

    @DisplayName("Deve converter endereço para banco de dados texto sem complemento")
    @Test
    void shouldConvertEnderecoToDatabaseStringWithoutComplemento()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().buildWithoutComplemento();

        String resultado = endereco.toDatabaseString();

        assertEquals("Rua das Flores, 123 - Centro - São Paulo/SP", resultado);
    }

    @DisplayName("Deve remover reservado caracteres antes convertendo endereço para banco de dados texto")
    @Test
    void shouldRemoveReservedCharactersBeforeConvertingEnderecoToDatabaseString()
    {
        Endereco endereco = new Endereco(
                "Rua-/das Flores",
                123,
                "Centro-/Sul",
                "São-/Paulo",
                "S/P-",
                "Apartamento-/-42"
        );

        String resultado = endereco.toDatabaseString();

        assertEquals("Ruadas Flores, 123 - CentroSul - SãoPaulo/SP - Apartamento42", resultado);
    }

    //Testes fromDatabaseString

    @DisplayName("Deve criar endereço de banco de dados texto com complemento")
    @Test
    void shouldCreateEnderecoFromDatabaseStringWithComplemento()
    {
        Endereco endereco = Endereco.fromDatabaseString("Rua das Flores, 123 - Centro - São Paulo/SP - Apartamento 42");

        assertAll(
                () -> assertEquals("Rua das Flores", endereco.getLogradouro()),
                () -> assertEquals(123, endereco.getNumero()),
                () -> assertEquals("Centro", endereco.getBairro()),
                () -> assertEquals("São Paulo", endereco.getCidade()),
                () -> assertEquals("SP", endereco.getEstado()),
                () -> assertEquals("Apartamento 42", endereco.getComplemento())
        );
    }

    @DisplayName("Deve criar endereço de banco de dados texto sem complemento")
    @Test
    void shouldCreateEnderecoFromDatabaseStringWithoutComplemento()
    {
        Endereco endereco = Endereco.fromDatabaseString("Rua das Flores, 123 - Centro - São Paulo/SP");

        assertAll(
                () -> assertEquals("Rua das Flores", endereco.getLogradouro()),
                () -> assertEquals(123, endereco.getNumero()),
                () -> assertEquals("Centro", endereco.getBairro()),
                () -> assertEquals("São Paulo", endereco.getCidade()),
                () -> assertEquals("SP", endereco.getEstado()),
                () -> assertNull(endereco.getComplemento())
        );
    }

    @DisplayName("Deve rejeitar texto nulo ou em branco vindo do banco de dados")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankDatabaseString(String endereco)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(
                EnderecoWithInvalidInformationException.class,
                () -> Endereco.fromDatabaseString(endereco)
        );

        assertEquals("Endereço em formato inválido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar texto do banco de dados com formato inválido")
    @ParameterizedTest
    @ValueSource(strings = {
            "Rua das Flores - Centro - São Paulo/SP",
            "Rua das Flores, abc - Centro - São Paulo/SP",
            "Rua das Flores, 123 - São Paulo/SP",
            "Rua das Flores, 123 - Centro - São Paulo",
            "Rua das Flores, 123 - Centro - São Paulo/SP - "
    })
    void shouldRejectDatabaseStringWithInvalidFormat(String endereco)
    {
        EnderecoWithInvalidInformationException exception = assertThrows(
                EnderecoWithInvalidInformationException.class,
                () -> Endereco.fromDatabaseString(endereco)
        );

        assertEquals("Endereço em formato inválido.", exception.getMessage());
    }
}
