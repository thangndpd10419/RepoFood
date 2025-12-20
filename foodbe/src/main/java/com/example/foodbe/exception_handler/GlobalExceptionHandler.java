package com.example.foodbe.exception_handler;

import com.example.foodbe.exception_handler.exception.SystemErrorException;
import com.example.foodbe.payload.ApiError;
import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.payload.ApiSubError;
import com.example.foodbe.utils.ConstantUtils;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.error("NotFoundException: {}", ex.getMessage());
        int status = HttpStatus.NOT_FOUND.value();
        ApiError error = new ApiError(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleConflictException(ConflictException ex, HttpServletRequest request) {
        log.error("ConflictException: {}", ex.getMessage());
        int status = HttpStatus.CONFLICT.value();
        ApiError error = new ApiError(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.CONFLICT);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            Locale locale) {

        Map<String, FieldError> fieldErrorMap = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            String[] codes = error.getCodes();

            String priorityCode = getPriorityErrorCode(codes);

            if (fieldErrorMap.containsKey(field)) {
                FieldError existingError = fieldErrorMap.get(field);
                String existingPriorityCode = getPriorityErrorCode(existingError.getCodes());

                if (comparePriority(priorityCode, existingPriorityCode) < 0) {

                    fieldErrorMap.put(field, error);
                }
            } else {

                fieldErrorMap.put(field, error);
            }
        }

        List<ApiSubError> fieldErrors = fieldErrorMap.values().stream()
                .map(error -> {
                    String key = error.getDefaultMessage().replaceAll("^\\{|}$", "");

                    String message = error.getDefaultMessage();
                    return new ApiSubError(error.getField(), message);
                })
                .collect(Collectors.toList());

        List<ApiSubError> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(error -> new ApiSubError(error.getObjectName(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        List<ApiSubError> allErrors = new ArrayList<>();
        allErrors.addAll(fieldErrors);
        allErrors.addAll(globalErrors);

        int status = HttpStatus.BAD_REQUEST.value();
        ApiError apiError = new ApiError(
                status,
                ConstantUtils.Error.VALIDATE_ERROR_MSG,
                request.getRequestURI(),
                allErrors
        );

        return new ResponseEntity<>(
                ApiResponse.error(status, apiError, ConstantUtils.Error.VALIDATE_ERROR_MSG),
                HttpStatus.BAD_REQUEST
        );
    }

    private int comparePriority(String code1, String code2) {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("NotBlank", 1);
        priorityMap.put("NotNull", 2);
        priorityMap.put("Size", 3);
        priorityMap.put("Pattern", 4);
        priorityMap.put("Max", 5);
        priorityMap.put("Min", 6);

        Integer priority1 = priorityMap.get(code1);
        Integer priority2 = priorityMap.get(code2);

        if (priority1 == null) priority1 = Integer.MAX_VALUE;
        if (priority2 == null) priority2 = Integer.MAX_VALUE;

        return Integer.compare(priority1, priority2);
    }

    private String getPriorityErrorCode(String[] codes) {

        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("NotBlank",1);
        priorityMap.put("NotNull", 2);
        priorityMap.put("Size", 3);
        priorityMap.put("Pattern", 4);
        priorityMap.put("Max", 5);
        priorityMap.put("Min", 6);

        for (String code : codes) {
            Integer priority = priorityMap.get(code);
            if (priority != null) {
                return code;
            }
        }

        return codes.length > 0 ? codes[0] : "DefaultError";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", ex.getMessage());
        int status = HttpStatus.FORBIDDEN.value();
        ApiError error = new ApiError(status, "Bạn không có quyền truy cập", request.getRequestURI());
        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.FORBIDDEN);

    }

        @ExceptionHandler(SystemErrorException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleSystemError(Exception ex, HttpServletRequest request) {
        log.error("Lỗi hệ thống >>>>>>>>>>>>>>>>>>>: ", ex);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ApiError error = new ApiError(
                status,
                ConstantUtils.ExceptionMessage.SYSTEM_ERROR,
                request.getRequestURI()
        );
        return new ResponseEntity<>(ApiResponse.error(status, error, ConstantUtils.ExceptionMessage.SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
