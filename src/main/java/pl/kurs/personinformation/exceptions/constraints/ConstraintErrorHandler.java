package pl.kurs.personinformation.exceptions.constraints;

import pl.kurs.personinformation.exceptions.handling.ExceptionResponseBody;

public interface ConstraintErrorHandler {

    ExceptionResponseBody mapToErrorDto();

    String getConstraintName();

}