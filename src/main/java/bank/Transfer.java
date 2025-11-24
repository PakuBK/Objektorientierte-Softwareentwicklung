package bank;

import bank.exceptions.TransactionAttributeException;

import java.util.Objects;

/**
 * Represents a transfer transaction between two accounts.
 * <p>
 * A {@code Transfer} has a sender and a recipient. The amount is always positive, and the
 * direction of the transfer (sending or receiving) is determined by comparing the sender/recipient
 * to the account for which the balance is being calculated.
 * </p>
 * <p>
 * The {@code calculate()} method returns the amount without modification. The interpretation
 * (addition or subtraction) is handled externally, e.g., in {@link PrivateBank#getAccountBalance(String)}.
 * </p>
 */
public class Transfer extends Transaction {
    /**
     * The account sending the money.
     */
    private String sender;

    /**
     * The account receiving the money.
     */
    private String recipient;

    /**
     * Returns the sender of the transfer.
     *
     * @return the sender account name
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender of the transfer.
     *
     * @param sender the sender account name
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Returns the recipient of the transfer.
     *
     * @return the recipient account name
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient of the transfer.
     *
     * @param recipient the recipient account name
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Sets the amount of the transfer.
     * <p>
     * The amount must be positive; otherwise a {@link TransactionAttributeException} is thrown.
     * </p>
     *
     * @param amount the amount to transfer
     * @throws TransactionAttributeException if the amount is negative
     */
    @Override
    public void setAmount(double amount) throws TransactionAttributeException {
        if (amount < 0) throw new TransactionAttributeException("Transfer amount cannot be negative");
        this.amount = amount;
    }

    /**
     * Constructs a new {@code Transfer} instance with the specified date, amount, description,
     * sender, and recipient.
     *
     * @param date the date of the transfer
     * @param amount the amount of the transfer (must be >= 0)
     * @param description a description or note for the transfer
     * @param sender the sending account
     * @param recipient the receiving account
     * @throws TransactionAttributeException if the amount is invalid
     */
    public Transfer(String date, double amount, String description, String sender, String recipient) throws TransactionAttributeException{
        super(date, amount, description);
        setRecipient(recipient);
        setSender(sender);
    }

    /**
     * Copy constructor for creating a new {@code Transfer} based on another {@code Transfer}.
     *
     * @param other the {@code Transfer} to copy
     * @throws TransactionAttributeException if the amount is invalid
     */
    public Transfer(Transfer other) throws TransactionAttributeException{
        this(other.date, other.amount, other.description, other.sender, other.recipient);
    }

    /**
     * Returns the amount of the transfer.
     * <p>
     * Note: whether this amount is considered positive or negative for a specific account
     * depends on whether the account is the sender or the recipient.
     * </p>
     *
     * @return the transfer amount
     */
    @Override
    public double calculate() {
        return amount;
    }

    /**
     * Returns a string representation of the transfer, including date, amount, description, sender, and recipient.
     *
     * @return a string representation of the transfer
     */
    @Override
    public String toString() {
        return super.toString() + "\n --> Transfer[" + "Sender: " + this.sender + "; "
                + "Recipient: " + this.recipient + ";" + "Calculated:" + this.calculate() + "]";
    }

    /**
     * Compares this transfer to another object for equality.
     * Two transfers are equal if they have the same date, amount, description, sender, and recipient.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer that = (Transfer) o;
        return super.equals(o) && Objects.equals(this.sender, that.sender) &&
                Objects.equals(this.recipient, that.recipient);
    }
}
