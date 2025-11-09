package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Represents a payment transaction, which can either be a deposit (incoming) or withdrawal (outgoing).
 * <p>
 * The {@code Payment} class extends {@link Transaction} and adds the concept of incoming and outgoing interest rates,
 * which are applied when calculating the final amount of the transaction.
 * </p>
 * <p>
 * Incoming interest is subtracted from deposits, and outgoing interest is added to withdrawals.
 * </p>
 */
public class Payment extends Transaction {
    /**
     * The {@link InterestController} that stores the incoming and outgoing interest rates for this payment.
     */
    private InterestController interestController;

    /**
     * Returns the incoming interest rate for this payment.
     *
     * @return the incoming interest rate (0 <= rate <= 1)
     */
    public double getIncomingInterest() {
        return interestController.getIncomingInterest();
    }

    /**
     * Returns the outgoing interest rate for this payment.
     *
     * @return the outgoing interest rate (0 <= rate <= 1)
     */
    public double getOutgoingInterest() {
        return interestController.getOutgoingInterest();
    }

    /**
     * Sets the incoming interest rate for this payment.
     *
     * @param value the new incoming interest rate (0 <= value <= 1)
     */
    public void setIncomingInterest(double value) {
        interestController.setIncomingInterest(value);
    }

    /**
     * Sets the outgoing interest rate for this payment.
     *
     * @param value the new outgoing interest rate (0 <= value <= 1)
     */
    public void setOutgoingInterest(double value) {
        interestController.setOutgoingInterest(value);
    }

    /**
     * Constructs a new {@code Payment} instance with the specified date, amount, description,
     * incoming interest, and outgoing interest.
     *
     * @param date the date of the transaction
     * @param amount the amount of the transaction
     * @param description a description or note for the transaction
     * @param incomingInterest the interest rate applied for deposits (0 <= rate <= 1)
     * @param outgoingInterest the interest rate applied for withdrawals (0 <= rate <= 1)
     * @throws TransactionAttributeException if the amount is invalid
     */
    public Payment(String date, double amount, String description, double incomingInterest, double outgoingInterest) throws TransactionAttributeException {
        super(date, amount, description);
        interestController = new InterestController();
        try {
            interestController.setIncomingInterest(incomingInterest);
            interestController.setOutgoingInterest(outgoingInterest);
        } catch (IllegalArgumentException e) {
            throw new TransactionAttributeException("Invalid interest for Payment: " + e.getMessage());
        }
    }

    /**
     * Copy constructor for creating a new {@code Payment} instance based on another {@code Payment}.
     *
     * @param other the {@code Payment} instance to copy
     * @throws TransactionAttributeException if the amount of the copied payment is invalid
     */
    public Payment(Payment other) throws TransactionAttributeException {
        this(other.date, other.amount, other.description, other.getIncomingInterest(), other.getOutgoingInterest());
    }

    /**
     * Calculates the final amount of this payment, taking into account incoming or outgoing interest.
     *
     * @return the calculated transaction amount
     */
    @Override
    public double calculate() {
        if (this.amount < 0) { // withdrawal
            return this.amount + this.amount * this.getOutgoingInterest();
        } else { // deposit
            return this.amount - this.amount * this.getIncomingInterest();
        }
    }

    /**
     * Returns a string representation of this payment, including the date, amount, description,
     * and the applied interest rates.
     *
     * @return a string representation of this payment
     */
    @Override
    public String toString() {
        return super.toString() + "\n --> Payment[" + "Incoming Interest: " + this.getIncomingInterest()
                + "; " + "Outgoing Interest: " + this.getOutgoingInterest() + ";" + "Calculated:" + this.calculate() + "]";
    }

    /**
     * Compares this payment to another object for equality.
     * Two payments are equal if they have the same date, amount, description, and interest rates.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment that = (Payment) o;
        return super.equals(o) &&
                Double.compare(this.getIncomingInterest(), that.getIncomingInterest()) == 0 &&
                Double.compare(this.getOutgoingInterest(), that.getOutgoingInterest()) == 0;
    }
}
