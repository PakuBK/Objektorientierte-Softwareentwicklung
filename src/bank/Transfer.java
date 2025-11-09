package bank;

import java.util.Objects;

/**
 * Repräsentiert eine Überweisungstransaktion zwischen zwei Parteien.
 *
 * Im Gegensatz zu {@link Payment} fallen bei einer Transfer-Transaktion
 * keine Ein- oder Auszahlungszinsen an. Die Methode {@link #calculate()}
 * gibt daher immer den ursprünglichen Betrag unverändert zurück.
 *
 * Der Betrag einer Überweisung muss immer positiv sein.
 */
public class Transfer extends Transaction implements CalculateBill {

    /**
     * Name oder Identifikation des Senders.
     */
    private String sender;

    /**
     * Name oder Identifikation des Empfängers.
     */
    private String recipient;

    /**
     * Gibt den Sender der Überweisung zurück.
     *
     * @return Sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Setzt den Sender der Überweisung.
     *
     * @param sender Name/ID des Senders
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gibt den Empfänger der Überweisung zurück.
     *
     * @return Empfänger
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Setzt den Empfänger der Überweisung.
     *
     * @param recipient Name/ID des Empfängers
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Setzt den Transaktionsbetrag.
     * Für Überweisungen muss der Betrag positiv sein.
     * Bei negativen Beträgen wird der Wert ignoriert und lediglich eine Warnung ausgegeben.
     *
     * @param amount positiver Transaktionsbetrag
     */
    @Override
    public void setAmount(double amount) {
        if (amount < 0) {
            System.out.printf("[Transfer] Amount (%f) can't be negative.%n", amount);
            return; // TODO: statt printf -> Exception werfen
        }
        this.amount = amount;
    }

    /**
     * Konstruktor zur Erstellung einer Überweisung.
     *
     * @param date        Datum der Transaktion
     * @param amount      positiver Transaktionsbetrag
     * @param description Beschreibung der Transaktion
     * @param sender      Name/ID des Senders
     * @param recipient   Name/ID des Empfängers
     */
    public Transfer(String date, double amount, String description, String sender, String recipient) {
        super(date, amount, description);
        setRecipient(recipient);
        setSender(sender);
    }

    /**
     * Copy-Konstruktor.
     * Erstellt eine identische Kopie eines bestehenden Transfer-Objekts.
     *
     * @param other zu kopierende Instanz
     */
    public Transfer(Transfer other) {
        this(other.date, other.amount, other.description, other.sender, other.recipient);
    }

    /**
     * Gibt den Transaktionsbetrag unverändert zurück.
     * Für Transfer-Objekte fallen keine Zinsen an.
     *
     * @return ursprünglicher Betrag
     */
    @Override
    public double calculate() {
        return amount;
    }

    /**
     * Gibt eine String-Repräsentation des Transfers zurück,
     * ergänzt um Sender- und Empfängerinformationen.
     *
     * @return formatierter String
     */
    @Override
    public String toString() {
        return super.toString() +
                "\n --> Transfer[Sender: " + this.sender +
                "; Recipient: " + this.recipient + "]";
    }

    /**
     * Vergleicht dieses Objekt mit einem anderen auf Gleichheit.
     *
     * Zwei Transfer-Objekte gelten als gleich, wenn alle Attribute
     * (einschließlich geerbter Attribute sowie {@code sender} und {@code recipient})
     * übereinstimmen.
     *
     * @param o zu vergleichendes Objekt
     * @return true, wenn logisch gleich; sonst false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer that = (Transfer) o;
        return super.equals(o) &&
                Objects.equals(this.sender, that.sender) &&
                Objects.equals(this.recipient, that.recipient);
    }
}
