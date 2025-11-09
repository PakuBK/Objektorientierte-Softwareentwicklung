import bank.*;
import bank.exceptions.*;
public class Main {
    public static void main(String[] args) {
        try {
            // PrivateBank erstellen
            PrivateBank bank1 = new PrivateBank("Bank1", 0.05, 0.02);
            PrivateBank bank2 = new PrivateBank("Bank2", 0.03, 0.01);

            // Konten erstellen
            bank1.createAccount("Alice");
            bank2.createAccount("Bob");

            // Transaktionen erstellen
            Payment p1 = new Payment("2025-11-08", 1000, "Deposit", 0.05, 0.02);
            Payment p2 = new Payment("2025-11-08", -200, "Withdrawal", 0.05, 0.02);

            Transfer t1 = new IncomingTransfer("2025-11-08", 500, "Gift", "Bob", "Alice");
            Transfer t2 = new OutgoingTransfer("2025-11-08", 300, "Payment", "Alice", "Bob");

            // Transaktionen hinzufügen
            bank1.addTransaction("Alice", p1);
            bank1.addTransaction("Alice", p2);
            bank1.addTransaction("Alice", t1);
            bank1.addTransaction("Alice", t2);

            // Methoden testen
            System.out.println("Kontostand Alice: " + bank1.getAccountBalance("Alice"));
            System.out.println("Alle Transaktionen Alice:");
            for (Transaction t : bank1.getTransactions("Alice")) {
                System.out.println(t);
            }

            System.out.println("Positive Transaktionen Alice:");
            for (Transaction t : bank1.getTransactionsByType("Alice", true)) {
                System.out.println(t);
            }

            System.out.println("Negative Transaktionen Alice:");
            for (Transaction t : bank1.getTransactionsByType("Alice", false)) {
                System.out.println(t);
            }

            System.out.println("Transaktionen sortiert (aufsteigend):");
            for (Transaction t : bank1.getTransactionsSorted("Alice", true)) {
                System.out.println(t);
            }

            // Fehlerfälle testen
            try {
                bank1.createAccount("Alice"); // sollte AccountAlreadyExistsException werfen
            } catch (AccountAlreadyExistsException e) {
                System.out.println("Fehler getestet: AccountAlreadyExistsException korrekt geworfen.");
            }

            try {
                Payment invalidPayment = new Payment("2025-11-08", 100, "Invalid", -0.1, 0.02);
                bank1.addTransaction("Alice", invalidPayment);
            } catch (TransactionAttributeException e) {
                System.out.println("Fehler getestet: TransactionAttributeException für Payment korrekt geworfen.");
            }

            try {
                Transfer invalidTransfer = new OutgoingTransfer("2025-11-08", -100, "Invalid", "Alice", "Bob");
                bank1.addTransaction("Alice", invalidTransfer);
            } catch (TransactionAttributeException e) {
                System.out.println("Fehler getestet: TransactionAttributeException für Transfer korrekt geworfen.");
            }

            // PrivateBank equals testen
            PrivateBank copyTargetBank = new PrivateBank("copyMe", 0.1, 0.2);
            PrivateBank copyMeBank = new PrivateBank(copyTargetBank);
            System.out.println("copyTargetBank.equals(copyMeBank) = " + copyTargetBank.equals(copyMeBank)); // sollte true
            // leicht verändern
            copyTargetBank.setName("copyMeLOL");
            System.out.println("copyTargetBank.equals(copyMeBank) = " + copyTargetBank.equals(copyMeBank)); // sollte false

            // PrivateBankAlt testen
            PrivateBankAlt bankAlt = new PrivateBankAlt("AltBank", 0.04, 0.01);
            bankAlt.createAccount("Charlie");
            bankAlt.addTransaction("Charlie", new IncomingTransfer("2025-11-08", 250, "Deposit", "BankAlt", "Charlie"));
            bankAlt.addTransaction("Charlie", new OutgoingTransfer("2025-11-08", 100, "Withdrawal", "Charlie", "BankAlt"));

            System.out.println("Kontostand Charlie: " + bankAlt.getAccountBalance("Charlie"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
