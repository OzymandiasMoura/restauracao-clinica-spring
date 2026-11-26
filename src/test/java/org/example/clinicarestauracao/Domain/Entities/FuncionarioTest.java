package org.example.clinicarestauracao.Domain.Entities;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
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

    @DisplayName("Deve remover espaços iniciais e finais")
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

    @DisplayName("Deve rejeitar nome nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankName(String nome)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Funcionário não pode ter o nome vazio ou em branco.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar nome com menos de três caracteres")
    @ParameterizedTest
    @ValueSource(strings = {"A", "Pe"})
    void shouldRejectNameWithLessThanThreeCharacters(String nome)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Nome deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve criar funcionário com nome válido")
    @Test
    void shouldCreateFuncionarioWithValidName()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura").build();

        assertEquals("Pedro Moura", funcionario.getNome());
    }

    @DisplayName("Deve rejeitar nome com menos de três caracteres após a normalização")
    @Test
    void shouldRejectShortNameAfterNormalization()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome("  Pe  ").build());

        assertEquals("Nome deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar nome com mais de 250 caracteres")
    @Test
    void shouldRejectNameWithMoreThan250Characters()
    {
        String nome = "A".repeat(251);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Nome deve ter no máximo 250 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar nome com exatamente 250 caracteres")
    @Test
    void shouldAcceptNameWithExactly250Characters()
    {
        String nome = "A".repeat(250);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome(nome).build();

        assertEquals(nome, funcionario.getNome());
    }

    @DisplayName("Deve validar o comprimento máximo do nome após a normalização")
    @Test
    void shouldValidateMaximumNameLengthAfterNormalization()
    {
        String nomeNormalizado = "A".repeat(250);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome("  " + nomeNormalizado + "  ").build();

        assertEquals(nomeNormalizado, funcionario.getNome());
    }

    //Testes para o CPF

    @DisplayName("Deve normalizar CPF")
    @Test
    void shouldNormalizeCpf()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-25").build();

        assertEquals("52998224725", funcionario.getCpf());
    }

    @DisplayName("Deve rejeitar CPF nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf(cpf).build());

        assertEquals("CPF não pode ser vazio ou em branco.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar CPF inválido")
    @Test
    void shouldRejectInvalidCpf()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-24").build());

        assertEquals("CPF é inválido.", ex.getMessage());
    }

    //Testes de email

    @DisplayName("Deve normalizar e-mail")
    @Test
    void shouldNormalizeEmail()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEmail("  PEDRO@EMAIL.COM  ").build();

        assertEquals("pedro@email.com", funcionario.getEmail());
    }

    @DisplayName("Deve rejeitar e-mail nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectNullOrBlankEmail(String email)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEmail(email).build());

        assertEquals("E-mail não pode ser vazio ou em branco.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar e-mail inválido")
    @Test
    void shouldRejectInvalidEmail()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEmail("pedro@email").build());

        assertEquals("E-mail é inválido.", exception.getMessage());
    }

    //Testes para dataNascimento

    @DisplayName("Deve criar funcionário com data de nascimento válida")
    @Test
    void shouldCreateFuncionarioWithValidBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).build();

        assertEquals(dataNascimento, funcionario.getDataNascimento());
    }

    @DisplayName("Deve rejeitar data de nascimento nula")
    @Test
    void shouldRejectNullBirthDate()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(null).build());

        assertEquals("É necessário definir a data de nascimento.", ex.getMessage());
    }

    @DisplayName("Deve rejeitar data de nascimento futura")
    @Test
    void shouldRejectFutureBirthDate()
    {
        LocalDate dataFutura = LocalDate.now().plusDays(1);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataFutura).build());

        assertEquals("Data de nascimento não pode ser futura.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de nascimento posterior à data de admissão")
    @Test
    void shouldRejectBirthDateAfterAdmissionDate()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(LocalDate.of(1990, 1, 10)).setDataAdmissao(LocalDate.of(2020, 1, 10)).build();

        LocalDate nascimentoPosteriorAdmissao = LocalDate.of(2021, 1, 10);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.setDataNascimento(nascimentoPosteriorAdmissao));

        assertEquals("Data de admissão não pode ser anterior ao nascimento.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de nascimento anterior a 1900")
    @Test
    void shouldRejectBirthDateBefore1900()
    {
        LocalDate dataNascimento = LocalDate.of(1899, 12, 31);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).build());

        assertEquals("Data de nascimento não pode ser anterior a 1900.", exception.getMessage());
    }

    @DisplayName("Deve aceitar a data mínima de nascimento permitida")
    @Test
    void shouldAcceptBirthDateAtMinimumAllowedDate()
    {
        LocalDate dataNascimento = LocalDate.of(1900, 1, 1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).build();

        assertEquals(dataNascimento, funcionario.getDataNascimento());
    }

    //Testes de endereço

    @DisplayName("Deve definir endereço com sucesso")
    @Test
    void shouldSetEnderecoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEndereco(endereco).build();

        assertEquals(endereco, funcionario.getEndereco());
    }

    @DisplayName("Deve rejeitar endereço nulo")
    @Test
    void shouldRejectNullAddress()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEndereco(null).build());

        assertEquals("Endereço não pode ser nulo ou vazio.", exception.getMessage());
    }

    //Testes para CEP

    @DisplayName("Deve normalizar CEP")
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

    @DisplayName("Deve rejeitar CEP nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankCep(String cep)
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCep(cep).build());

        assertEquals("CEP não pode ser nulo ou vazio.", ex.getMessage());
    }

    @DisplayName("Deve rejeitar CEP inválido")
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

    @DisplayName("Deve associar usuário com funcionário")
    @Test
    void shouldAssociateUserWithFuncionario()
    {
        User user = new User("pedro", "senha123", UserRoles.USER);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setUser(user).build();

        assertSame(user, funcionario.getUser());
    }

    @DisplayName("Deve rejeitar funcionário sem usuário")
    @Test
    void shouldRejectFuncionarioWithoutUser()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setUser(null).build());

        assertEquals("Usuário não pode ser nulo.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar novo funcionário sem usuário")
    @Test
    void shouldRejectNewFuncionarioWithoutUser()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setUser(null).buildForCreate());

        assertEquals("Usuário não pode ser nulo.", exception.getMessage());
    }

    //Testar ativo
    @DisplayName("Deve criar novo funcionário como ativo")
    @Test
    void shouldCreateNewFuncionarioAsActive()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setAtivo(false).buildForCreate();

        assertTrue(funcionario.isAtivo());
    }

    @DisplayName("Deve preservar o estado ativo quando reconstruindo funcionário")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldPreserveActiveStatusWhenRebuildingFuncionario(boolean ativo)
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setAtivo(ativo).build();

        assertEquals(ativo, funcionario.isAtivo());
    }

    @DisplayName("Deve associar cargo com funcionário")
    @Test
    void shouldAssociateCargoWithFuncionario()
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCargo(cargo).build();
        assertSame(cargo, funcionario.getCargo());
    }

    @DisplayName("Deve rejeitar cargo nulo")
    @Test
    void shouldRejectNullCargo()
    {
        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCargo(null).build());
        assertEquals("Cargo do funcionário deve ser informado.", exception.getMessage());
    }

    // Testes para dataAdmissao

    @DisplayName("Deve criar funcionário com data de admissão válida")
    @Test
    void shouldCreateFuncionarioWithValidAdmissionDate()
    {
        LocalDate dataAdmissao = LocalDate.of(2020, 1, 10);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).build();

        assertEquals(dataAdmissao, funcionario.getDataAdmissao());
    }

    @DisplayName("Deve rejeitar data de admissão nula")
    @Test
    void shouldRejectNullAdmissionDate()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataAdmissao(null).build());

        assertEquals("Data de admissão não pode ser nula.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar futuro admissão data")
    @Test
    void shouldRejectFutureAdmissionDate()
    {
        LocalDate dataFutura = LocalDate.now().plusDays(1);
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataFutura).build());

        assertEquals("Data de admissão não pode ser futura.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de admissão anterior à data de nascimento")
    @Test
    void shouldRejectAdmissionDateBeforeBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);
        LocalDate dataAdmissao = LocalDate.of(1989, 12, 31);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).setDataAdmissao(dataAdmissao).build());

        assertEquals("Data de admissão não pode ser anterior ao nascimento.", exception.getMessage());
    }

    @DisplayName("Deve atualizar nascimento e admissão datas juntos")
    @Test
    void shouldUpdateBirthAndAdmissionDatesTogether()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(LocalDate.of(1990, 1, 10)).setDataAdmissao(LocalDate.of(2000, 1, 10)).build();

        LocalDate novaDataNascimento = LocalDate.of(2005, 1, 10);
        LocalDate novaDataAdmissao = LocalDate.of(2010, 1, 10);

        funcionario.atualizarDatas(novaDataNascimento, novaDataAdmissao);

        assertEquals(novaDataNascimento, funcionario.getDataNascimento());
        assertEquals(novaDataAdmissao, funcionario.getDataAdmissao());
    }

    @DisplayName("Deve preservar as datas quando a atualização for inválida")
    @Test
    void shouldPreserveDatesWhenUpdatedDatesAreInvalid()
    {
        LocalDate dataNascimentoExistente = LocalDate.of(1990, 1, 10);
        LocalDate dataAdmissaoExistente = LocalDate.of(2010, 1, 10);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario()
                .setDataNascimento(dataNascimentoExistente)
                .setDataAdmissao(dataAdmissaoExistente)
                .build();

        FuncionarioWithInvalidInformationException exception =
                assertThrows(
                        FuncionarioWithInvalidInformationException.class,
                        () -> funcionario.atualizarDatas(
                                LocalDate.of(2000, 1, 10),
                                LocalDate.of(1999, 1, 10)
                        )
                );

        assertEquals(
                "Data de admissão não pode ser anterior ao nascimento.",
                exception.getMessage()
        );
        assertEquals(dataNascimentoExistente, funcionario.getDataNascimento());
        assertEquals(dataAdmissaoExistente, funcionario.getDataAdmissao());
    }

    @DisplayName("Deve preservar as datas quando a nova admissão não for anterior à demissão")
    @Test
    void shouldPreserveDatesWhenNewAdmissionIsNotBeforeDismissal()
    {
        LocalDate dataNascimentoExistente = LocalDate.of(1990, 1, 10);
        LocalDate dataAdmissaoExistente = LocalDate.of(2010, 1, 10);
        LocalDate dataDemissaoExistente = LocalDate.of(2020, 1, 10);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario()
                .setDataNascimento(dataNascimentoExistente)
                .setDataAdmissao(dataAdmissaoExistente)
                .setDataDemissao(dataDemissaoExistente)
                .setAtivo(false)
                .build();

        FuncionarioWithInvalidInformationException exception =
                assertThrows(
                        FuncionarioWithInvalidInformationException.class,
                        () -> funcionario.atualizarDatas(
                                LocalDate.of(1995, 1, 10),
                                dataDemissaoExistente
                        )
                );

        assertEquals(
                "Data de admissão deve ser anterior à data de demissão.",
                exception.getMessage()
        );
        assertEquals(dataNascimentoExistente, funcionario.getDataNascimento());
        assertEquals(dataAdmissaoExistente, funcionario.getDataAdmissao());
        assertEquals(dataDemissaoExistente, funcionario.getDataDemissao());
    }

    @DisplayName("Deve rejeitar data de admissão anterior a 1900")
    @Test
    void shouldRejectAdmissionDateBefore1900()
    {
        LocalDate dataAdmissao = LocalDate.of(1899, 12, 31);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataAdmissao(dataAdmissao).build());

        assertEquals("Data de admissão não pode ser anterior a 1900.", exception.getMessage());
    }

    @DisplayName("Deve aceitar a data mínima de admissão permitida")
    @Test
    void shouldAcceptAdmissionDateAtMinimumAllowedDate()
    {
        LocalDate dataLimite = LocalDate.of(1900, 1, 1);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataLimite).setDataAdmissao(dataLimite).build();

        assertEquals(dataLimite, funcionario.getDataAdmissao());
    }
    // Testes para dataDemissao

    @DisplayName("Deve demitir o funcionário ativo com uma data válida")
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

    @DisplayName("Deve rejeitar demissão sem data")
    @Test
    void shouldRejectDismissalWithoutDate()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setAtivo(true).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.demitir(null));

        assertEquals("Data de demissão deve ser informada.", exception.getMessage());
        assertTrue(funcionario.isAtivo());
        assertNull(funcionario.getDataDemissao());
    }

    @DisplayName("Deve rejeitar data de demissão anterior à data de admissão")
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

    @DisplayName("Deve rejeitar data de demissão igual à data de admissão")
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

    @DisplayName("Deve rejeitar futuro demissão data")
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

    @DisplayName("Deve preservar a data de demissão quando a nova data for inválida")
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

    @DisplayName("Deve atualizar a data de demissão mesmo quando o funcionário já está inativo")
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

    //Testes para reactivate
    @DisplayName("Deve reativar demitido funcionário")
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

    //Testes para cargo

    @DisplayName("Deve rejeitar inativo cargo")
    @Test
    void shouldRejectInactiveCargo()
    {
        Cargo cargoInativo = CargoTestBuilder.newCargo().setAtivo(false).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCargo(cargoInativo).build());

        assertEquals("Cargo não pode estar inativo.", exception.getMessage());
    }

    @DisplayName("Deve preservar atual cargo quando novo cargo é inativo")
    @Test
    void shouldPreserveCurrentCargoWhenNewCargoIsInactive()
    {
        Cargo cargoAtual = CargoTestBuilder.newCargo().setId(1L).setAtivo(true).build();
        Cargo cargoInativo = CargoTestBuilder.newCargo().setId(2L).setAtivo(false).build();
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCargo(cargoAtual).build();

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> funcionario.setCargo(cargoInativo));

        assertEquals("Cargo não pode estar inativo.", exception.getMessage());
        assertSame(cargoAtual, funcionario.getCargo());
    }

    //Testes para setTelefone

    @DisplayName("Deve normalizar telefone")
    @ParameterizedTest
    @CsvSource({
            "'(11) 99999-9999', '11999999999'",
            "'(11) 3333-4444', '1133334444'"
    })
    void shouldNormalizeTelefone(String telefone, String telefoneEsperado)
    {
        Funcionario funcionario = FuncionarioTestBuilder
                .newFuncionario()
                .setTelefone(telefone)
                .build();

        assertEquals(telefoneEsperado, funcionario.getTelefone());
    }

    @DisplayName("Deve rejeitar telefone nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankTelefone(String telefone)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(
                FuncionarioWithInvalidInformationException.class,
                () -> FuncionarioTestBuilder
                        .newFuncionario()
                        .setTelefone(telefone)
                        .build()
        );

        assertEquals("Telefone deve ser válido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar telefone inválido")
    @ParameterizedTest
    @ValueSource(strings = {
            "119999999",
            "119999999999",
            "11899999999",
            "1199999999",
            "+55 (11) 99999-9999",
            "1199999999A"
    })
    void shouldRejectInvalidTelefone(String telefone)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(
                FuncionarioWithInvalidInformationException.class,
                () -> FuncionarioTestBuilder
                        .newFuncionario()
                        .setTelefone(telefone)
                        .build()
        );

        assertEquals("Telefone deve ser válido.", exception.getMessage());
    }
}
