package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Represents an outgoing transfer from an account.
 * <p>
 * An {@code OutgoingTransfer} is a type of {@link Transfer} where the current account
 * is the sender. The calculated amount is always negative, as it decreases the account balance.
 * </p>
 */
public class OutgoingTransfer extends Transfer {

    /**
     * Constructs a new OutgoingTransfer with the given attributes.
     *
     * @param date        the date of the transfer
     * @param amount      the amount of money transferred (must be non-negative)
     * @param description additional information about the transfer
     * @param sender      the account sending the money
     * @param recipient   the account receiving the money
     * @throws TransactionAttributeException if the amount is invalid
     */
    public OutgoingTransfer(String date, double amount, String description, String sender, String recipient)
            throws TransactionAttributeException {
        super(date, amount, description, sender, recipient);
    }

    /**
     * Copy constructor that creates an OutgoingTransfer from an existing Transfer.
     *
     * @param other the Transfer object to copy
     * @throws TransactionAttributeException if the amount is invalid
     */
    public OutgoingTransfer(Transfer other) throws TransactionAttributeException {
        super(other);
    }

    /**
     * Calculates the value of the transfer for the account balance.
     * <p>
     * Since this is an outgoing transfer, the amount is negative.
     * </p>
     *
     * @return the negative amount of the transfer
     */
    @Override
    public double calculate() {
        return -this.amount;
    }
}
