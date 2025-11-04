package bank;

import java.util.Objects;

public abstract class Transaction {
    protected String date; // date the payment was made
    protected double amount; // the amount of the payment
    protected String description; // additional information about the payment

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction(String date, double amount, String description) {
        setAmount(amount);
        setDate(date);
        setDescription(description);
    }

    @Override
    public String toString() {
        return "Transaction [" + "date='" + date + "; "  + "amount=" + amount + "; " +
                "description=" + description + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // same instance
        if (o == null || getClass() != o.getClass()) return false; // null or different class
        Transaction that = (Transaction) o; // cast
        return Double.compare(that.amount, amount) == 0 && Objects.equals(date, that.date) && Objects.equals(description, that.description);
    }

}
