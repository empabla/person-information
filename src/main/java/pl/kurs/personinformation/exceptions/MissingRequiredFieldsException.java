package pl.kurs.personinformation.exceptions;

public class MissingRequiredFieldsException extends RuntimeException {

    public MissingRequiredFieldsException(String message) {
        super(message);
    }
}
