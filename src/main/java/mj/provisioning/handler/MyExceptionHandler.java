package mj.provisioning.handler;

import lombok.extern.slf4j.Slf4j;
import mj.provisioning.common.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        log.error("CustomException {}", e);
        return ResponseEntity.status(HttpStatus.valueOf(e.getErrorCode().getStatus())).body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleCustomException(Exception e) {
        log.error("Exception {}", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
