package pl.kurs.personinformation.exceptions;

public class PositionNotBelongToEmployeeException extends RuntimeException {

    public PositionNotBelongToEmployeeException(String message) {
        super(message);
    }

}
