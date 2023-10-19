package pl.kurs.personinformation.exceptions.handling;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.personinformation.exceptions.UpdateOptimisticLockingException;
import pl.kurs.personinformation.exceptions.constraints.ConstraintErrorHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
@Profile("test")
public class TestConstraintExceptionHandler {

    private Map<String, ConstraintErrorHandler> constraintErrorMapper;

    public TestConstraintExceptionHandler(Set<ConstraintErrorHandler> handlers) {
        this.constraintErrorMapper = handlers.stream()
                .collect(Collectors.toMap(ConstraintErrorHandler::getConstraintName, Function.identity()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseBody> handleConstraintViolationException(ConstraintViolationException e) {
        String constraintName = e.getConstraintName().substring(7);
        ExceptionResponseBody body = constraintErrorMapper.get(constraintName).mapToErrorDto();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UpdateOptimisticLockingException.class)
    public ResponseEntity<ExceptionResponseBody> handleUpdateOptimisticLockingException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "CONFLICT",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }



}
