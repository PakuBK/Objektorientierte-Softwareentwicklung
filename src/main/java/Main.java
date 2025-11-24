import bank.*;
import bank.exceptions.*;
public class Main {
    public static void main(String[] args) {
        try {
            // PrivateBank erstellen
            PrivateBank bank1 = new PrivateBank("Bank1", 0.05, 0.02, "accounts1");

            // Konten erstellen
            bank1.createAccount("Alice");

            // Transaktionen erstellen
            Payment p1 = new Payment("1", 1000, "Deposit", 0.05, 0.02);
            Payment p2 = new Payment("2", -200, "Withdrawal", 0.05, 0.02);

            Transfer t1 = new IncomingTransfer("3", 500, "Gift", "Bob", "Alice");
            Transfer t2 = new OutgoingTransfer("4", 300, "Payment", "Alice", "Bob");

            // Transaktionen hinzufügen
            bank1.addTransaction("Alice", p1);
            bank1.addTransaction("Alice", p2);
            bank1.addTransaction("Alice", t1);
            bank1.addTransaction("Alice", t2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
