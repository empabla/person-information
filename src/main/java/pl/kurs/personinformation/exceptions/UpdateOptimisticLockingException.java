package pl.kurs.personinformation.exceptions;

public class UpdateOptimisticLockingException extends RuntimeException {

    public UpdateOptimisticLockingException(String message) {
        super(message);
    }

}
