import bank.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("TEST: Payment & Transfer");

        // objekte erzeugen
        Payment pIn = new Payment("01.01.2025", 1000.0, "Gehalt", 0.05, 0.10);
        Payment pOut = new Payment("02.01.2025", -200.0, "Miete", 0.05, 0.10);
        Payment pCopy = new Payment(pIn);

        Transfer t1 = new Transfer("03.01.2025", 150.0, "Essen", "Paul", "Maria");
        Transfer t2 = new Transfer(t1);

        if (!t2.getDate().equals(t1.getDate())) {
            System.out.println("Transfer Error in Copy Constructor");
        }
        if (t2.getAmount() != t1.getAmount()) {
            System.out.println("Transfer Error in Copy Constructor");
        }
        if (!t2.getDescription().equals(t1.getDescription())) {
            System.out.println("Transfer Error in Copy Constructor");
        }
        if (!t2.getRecipient().equals(t1.getRecipient())) {
            System.out.println("Transfer Error in Copy Constructor");
        }
        if (!t2.getSender().equals(t1.getSender())) {
            System.out.println("Transfer Error in Copy Constructor");
        }

        // calculate() testen

        // Payment: Einzahlung
        double expectedIn = 1000.0 - 1000.0 * 0.05;     // 950
        assertDouble("Payment(Einzahlung)", expectedIn, pIn.calculate());

        // Payment: Auszahlung
        double expectedOut = -200.0 + (-200.0) * 0.10;  // -220
        assertDouble("Payment(Auszahlung)", expectedOut, pOut.calculate());

        // Payment Copy
        assertDouble("Payment(Copy)", pIn.calculate(), pCopy.calculate());

        // Transfer
        assertDouble("Transfer", 150.0, t1.calculate());

        // Transfer Copy
        assertDouble("Transfer(Copy)", t1.calculate(), t2.calculate());


        // equals() testen
        assertBool("Payment Copy equals", true, pIn.equals(pCopy));
        assertBool("Payment non-equals", false, pIn.equals(pOut));

        assertBool("Transfer Copy equals", true, t1.equals(t2));
        assertBool("Transfer non-equals", false, t1.equals(pIn));  // verschiedene Klassen


        // toString()
        System.out.println(pIn.toString());
        System.out.println(t1.toString());
    }

    private static void assertDouble(String name, double expected, double actual) {
        if (Math.abs(expected - actual) > 0.00001) {
            System.out.printf("%s FAILED — expected %.2f, got %.2f%n", name, expected, actual);
        }
    }

    private static void assertBool(String name, boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.printf("%s FAILED — expected %b, got %b%n", name, expected, actual);
        }
    }

    private static void assertNotNull(String name, Object value) {
        if (value == null) {
            System.out.printf("%s FAILED — value is null%n", name);
        }
    }
}
