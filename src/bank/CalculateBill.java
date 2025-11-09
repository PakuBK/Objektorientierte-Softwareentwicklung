package bank;

/**
 * Funktionales Interface zur Berechnung des effektiven Transaktionswertes.
 *
 * Klassen, die dieses Interface implementieren, müssen definieren,
 * wie der Nettobetrag einer Transaktion berechnet wird. Dabei darf der
 * zugrunde liegende Betrag (z. B. {@code amount} in {@link Transaction})
 * nicht verändert werden.
 *
 * Beispiele:
 * <ul>
 *     <li>{@link Payment}: Betrag wird um Zinsen korrigiert</li>
 *     <li>{@link Transfer}: Betrag bleibt unverändert</li>
 * </ul>
 */
public interface CalculateBill {

    /**
     * Berechnet den Nettobetrag der Transaktion.
     * Der ursprüngliche Transaktionsbetrag darf dabei nicht geändert werden.
     *
     * @return berechneter Nettobetrag
     */
    double calculate();
}
