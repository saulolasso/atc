package com.slb.atc.mail.error;

import com.slb.atc.mail.dto.ErrorMessageDto;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest req) {
    ErrorMessageDto errorMessageDto = new ErrorMessageDto();
    errorMessageDto.setTimestamp(new Date());
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(x -> "Error in field " + x.getField() + ": " + x.getDefaultMessage())
            .collect(Collectors.toList());

    errorMessageDto.setErrors(errors);
    return new ResponseEntity<>(errorMessageDto, headers, status);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
    ErrorMessageDto errorMessageDto = new ErrorMessageDto();
    errorMessageDto.setTimestamp(new Date());
    List<String> errors = new ArrayList<>();
    errors.add(ex.getMessage());
    return new ResponseEntity<>(errorMessageDto, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Object> handleBadRequestException(
      BadRequestException ex, WebRequest request) {
    ErrorMessageDto errorMessageDto = new ErrorMessageDto();
    errorMessageDto.setTimestamp(new Date());
    List<String> errors = new ArrayList<>();
    errors.add(ex.getMessage());
    return new ResponseEntity<>(errorMessageDto, HttpStatus.BAD_REQUEST);
  }
}
