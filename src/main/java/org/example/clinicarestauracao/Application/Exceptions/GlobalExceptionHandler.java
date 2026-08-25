package org.example.clinicarestauracao.Application.Exceptions;

import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ErrorResponseDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.FieldErrorDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ValidationErrorResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;
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

    @ExceptionHandler(MedicamentoNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMedicamentoNotFound(MedicamentoNotFoundException ex)
    {
        var response = new ErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MedicamentoWithInvalidInformationException.class)
    public ResponseEntity<ErrorResponseDto> handleMedicamentoWithInvalidInformation(MedicamentoWithInvalidInformationException ex)
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
}