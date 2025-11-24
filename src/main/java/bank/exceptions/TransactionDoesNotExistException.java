package bank.exceptions;

public class TransactionDoesNotExistException extends Exception {
    public TransactionDoesNotExistException() {
        super();
    }

    public TransactionDoesNotExistException(String message) {
        super(message);
    }
}
