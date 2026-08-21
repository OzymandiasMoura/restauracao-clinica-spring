package org.example.clinicarestauracao.Application.Exceptions;

import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
