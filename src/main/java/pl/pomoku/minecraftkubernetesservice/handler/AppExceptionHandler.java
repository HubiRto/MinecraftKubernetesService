package pl.pomoku.minecraftkubernetesservice.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.pomoku.minecraftkubernetesservice.dto.response.ErrorResponse;
import pl.pomoku.minecraftkubernetesservice.exception.AppException;

@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(value = {AppException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleExceptions(AppException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(ex.getMessage()));
    }
}
