package pl.kurs.personinformation.exceptions;

public class DictionaryNotFoundException extends RuntimeException {

    public DictionaryNotFoundException(String message) {
        super(message);
    }

}
