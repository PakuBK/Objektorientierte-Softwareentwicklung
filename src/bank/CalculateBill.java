package bank;

/**
 * Interface for transactions or financial operations that can be calculated.
 * <p>
 * Any class implementing {@code CalculateBill} must provide a concrete implementation
 * of the {@link #calculate()} method, which returns the numeric value of the transaction
 * (e.g., for calculating account balances).
 * </p>
 */
public interface CalculateBill {

    /**
     * Calculates the monetary value of this transaction or financial operation.
     *
     * @return the calculated amount, which can be positive or negative depending on the transaction type
     */
    double calculate();
}
