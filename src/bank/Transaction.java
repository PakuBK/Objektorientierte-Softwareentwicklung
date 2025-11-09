package bank;

import java.util.Objects;

/**
 * Abstrakte Oberklasse für alle Transaktionsarten.
 *
 * Eine Transaktion besitzt ein Datum, einen Betrag sowie eine Beschreibung.
 * Sie dient als Basis für spezialisierte Transaktionstypen wie
 * {@link Payment} oder {@link Transfer}.
 */
public abstract class Transaction {

    /**
     * Datum der Transaktion, z. B. im Format "YYYY-MM-DD".
     */
    protected String date;

    /**
     * Betrag der Transaktion.
     * Positive Werte stehen für Einzahlungen, negative für Auszahlungen.
     */
    protected double amount;

    /**
     * Beschreibung oder zusätzliche Informationen zur Transaktion.
     */
    protected String description;

    /**
     * Gibt das Transaktionsdatum zurück.
     *
     * @return Datum der Transaktion
     */
    public String getDate() {
        return date;
    }

    /**
     * Setzt das Datum der Transaktion.
     *
     * @param date neues Datum
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gibt den Transaktionsbetrag zurück.
     *
     * @return Betrag (positiv = Einzahlung, negativ = Auszahlung)
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Setzt den Transaktionsbetrag.
     *
     * @param amount Betrag der Transaktion (positiv oder negativ)
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gibt die Beschreibung der Transaktion zurück.
     *
     * @return Beschreibungstext
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setzt die Beschreibung der Transaktion.
     *
     * @param description neue Beschreibung
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Konstruktor zur Erstellung einer Transaktion.
     *
     * @param date        Datum der Transaktion
     * @param amount      Betrag (positiv = Einzahlung, negativ = Auszahlung)
     * @param description Beschreibung der Transaktion
     */
    public Transaction(String date, double amount, String description) {
        setAmount(amount);
        setDate(date);
        setDescription(description);
    }

    /**
     * Gibt eine Textdarstellung der Transaktion zurück.
     *
     * @return formatierter String mit den Attributen
     */
    @Override
    public String toString() {
        return "Transaction [" +
                "date='" + date + "; " +
                "amount=" + amount + "; " +
                "description=" + description + "]";
    }

    /**
     * Vergleicht diese Transaktion mit einem anderen Objekt auf Gleichheit.
     *
     * Zwei Transaktionen gelten als gleich, wenn
     * {@code date}, {@code amount} und {@code description} übereinstimmen.
     *
     * @param o zu vergleichendes Objekt
     * @return true, wenn die Objekte gleich sind; sonst false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                 // gleiche Instanz
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0
                && Objects.equals(date, that.date)
                && Objects.equals(description, that.description);
    }
}
