package bank.exceptions;

public class AccountDoesNotExistException extends Exception {
    public AccountDoesNotExistException() {
        super();
    }

    public AccountDoesNotExistException(String message) {
        super(message);
    }
}
