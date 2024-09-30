package com.swp391.koibe.exceptions;

import com.swp391.koibe.components.LocalizationUtils;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.responses.ExceptionResponse;
import com.swp391.koibe.responses.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler {

    private final LocalizationUtils localizationUtils;

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleDataNotFoundException(DataNotFoundException e) {
        return ExceptionResponse.builder()
            .message(localizationUtils.getLocalizedMessage("exception.data_not_found"))
            .build();
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleNullPointerException(NullPointerException e) {
        return ExceptionResponse.builder()
            .message(localizationUtils.getLocalizedMessage("exception.null_pointer"))
            .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse handleException(Exception e) {
        return ExceptionResponse.builder()
            .message(localizationUtils.getLocalizedMessage("exception.internal_server_error"))
            .build();
    }

}
