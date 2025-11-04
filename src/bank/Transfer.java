package bank;


import java.util.Objects;

public class Transfer extends Transaction implements CalculateBill {
    private String sender;
    private String recipient;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public void setAmount(double amount) {
        if (amount < 0) {
            System.out.printf("[Transfer] Amount (%f) can't be negative.", amount);
            return; // todo throw error
        }
        this.amount = amount;
    }

    public Transfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description);
        setRecipient(recipient);
        setSender(sender);
    }

    public Transfer(Transfer other) {
        this(other.date, other.amount, other.description, other.sender, other.recipient);
    }

    @Override
    public double calculate() {
        return amount;
    }

    @Override
    public String toString() {
        return super.toString() + "\n --> Transfer[" + "Sender: " + this.sender + "; "
                + "Recipient: " + this.recipient + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer that = (Transfer) o;
        return super.equals(o) && Objects.equals(this.sender, that.sender) &&
                Objects.equals(this.recipient, that.recipient);
    }
}
