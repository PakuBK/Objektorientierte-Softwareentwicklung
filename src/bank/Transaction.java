package bank;

import bank.exceptions.TransactionAttributeException;

import java.util.Objects;

/**
 * Abstract base class representing a financial transaction.
 * <p>
 * A {@code Transaction} has a date, an amount, and a description.
 * Subclasses must implement the {@link #calculate()} method to determine the
 * effective amount of the transaction, possibly including fees, interest, or
 * other adjustments.
 * </p>
 */
public abstract class Transaction implements CalculateBill {
    /**
     * The date when the transaction occurred.
     */
    protected String date;

    /**
     * The nominal amount of the transaction.
     */
    protected double amount;

    /**
     * Additional information about the transaction.
     */
    protected String description;

    /**
     * Returns the date of the transaction.
     *
     * @return the date as a String
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the transaction.
     *
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns the nominal amount of the transaction.
     *
     * @return the transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     * <p>
     * Subclasses may override this method to enforce additional validation rules.
     * </p>
     *
     * @param amount the transaction amount
     * @throws TransactionAttributeException if the amount is invalid according to subclass rules
     */
    public void setAmount(double amount) throws TransactionAttributeException {
        this.amount = amount;
    }

    /**
     * Returns the description of the transaction.
     *
     * @return the description as a String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the transaction.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Constructs a new {@code Transaction} with the given date, amount, and description.
     *
     * @param date the date of the transaction
     * @param amount the nominal amount of the transaction
     * @param description additional information about the transaction
     * @throws TransactionAttributeException if the amount is invalid
     */
    public Transaction(String date, double amount, String description) throws TransactionAttributeException {
        setAmount(amount);
        setDate(date);
        setDescription(description);
    }

    /**
     * Returns a string representation of the transaction, including date, amount, and description.
     *
     * @return a string describing the transaction
     */
    @Override
    public String toString() {
        return "Transaction [" + "date='" + date + "; "  + "amount=" + amount + "; " +
                "description=" + description + "]";
    }

    /**
     * Compares this transaction with another object for equality.
     * Two transactions are considered equal if they have the same date, amount, and description.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // same instance
        if (o == null || getClass() != o.getClass()) return false; // null or different class
        Transaction that = (Transaction) o; // cast
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(date, that.date) &&
                Objects.equals(description, that.description);
    }
}
