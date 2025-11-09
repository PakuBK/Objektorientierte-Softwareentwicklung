package bank;

import java.util.Objects;

/**
 * Repräsentiert eine Zahlungstransaktion.
 *
 * Bei einem Payment können sowohl ein- als auch ausgehende Zinsen anfallen.
 * Der Nettobetrag einer Transaktion kann über die Methode {@link #calculate()} berechnet werden.
 *
 * Bei positiver {@code amount} (Einzahlung) wird der Betrag um den incomingInterest reduziert.
 * Bei negativer {@code amount} (Auszahlung) wird der Betrag um den outgoingInterest erhöht.
 */
public class Payment extends Transaction implements CalculateBill {

    /**
     * Prozentsatz der Zinsen, die bei einer Einzahlung abgezogen werden.
     * Werte müssen im Bereich von 0 bis 1 (entspricht 0–100 %) liegen.
     */
    private double incomingInterest;

    /**
     * Prozentsatz der Zinsen, die bei einer Auszahlung hinzugerechnet werden.
     * Werte müssen im Bereich von 0 bis 1 (entspricht 0–100 %) liegen.
     */
    private double outgoingInterest;

    /**
     * Gibt den Zinsprozentsatz zurück, der bei einer Einzahlung abgezogen wird.
     *
     * @return incoming interest (0–1)
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Setzt den Zinsprozentsatz, der bei einer Einzahlung abgezogen wird.
     * Der Wert muss zwischen 0 und 1 liegen. Ungültige Eingaben werden ignoriert.
     *
     * @param incomingInterest Zinswert (0–1)
     */
    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest < 0 || incomingInterest > 1) return;
        this.incomingInterest = incomingInterest;
    }

    /**
     * Gibt den Zinsprozentsatz zurück, der bei einer Auszahlung hinzugerechnet wird.
     *
     * @return outgoing interest (0–1)
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Setzt den Zinsprozentsatz, der bei einer Auszahlung hinzugerechnet wird.
     * Der Wert muss zwischen 0 und 1 liegen. Ungültige Eingaben werden ignoriert.
     *
     * @param outgoingInterest Zinswert (0–1)
     */
    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest < 0 || outgoingInterest > 1) return;
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * Erstellt ein neues Payment-Objekt.
     *
     * @param date             Datum der Transaktion
     * @param amount           Betrag der Transaktion (positiv = Einzahlung, negativ = Auszahlung)
     * @param description      Beschreibung der Transaktion
     * @param incomingInterest Zins bei Einzahlung (0–1)
     * @param outgoingInterest Zins bei Auszahlung (0–1)
     */
    public Payment(String date, double amount, String description,
                   double incomingInterest, double outgoingInterest) {
        super(date, amount, description);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * Copy-Konstruktor.
     * Erstellt eine identische Kopie eines bestehenden Payment-Objekts.
     *
     * @param other zu kopierendes Payment-Objekt
     */
    public Payment(Payment other) {
        this(other.date, other.amount, other.description,
                other.incomingInterest, other.outgoingInterest);
    }

    /**
     * Berechnet den Transaktionswert unter Berücksichtigung der Zinsen.
     * <ul>
     *     <li>Einzahlung (amount > 0): amount − incomingInterest%</li>
     *     <li>Auszahlung (amount < 0): amount + outgoingInterest%</li>
     * </ul>
     *
     * @return berechneter Betrag
     */
    @Override
    public double calculate() {
        if (this.amount < 0) { // Auszahlung
            return this.amount + this.amount * this.outgoingInterest;
        } else { // Einzahlung
            return this.amount - this.amount * this.incomingInterest;
        }
    }

    /**
     * Liefert eine String-Repräsentation der Zahlung inklusive berechnetem Betrag und Zinsdaten.
     *
     * @return Beschreibung der Transaktion
     */
    @Override
    public String toString() {
        return super.toString()
                + "\n --> Payment[" + "Incoming Interest: " + this.incomingInterest
                + "; Outgoing Interest: " + this.outgoingInterest + "]";
    }

    /**
     * Vergleicht das Payment-Objekt mit einem anderen Objekt auf Gleichheit.
     *
     * Zwei Payment-Objekte gelten als gleich, wenn alle geerbten Attribute
     * (date, amount, description) sowie incomingInterest und outgoingInterest gleich sind.
     *
     * @param o zu vergleichendes Objekt
     * @return true, wenn Objekte logisch gleich sind; sonst false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment that = (Payment) o;
        return super.equals(o)
                && Double.compare(this.incomingInterest, that.incomingInterest) == 0
                && Double.compare(this.outgoingInterest, that.outgoingInterest) == 0;
    }
}
