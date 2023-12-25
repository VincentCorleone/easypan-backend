package love.vincentcorleone.easypan.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({RuntimeException.class})
    public ResponseResult<Object> handleException(HttpServletRequest request, Exception e){
        return ResponseResult.fail(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseResult<Object> handleException(HttpServletRequest request, ConstraintViolationException e){
        return ResponseResult.fail(e.getMessage());
    }
}
