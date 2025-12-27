package com.example.foodbe.exception_handler;

import com.example.foodbe.exception_handler.exception.InvalidDateRangeException;
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
//      private final Validator validator;


    // ==================== NotFoundException (404) ====================
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.error("NotFoundException: {}", ex.getMessage());
        int status = HttpStatus.NOT_FOUND.value();
        ApiError error = new ApiError(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.NOT_FOUND);
    }

    // ==================== ConflictException (409) ====================
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleConflictException(ConflictException ex, HttpServletRequest request) {
        log.error("ConflictException: {}", ex.getMessage());
        int status = HttpStatus.CONFLICT.value();
        ApiError error = new ApiError(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.CONFLICT);

    }

    // ==================== Validation errors (400) ====================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            Locale locale) {

        // Map để giữ 1 lỗi ưu tiên mỗi field
        Map<String, FieldError> fieldErrorMap = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            String[] codes = error.getCodes();

            // Lấy mã lỗi ưu tiên từ các mã lỗi
            String priorityCode = getPriorityErrorCode(codes);

            // Nếu field đã có lỗi, cần kiểm tra xem lỗi hiện tại có ưu tiên cao hơn không
            if (fieldErrorMap.containsKey(field)) {
                FieldError existingError = fieldErrorMap.get(field);
                String existingPriorityCode = getPriorityErrorCode(existingError.getCodes());

                // So sánh độ ưu tiên của lỗi hiện tại và lỗi đã có
                if (comparePriority(priorityCode, existingPriorityCode) < 0) {
                    // Nếu lỗi hiện tại có độ ưu tiên cao hơn, thay thế lỗi cũ
                    fieldErrorMap.put(field, error);
                }
            } else {
                // Nếu chưa có lỗi cho field này, thì thêm lỗi vào map
                fieldErrorMap.put(field, error);
            }
        }

        // Chuyển FieldError thành ApiSubError với thông điệp i18n
        List<ApiSubError> fieldErrors = fieldErrorMap.values().stream()
                .map(error -> {
                    String key = error.getDefaultMessage().replaceAll("^\\{|}$", "");
//                    String code = getPriorityErrorCode(error.getCodes());
//                    String message = messageSource.getMessage(
//                           key,
//                                error.getArguments(),
//                            locale
//                    );
                    String message = error.getDefaultMessage();
                    return new ApiSubError(error.getField(), message);
                })
                .collect(Collectors.toList());

        // Xử lý lỗi toàn cục
        List<ApiSubError> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(error -> new ApiSubError(error.getObjectName(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        // Gộp tất cả lỗi
        List<ApiSubError> allErrors = new ArrayList<>();
        allErrors.addAll(fieldErrors);
        allErrors.addAll(globalErrors);

        // Tạo ApiError
        int status = HttpStatus.BAD_REQUEST.value();
        ApiError apiError = new ApiError(
                status,
                ConstantUtils.Error.VALIDATE_ERROR_MSG,
                request.getRequestURI(),
                allErrors
        );

        // Trả về response với mã lỗi và thông điệp lỗi
        return new ResponseEntity<>(
                ApiResponse.error(status, apiError, ConstantUtils.Error.VALIDATE_ERROR_MSG),
                HttpStatus.BAD_REQUEST
        );
    }

    // Phương thức so sánh độ ưu tiên của các mã lỗi
    private int comparePriority(String code1, String code2) {
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("NotBlank", 1);
        priorityMap.put("NotNull", 2);
        priorityMap.put("Size", 3);
        priorityMap.put("Pattern", 4);
        priorityMap.put("Max", 5);
        priorityMap.put("Min", 6);

        // Lấy độ ưu tiên của hai mã lỗi
        Integer priority1 = priorityMap.get(code1);
        Integer priority2 = priorityMap.get(code2);

        // Nếu mã lỗi không có trong map, giả sử độ ưu tiên là cao nhất
        if (priority1 == null) priority1 = Integer.MAX_VALUE;
        if (priority2 == null) priority2 = Integer.MAX_VALUE;

        // So sánh độ ưu tiên
        return Integer.compare(priority1, priority2);
    }


    // Phương thức trả về mã lỗi ưu tiên nhất
    private String getPriorityErrorCode(String[] codes) {
        // Danh sách các mã lỗi theo thứ tự ưu tiên (từ quan trọng nhất đến ít quan trọng)
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("NotBlank",1);
        priorityMap.put("NotNull", 2);
        priorityMap.put("Size", 3);
        priorityMap.put("Pattern", 4);
        priorityMap.put("Max", 5);
        priorityMap.put("Min", 6);

        // Tìm lỗi ưu tiên cao nhất
        for (String code : codes) {
            Integer priority = priorityMap.get(code);
            if (priority != null) {
                return code;  // Trả về mã lỗi có độ ưu tiên cao nhất
            }
        }

        // Nếu không tìm thấy, trả về lỗi đầu tiên
        return codes.length > 0 ? codes[0] : "DefaultError";
    }

    // ==================== AccessDeniedException (403) ====================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", ex.getMessage());
        int status = HttpStatus.FORBIDDEN.value();
        ApiError error = new ApiError(status, "Bạn không có quyền truy cập", request.getRequestURI());
        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.FORBIDDEN);

    }

    // ==================== Generic Exception (500) ====================
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<ApiError>> handleGenericException(Exception ex, HttpServletRequest request) {
//        log.error("Unhandled exception: ", ex);
//        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
//        ApiError error = new ApiError(status, "Có lỗi xảy ra, vui lòng thử lại sau", request.getRequestURI());
//        return new ResponseEntity<>(ApiResponse.error(status,error,ex.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
//
//    }
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
//=================================================
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiResponse<ApiError>> handlerDateRangeError(Exception ex, HttpServletRequest request){

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ConstantUtils.ExceptionMessage.DATERANGE,
                request.getRequestURI()
        );
        return  new ResponseEntity<>(ApiResponse.error(400,error,ConstantUtils.ExceptionMessage.DATERANGE), HttpStatus.BAD_REQUEST);
    }



    //============token exception=================
}
