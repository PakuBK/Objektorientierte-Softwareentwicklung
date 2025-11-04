package bank;

import java.util.Objects;

public class Payment extends Transaction implements CalculateBill{
    private double incomingInterest;
    private double outgoingInterest;

    public double getIncomingInterest() {
        return incomingInterest;
    }

    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest < 0 || 1 < incomingInterest) return;
        this.incomingInterest = incomingInterest;
    }

    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest < 0 || 1 < outgoingInterest) return;
        this.outgoingInterest = outgoingInterest;
    }

    public Payment(String date, double amount, String description, double incomingInterest, double outgoingInterest) {
        super(date, amount, description);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    // copy constructor
    public Payment(Payment other) {
        this(other.date, other.amount, other.description, other.incomingInterest, other.outgoingInterest);
    }


    @Override
    public double calculate() {
        if (this.amount < 0) { // auszahlung
            return this.amount + this.amount * this.outgoingInterest;
        }
        else { // einzahlung
            return this.amount - this.amount * this.incomingInterest;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "\n --> Payment[" + "Incoming Interest: " + this.incomingInterest
                + "; " + "Outgoing Interest: " + this.outgoingInterest + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment that = (Payment) o;
        return super.equals(o) && Double.compare(this.incomingInterest, that.incomingInterest) == 0 &&
                Double.compare(this.outgoingInterest, that.outgoingInterest) == 0;
    }
}
