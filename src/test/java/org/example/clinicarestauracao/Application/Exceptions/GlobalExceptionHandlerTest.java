package org.example.clinicarestauracao.Application.Exceptions;

import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ErrorResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Endereco.EnderecoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest
{
    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid()
    {
        var exception = new BadCredentialsException("mensagem interna");

        var response = handler.handleBadCredentialsException(exception);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Usuário ou senha inválidos.", response.getBody().message());
    }

    @Test
    void shouldReturnConflictWhenUsernameIsInUse()
    {
        var exception = new UsernameAlredyInUseException("mensagem interna");

        var response = handler.handleUsernameAlreadyInUse(exception);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("mensagem interna", response.getBody().message());
    }

    @Test
    void shouldReturnBadRequestWhenUserInformationIsInvalid()
    {
        var exception = new UserWithInvalidInformationException("Nome de usuário não pode ser vazio.");

        var response = handler.handleUserWithNullInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome de usuário não pode ser vazio.", response.getBody().message());
    }

    @Test
    void shouldReturnNotFoundWhenModalidadeDoesNotExist()
    {
        ModalidadeNotFoundException exception = new ModalidadeNotFoundException("Modalidade não encontrada.");

        ResponseEntity<ErrorResponseDto> response = handler.handleModalidadeNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Modalidade não encontrada.", response.getBody().message());
    }
    @Test
    void shouldReturnBadRequestWhenFuncionarioInformationIsInvalid()
    {
        var exception = new FuncionarioWithInvalidInformationException("Nome do funcionário não pode ser vazio.");

        var response = handler.handleFuncionarioWithInvalidInformation(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Nome do funcionário não pode ser vazio.", response.getBody().message());
    }

    @Test
    void shouldReturnBadRequestWhenModalidadeInformationIsInvalid()
    {
        var exception = new ModalidadeWithInvalidInformationException("Nome da modalidade não pode ser vazio.");

        var response = handler.handleUserWithNullInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome da modalidade não pode ser vazio.", response.getBody().message());
    }

    @Test
    void shouldReturnBadRequestWhenCargoInformationIsInvalid()
    {
        var exception = new CargoWithInvalidInformationException("Nome do cargo não pode ser nulo ou vazio.");

        var response = handler.handleCargoWithInvalidInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome do cargo não pode ser nulo ou vazio.", response.getBody().message());
    }

    @Test
    void shouldNotFoundRequestWhenCargoNotFound()
    {
        var exception = new CargoNotFoundException("Cargo não encontrado.");

        var response = handler.handleCargoNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cargo não encontrado.", response.getBody().message());
    }

    @Test
    void shouldReturnNotFoundWhenFuncionarioDoesNotExist()
    {
        var exception = new FuncionarioNotFoundException("Funcionário não encontrado.");

        var response = handler.handleFuncionarioNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Funcionário não encontrado.", response.getBody().message());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist()
    {
        UserNotFoundException exception = new UserNotFoundException("Usuário não encontrado.");

        ResponseEntity<ErrorResponseDto> response = handler.handleUserNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuário não encontrado.", response.getBody().message());
    }

    @Test
    void shouldReturnBadRequestWhenEnderecoWithInvalidInformation()
    {
        EnderecoWithInvalidInformationException exception = new EnderecoWithInvalidInformationException("Endereço com campos inválidos.");

        ResponseEntity<ErrorResponseDto> response = handler.handleEnderecoWithInvalidInformation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Endereço com campos inválidos.",  response.getBody().message());
    }
}