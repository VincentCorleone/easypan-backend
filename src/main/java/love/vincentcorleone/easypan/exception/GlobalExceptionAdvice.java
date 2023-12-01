package love.vincentcorleone.easypan.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleException(HttpServletRequest request, Exception e){
        Map<String,String> result = new HashMap<>();
        result.put("message",e.getMessage());
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleException(HttpServletRequest request, ConstraintViolationException e){
        Map<String,String> result = new HashMap<>();
        result.put("message",((ConstraintViolation<?>)e.getConstraintViolations().toArray()[0]).getMessage());
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(400));
    }
}
