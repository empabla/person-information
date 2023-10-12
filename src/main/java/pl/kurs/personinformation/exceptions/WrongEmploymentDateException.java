package pl.kurs.personinformation.exceptions;

public class WrongEmploymentDateException extends RuntimeException {

    public WrongEmploymentDateException(String message) {
        super(message);
    }

}
