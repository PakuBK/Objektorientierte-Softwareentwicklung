package bank;

import java.util.Objects;

/**
 * The {@code InterestController} class manages the incoming and outgoing interest rates for transactions.
 * It ensures that interest values are always in the valid range [0, 1].
 * <p>
 * This class is used by {@link Payment} and {@link PrivateBank} to apply interest rates
 * for deposits (incoming) and withdrawals (outgoing).
 * </p>
 */
public class InterestController {
    /**
            * Interest rate applied to incoming transactions (deposits).
            * Must be a value between 0 and 1.
            */
    private double incomingInterest;

    /**
     * Interest rate applied to outgoing transactions (withdrawals).
     * Must be a value between 0 and 1.
     */
    private double outgoingInterest;

    /**
     * Returns the current incoming interest rate.
     *
     * @return the incoming interest rate as a double in the range [0, 1]
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Sets the incoming interest rate.
     *
     * @param incomingInterest the new incoming interest rate (must be in range [0, 1])
     * @throws IllegalArgumentException if the interest rate is negative or greater than 1
     */
    public void setIncomingInterest(double incomingInterest) throws IllegalArgumentException {
        if (incomingInterest < 0 || incomingInterest > 1) throw new IllegalArgumentException("Incoming interest must be between 0 and 1");
        this.incomingInterest = incomingInterest;
    }

    /**
     * Returns the current outgoing interest rate.
     *
     * @return the outgoing interest rate as a double in the range [0, 1]
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Sets the outgoing interest rate.
     *
     * @param outgoingInterest the new outgoing interest rate (must be in range [0, 1])
     * @throws IllegalArgumentException if the interest rate is negative or greater than 1
     */
    public void setOutgoingInterest(double outgoingInterest) throws IllegalArgumentException {
        if (outgoingInterest < 0 || outgoingInterest > 1) throw new IllegalArgumentException("Outgoing interest must be between 0 and 1");
        this.outgoingInterest = outgoingInterest;
    }

    public InterestController() {}

    public InterestController(double incomingInterest, double outgoingInterest) throws IllegalArgumentException{
        this.setIncomingInterest(incomingInterest);
        this.setOutgoingInterest(outgoingInterest);
    }

    public InterestController(InterestController other) {
        this(other.getIncomingInterest(), other.getOutgoingInterest());
    }

    /**
     * Returns a string representation of the {@code InterestController} instance,
     * including the incoming and outgoing interest rates.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "InterestController{" +
                "incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                '}';
    }

    /**
     * Checks whether this {@code InterestController} is equal to another object.
     * Two instances are equal if they have the same incoming and outgoing interest rates.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestController that = (InterestController) o;
        return Double.compare(that.incomingInterest, incomingInterest) == 0 &&
                Double.compare(that.outgoingInterest, outgoingInterest) == 0;
    }

    /**
     * Returns a hash code value for the object, based on the incoming and outgoing interest rates.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(incomingInterest, outgoingInterest);
    }
}
