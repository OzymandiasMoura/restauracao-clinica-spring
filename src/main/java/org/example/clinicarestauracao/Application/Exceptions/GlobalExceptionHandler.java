package org.example.clinicarestauracao.Application.Exceptions;

import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ErrorResponseDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.FieldErrorDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ValidationErrorResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Endereco.EnderecoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex)
    {
        var response = new ErrorResponseDto("Usuário ou senha inválidos.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UsernameAlredyInUseException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameAlreadyInUse(UsernameAlredyInUseException exception)
    {
        var response = new ErrorResponseDto(exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleUserWithNullInformation(UserWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex)
    {
        List<FieldErrorDto> errosDeCampo = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new FieldErrorDto(erro.getField(), erro.getDefaultMessage()))
                .toList();

        var response = new ValidationErrorResponseDto("Falha na validação dos dados.", errosDeCampo);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConvenioWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleConvenioWithInvalidInformation(ConvenioWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConvenioNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleConvenioNotFound(ConvenioNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CargoWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto>  handleCargoWithInvalidInformation(CargoWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CargoNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCargoNotFound(CargoNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FuncionarioWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleFuncionarioWithInvalidInformation(FuncionarioWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FuncionarioNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleFuncionarioNotFound(FuncionarioNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //Endereço

    @ExceptionHandler(EnderecoWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleEnderecoWithInvalidInformation(EnderecoWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TriagemNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTriagemNotFound(TriagemNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TriagemWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleTriagemWithInvalidInformation(TriagemWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    //Acolhido
    @ExceptionHandler(AcolhidoWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleAcolhidoWithInvalidInformation(AcolhidoWithInvalidInformationException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AcolhidoNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAcolhidoNotFound(AcolhidoNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
