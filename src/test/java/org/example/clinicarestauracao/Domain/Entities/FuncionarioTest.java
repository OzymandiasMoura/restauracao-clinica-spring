package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest
{
    //Testes para o nome

    @ParameterizedTest
    @CsvSource({
            "'       Pedro      ', 'Pedro'",
            "'  Pedro Moura  ', 'Pedro Moura'",
            "'Pedro Moura', 'Pedro Moura'"
    })
    void shouldNormalizeLeadingAndTrailingSpaces(String nome, String nomeEsperado)
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome(nome).build();

        assertEquals(nomeEsperado, funcionario.getNome());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankName(String nome)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Funcionário não pode ter o nome vazio ou em branco.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "Pe"})
    void shouldRejectNameWithLessThanThreeCharacters(String nome)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Nome deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldCreateFuncionarioWithValidName()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura").build();

        assertEquals("Pedro Moura", funcionario.getNome());
    }

    @Test
    void shouldRejectShortNameAfterNormalization()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome("  Pe  ").build());

        assertEquals("Nome deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    //Testes para o CPF

    @Test
    void shouldNormalizeCpf()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-25").build();

        assertEquals("52998224725", funcionario.getCpf());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf(cpf).build());

        assertEquals("CPF não pode ser vazio ou em branco.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidCpf()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-24").build());

        assertEquals("CPF é inválido.", ex.getMessage());
    }

    //Testes de email

    @Test
    void shouldNormalizeEmail()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEmail("  PEDRO@EMAIL.COM  ").build();

        assertEquals("pedro@email.com", funcionario.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectNullOrBlankEmail(String email)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEmail(email).build());

        assertEquals("E-mail não pode ser vazio ou em branco.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidEmail()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEmail("pedro@email").build());

        assertEquals("E-mail é inválido.", exception.getMessage());
    }

    //Testes para dataNascimento

    @Test
    void shouldCreateFuncionarioWithValidBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).build();

        assertEquals(dataNascimento, funcionario.getDataNascimento());
    }

    @Test
    void shouldRejectNullBirthDate()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(null).build());

        assertEquals("É necessário definir a data de nascimento.", ex.getMessage());
    }

    @Test
    void shouldRejectFutureBirthDate()
    {
        LocalDate dataFutura = LocalDate.now().plusDays(1);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataFutura).build());

        assertEquals("Data de nascimento não pode ser futura.", exception.getMessage());
    }

    @Test
    void shouldRejectBirthDateAfterAdmissionDate()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(LocalDate.of(1990, 1, 10)).setDataAdmissao(LocalDate.of(2020, 1, 10)).build();

        LocalDate nascimentoPosteriorAdmissao = LocalDate.of(2021, 1, 10);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.setDataNascimento(nascimentoPosteriorAdmissao));

        assertEquals("Data de nascimento não pode ser depois da admissão.", exception.getMessage());
    }

    //Testes de endereço

    @Test
    void shouldCreateFuncionarioWithValidAddress()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEndereco("Praça da Sé, 1 - São Paulo - SP").build();

        assertEquals("Praça da Sé, 1 - São Paulo - SP", funcionario.getEndereco());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankAddress(String endereco)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEndereco(endereco).build());

        assertEquals("Endereço não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    void shouldNormalizeLeadingAndTrailingSpacesFromAddress()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEndereco("   Praça da Sé, 1 - São Paulo - SP   ").build();

        assertEquals("Praça da Sé, 1 - São Paulo - SP", funcionario.getEndereco());
    }

    //Testes para CEP

    @ParameterizedTest
    @CsvSource({
            "'12345678', '12345678'",
            "'12345-678', '12345678'",
            "'  12345-678  ', '12345678'"
    })
    void shouldNormalizeCep(String cep, String cepEsperado)
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCep(cep).build();

        assertEquals(cepEsperado, funcionario.getCep());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankCep(String cep)
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCep(cep).build());

        assertEquals("CEP não pode ser nulo ou vazio.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567",
            "123456789",
            "12345-67A",
            "12345 678",
            "12345@678"
    })
    void shouldRejectInvalidCep(String cep)
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCep(cep).build());

        assertEquals("CEP é inválido.", ex.getMessage());
    }

    //Testes para User

    @Test
    void shouldAssociateUserWithFuncionario()
    {
        User user = new User("pedro", "senha123", UserRoles.USER);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setUser(user).build();

        assertSame(user, funcionario.getUser());
    }

    @Test
    void shouldAllowFuncionarioWithoutUser()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setUser(null).build();

        assertNull(funcionario.getUser());
    }

    //Testar ativo
    @Test
    void shouldCreateNewFuncionarioAsActive()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setAtivo(false).buildForCreate();

        assertTrue(funcionario.isAtivo());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldPreserveActiveStatusWhenRebuildingFuncionario(boolean ativo)
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setAtivo(ativo).build();

        assertEquals(ativo, funcionario.isAtivo());
    }

    @Test
    void shouldCreateNewFuncionarioWithoutUserAsActive()
    {
        Funcionario funcionario = new Funcionario("Pedro Moura", "52998224725", "pedro@email.com", LocalDate.of(1990, 1, 10), "Praça da Sé, 1 - São Paulo - SP", "01001000", CargoTestBuilder.newCargo().build(), LocalDate.now());

        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getUser());
    }

    @Test
    void shouldAssociateCargoWithFuncionario()
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCargo(cargo).build();
        assertSame(cargo, funcionario.getCargo());
    }

    @Test
    void shouldRejectNullCargo()
    {
        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCargo(null).build());
        assertEquals("Cargo do funcionário deve ser informado.", exception.getMessage());
    }

    // Testes para dataAdmissao

    @Test
    void shouldCreateFuncionarioWithValidAdmissionDate()
    {
        LocalDate dataAdmissao = LocalDate.of(2020, 1, 10);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).build();

        assertEquals(dataAdmissao, funcionario.getDataAdmissao());
    }

    @Test
    void shouldRejectNullAdmissionDate()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataAdmissao(null).build());

        assertEquals("Data de admissão não pode ser nula.", exception.getMessage());
    }

    @Test
    void shouldRejectFutureAdmissionDate()
    {
        LocalDate dataFutura = LocalDate.now().plusDays(1);
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataFutura).build());

        assertEquals("Data de admissão não pode ser futura.", exception.getMessage());
    }

    @Test
    void shouldRejectAdmissionDateBeforeBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);
        LocalDate dataAdmissao = LocalDate.of(1989, 12, 31);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).setDataAdmissao(dataAdmissao).build());

        assertEquals("Data de admissão não pode ser anterior ao nascimento.", exception.getMessage());
    }

    // Testes para dataDemissao

    @Test
    void shouldDismissActiveFuncionarioWithValidDate()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).setAtivo(true).build();

        funcionario.demitir(hoje);

        assertFalse(funcionario.isAtivo());
        assertEquals(hoje, funcionario.getDataDemissao());
    }

    @Test
    void shouldRejectDismissalWithoutDate()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setAtivo(true).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.demitir(null));

        assertEquals("Data de demissão deve ser informada.", exception.getMessage());
        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getDataDemissao());
    }

    @Test
    void shouldRejectDismissalDateBeforeAdmissionDate()
    {
        LocalDate dataAdmissao = LocalDate.of(2026, 1, 10);
        LocalDate dataDemissao = dataAdmissao.minusDays(1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).setAtivo(true).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.demitir(dataDemissao));

        assertEquals("Data de demissão não pode ser anterior ou igual a data de admissão.", exception.getMessage());
        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getDataDemissao());
    }

    @Test
    void shouldRejectDismissalDateEqualToAdmissionDate()
    {
        LocalDate dataAdmissao = LocalDate.of(2026, 1, 10);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).setAtivo(true).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.demitir(dataAdmissao));

        assertEquals("Data de demissão não pode ser anterior ou igual a data de admissão.", exception.getMessage());
        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getDataDemissao());
    }

    @Test
    void shouldRejectFutureDismissalDate()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate dataDemissao = hoje.plusDays(1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).setAtivo(true).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.demitir(dataDemissao));

        assertEquals("Data de demissão não pode ser futura.", exception.getMessage());
        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getDataDemissao());
    }

    @Test
    void shouldPreserveDismissalDateWhenNewDismissalDateIsInvalid()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate primeiraDemissao = hoje.minusDays(1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).setAtivo(true).build();

        funcionario.demitir(primeiraDemissao);

        assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.demitir(hoje.plusDays(1)));
        assertFalse(funcionario.isAtivo());
        assertEquals(primeiraDemissao, funcionario.getDataDemissao());
    }

    @Test
    void shouldUpdateDismissalDateWhenFuncionarioIsAlreadyInactive()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate primeiraDemissao = hoje.minusDays(2);
        LocalDate novaDemissao = hoje.minusDays(1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(hoje.minusYears(1)).setAtivo(true).build();

        funcionario.demitir(primeiraDemissao);
        funcionario.demitir(novaDemissao);

        assertFalse(funcionario.isAtivo());
        assertEquals(novaDemissao, funcionario.getDataDemissao());
    }

    //Teste para reactivate
    @Test
    void shouldReactivateDismissedFuncionario()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate dataDemissao = hoje.minusDays(1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).setDataDemissao(dataDemissao).setAtivo(false).build();

        funcionario.reactivate();

        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getDataDemissao());
        assertEquals(dataAdmissao, funcionario.getDataAdmissao());
    }

}