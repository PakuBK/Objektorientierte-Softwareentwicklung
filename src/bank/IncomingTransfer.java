package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Represents an incoming transfer to an account.
 * <p>
 * An {@code IncomingTransfer} is a type of {@link Transfer} where the current account
 * is the recipient. The calculated amount is always positive, as it increases the account balance.
 * </p>
 */
public class IncomingTransfer extends Transfer {

    /**
     * Constructs a new IncomingTransfer with the given attributes.
     *
     * @param date        the date of the transfer
     * @param amount      the amount of money transferred (must be non-negative)
     * @param description additional information about the transfer
     * @param sender      the account sending the money
     * @param recipient   the account receiving the money
     * @throws TransactionAttributeException if the amount is invalid
     */
    public IncomingTransfer(String date, double amount, String description, String sender, String recipient)
            throws TransactionAttributeException {
        super(date, amount, description, sender, recipient);
    }

    /**
     * Copy constructor that creates an IncomingTransfer from an existing Transfer.
     *
     * @param other the Transfer object to copy
     * @throws TransactionAttributeException if the amount is invalid
     */
    public IncomingTransfer(Transfer other) throws TransactionAttributeException {
        super(other);
    }

    /**
     * Calculates the value of the transfer for the account balance.
     * <p>
     * Since this is an incoming transfer, the amount is positive.
     * </p>
     *
     * @return the positive amount of the transfer
     */
    @Override
    public double calculate() {
        return this.amount;
    }
}
