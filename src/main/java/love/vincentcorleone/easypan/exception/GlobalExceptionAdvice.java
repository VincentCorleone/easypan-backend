package love.vincentcorleone.easypan.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({RuntimeException.class})
    public ResponseResult<Object> handleException(HttpServletRequest request, Exception e){
        return ResponseResult.fail(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseResult<Object> handleException(HttpServletRequest request, ConstraintViolationException e){
        Map<String,String> result = new HashMap<>();
        result.put("message",((ConstraintViolation<?>)e.getConstraintViolations().toArray()[0]).getMessage());
        return ResponseResult.fail(e.getMessage());
    }
}
