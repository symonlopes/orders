package br.com.symon.orders.advice;

import br.com.symon.orders.error.ApiError;
import br.com.symon.orders.error.ErrorResponse;
import br.com.symon.orders.exception.DuplicatedOrderException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Log4j2
public class ExceptionAdvice {

    //Transform bean-validation error in common API format.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var response = ErrorResponse.builder().errors(new ArrayList<>()).build();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            response.errors().add( new ApiError(error.getDefaultMessage()) );
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatedOrderException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> duplicateExceptionHandler(DuplicatedOrderException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(List.of(new ApiError("Order already exists."))));
    }


}
