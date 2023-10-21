package pl.kurs.personinformation.exceptions.handling;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.personinformation.exceptions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UpdateOptimisticLockingException.class)
    public ResponseEntity<ExceptionResponseBody> handleUpdateOptimisticLockingException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "CONFLICT",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MissingRequiredFieldsException.class)
    public ResponseEntity<ExceptionResponseBody> handleMissingRequiredFieldsException(MissingRequiredFieldsException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseBody> handleJakartaConstraintViolationException
            (jakarta.validation.ConstraintViolationException e) {
        List<String> fieldErrorsMessages = e.getConstraintViolations()
                .stream()
                .map(violation -> "property: " + violation.getPropertyPath() + " / invalid value: '"
                        + violation.getInvalidValue() + "' / message: " + violation.getMessage())
                .collect(Collectors.toList());
        ExceptionResponseBody body = new ExceptionResponseBody(
                fieldErrorsMessages,
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({WrongEntityException.class, WrongIdException.class, WrongTypeException.class,
            DictionaryAlreadyExists.class, DictionaryValueAlreadyExistsException.class})
    public ResponseEntity<ExceptionResponseBody> handleEntityException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({WrongEmploymentDateException.class, PositionNotBelongToEmployeeException.class})
    public ResponseEntity<ExceptionResponseBody> handleEmployeePositionExceptions(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({EntityNotFoundException.class, DictionaryValueNotFoundException.class,
            DictionaryNotFoundException.class, PersonNotFoundException.class})
    public ResponseEntity<ExceptionResponseBody> handleNotFoundException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "NOT_FOUND",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DataImportFromFileException.class)
    public ResponseEntity<ExceptionResponseBody> handleDataImportFromFileException(DataImportFromFileException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> fieldErrorsMessages = e.getFieldErrors()
                .stream()
                .map(fe -> "field: " + fe.getField() + " / rejectedValue: '" + fe.getRejectedValue() +
                        "' / message: " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        ExceptionResponseBody body = new ExceptionResponseBody(
                fieldErrorsMessages,
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ExceptionResponseBody> handleInvalidFormatException(InvalidFormatException e) {
        String fieldName = e.getPath().get(0).getFieldName();
        String rejectedValue = e.getValue().toString();
        String errorMessage = e.getOriginalMessage();
        List<String> errorMessages = List.of("field: " + fieldName + " / rejectedValue: '" + rejectedValue +
                "' / message: " + errorMessage);
        ExceptionResponseBody body = new ExceptionResponseBody(
                errorMessages,
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponseBody> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
