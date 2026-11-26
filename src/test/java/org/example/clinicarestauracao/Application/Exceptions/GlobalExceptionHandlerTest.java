package org.example.clinicarestauracao.Application.Exceptions;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ErrorResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Convenio.ConvenioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Convenio.ConvenioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Endereco.EnderecoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoInvalidQueryException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.User.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UsernameAlredyInUseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest
{
    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @DisplayName("Deve retornar HTTP 401 quando as credenciais forem inválidas")
    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid()
    {
        var exception = new BadCredentialsException("mensagem interna");

        var response = handler.handleBadCredentialsException(exception);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Usuário ou senha inválidos.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 409 quando o nome de usuário já estiver em uso")
    @Test
    void shouldReturnConflictWhenUsernameIsInUse()
    {
        var exception = new UsernameAlredyInUseException("mensagem interna");

        var response = handler.handleUsernameAlreadyInUse(exception);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("mensagem interna", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações do usuário forem inválidas")
    @Test
    void shouldReturnBadRequestWhenUserInformationIsInvalid()
    {
        var exception = new UserWithInvalidInformationException("Nome de usuário não pode ser vazio.");

        var response = handler.handleUserWithNullInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome de usuário não pode ser vazio.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 404 quando o convênio não existir")
    @Test
    void shouldReturnNotFoundWhenConvenioDoesNotExist()
    {
        ConvenioNotFoundException exception = new ConvenioNotFoundException("Convênio não encontrado.");

        ResponseEntity<ErrorResponseDto> response = handler.handleConvenioNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Convênio não encontrado.", response.getBody().message());
    }
    @DisplayName("Deve retornar HTTP 400 quando as informações do funcionário forem inválidas")
    @Test
    void shouldReturnBadRequestWhenFuncionarioInformationIsInvalid()
    {
        var exception = new FuncionarioWithInvalidInformationException("Nome do funcionário não pode ser vazio.");

        var response = handler.handleFuncionarioWithInvalidInformation(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Nome do funcionário não pode ser vazio.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações do convênio forem inválidas")
    @Test
    void shouldReturnBadRequestWhenConvenioInformationIsInvalid()
    {
        var exception = new ConvenioWithInvalidInformationException("Nome do convênio não pode ser vazio.");

        var response = handler.handleConvenioWithInvalidInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome do convênio não pode ser vazio.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações do cargo forem inválidas")
    @Test
    void shouldReturnBadRequestWhenCargoInformationIsInvalid()
    {
        var exception = new CargoWithInvalidInformationException("Nome do cargo não pode ser nulo ou vazio.");

        var response = handler.handleCargoWithInvalidInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome do cargo não pode ser nulo ou vazio.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 404 quando o cargo não for encontrado")
    @Test
    void shouldNotFoundRequestWhenCargoNotFound()
    {
        var exception = new CargoNotFoundException("Cargo não encontrado.");

        var response = handler.handleCargoNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cargo não encontrado.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 404 quando o funcionário não existir")
    @Test
    void shouldReturnNotFoundWhenFuncionarioDoesNotExist()
    {
        var exception = new FuncionarioNotFoundException("Funcionário não encontrado.");

        var response = handler.handleFuncionarioNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Funcionário não encontrado.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 404 quando o usuário não existir")
    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist()
    {
        UserNotFoundException exception = new UserNotFoundException("Usuário não encontrado.");

        ResponseEntity<ErrorResponseDto> response = handler.handleUserNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuário não encontrado.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações do endereço forem inválidas")
    @Test
    void shouldReturnBadRequestWhenEnderecoWithInvalidInformation()
    {
        EnderecoWithInvalidInformationException exception = new EnderecoWithInvalidInformationException("Endereço com campos inválidos.");

        ResponseEntity<ErrorResponseDto> response = handler.handleEnderecoWithInvalidInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Endereço com campos inválidos.",  response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações do acolhido forem inválidas")
    @Test
    void shouldReturnBadRequestWhenAcolhidoInformationIsInvalid()
    {
        AcolhidoWithInvalidInformationException exception = new AcolhidoWithInvalidInformationException("CPF já cadastrado.");

        ResponseEntity<ErrorResponseDto> response = handler.handleAcolhidoWithInvalidInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CPF já cadastrado.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 404 quando o acolhido não existir")
    @Test
    void shouldReturnNotFoundWhenAcolhidoDoesNotExist()
    {
        AcolhidoNotFoundException exception = new AcolhidoNotFoundException("Acolhido não encontrado.");

        ResponseEntity<ErrorResponseDto> response = handler.handleAcolhidoNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acolhido não encontrado.", response.getBody().message());
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações da internação forem inválidas")
    @Test
    void shouldReturnBadRequestWhenInternacaoInformationIsInvalid()
    {
        InternacaoWithInvalidInformationException exception = new InternacaoWithInvalidInformationException("Internação encerrada não pode ser atualizada.");

        ResponseEntity<ErrorResponseDto> response = handler.handleInternacaoWithInvalidInformation(exception);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Internação encerrada não pode ser atualizada.", response.getBody().message())
        );
    }

    @DisplayName("Deve retornar HTTP 404 quando a internação não existir")
    @Test
    void shouldReturnNotFoundWhenInternacaoDoesNotExist()
    {
        InternacaoNotFoundException exception = new InternacaoNotFoundException("Internação não encontrada.");

        ResponseEntity<ErrorResponseDto> response = handler.handleInternacaoNotFound(exception);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Internação não encontrada.", response.getBody().message())
        );
    }

    @DisplayName("Deve retornar HTTP 400 quando a consulta de internação for inválida")
    @Test
    void shouldReturnBadRequestWhenInternacaoQueryIsInvalid()
    {
        InternacaoInvalidQueryException exception = new InternacaoInvalidQueryException("Data de início não pode ser posterior à data de fim.");

        ResponseEntity<ErrorResponseDto> response = handler.handleInternacaoInvalidQueryException(exception);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Data de início não pode ser posterior à data de fim.", response.getBody().message())
        );
    }

    @DisplayName("Deve retornar HTTP 400 quando as informações do responsável forem inválidas")
    @Test
    void shouldReturnBadRequestWhenResponsavelInformationIsInvalid()
    {
        ResponsavelWithInvalidInformationException exception = new ResponsavelWithInvalidInformationException("CPF do responsável é inválido.");

        ResponseEntity<ErrorResponseDto> response = handler.handleResponsavelWithInvalidInformation(exception);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("CPF do responsável é inválido.", response.getBody().message())
        );
    }

    @DisplayName("Deve retornar não encontrado quando responsável não existir")
    @Test
    void shouldReturnNotFoundWhenResponsavelDoesNotExist()
    {
        ResponsavelNotFoundException exception = new ResponsavelNotFoundException("Responsável não encontrado.");

        ResponseEntity<ErrorResponseDto> response = handler.handleResponsavelNotFound(exception);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Responsável não encontrado.", response.getBody().message())
        );
    }

}
