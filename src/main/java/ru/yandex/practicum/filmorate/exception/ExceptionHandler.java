package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(MethodArgumentNotValidException exp) {
        log.warn(exp.getMessage(), exp);

        Map<String, String> errorMessageMap = new HashMap<>();
        exp.getBindingResult().getFieldErrors().forEach(error -> errorMessageMap.put(error.getField(), error.getDefaultMessage()));

        return errorMessageMap;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseData handle(NotFoundException exp) {
        log.warn(exp.getMessage(), exp);

        return new ErrorResponseData(exp.getMessage());
    }
}
