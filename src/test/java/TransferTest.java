import bank.*;

import bank.exceptions.TransactionAttributeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test class for {@link Transfer}.
 * Tests constructor, copy constructor, calculate(), equals(), and toString().
 * Also tests IncomingTransfer and OutgoingTransfer in calculate() tests.
 */
class TransferTest {
    private Transfer transfer1;
    private Transfer transfer2;
    private Transfer transfer3;
    private IncomingTransfer incomingTransfer;
    private OutgoingTransfer outgoingTransfer;

    @BeforeEach
    void init() throws TransactionAttributeException {
        // Standard transfers
        transfer1 = new Transfer("01.01.2024", 100.0, "Payment", "Alice", "Bob");
        transfer2 = new Transfer("02.01.2024", 200.0, "Rent", "Bob", "Charlie");
        transfer3 = new Transfer("01.01.2024", 100.0, "Payment", "Alice", "Bob");

        // Incoming and Outgoing transfers for calculate() tests
        incomingTransfer = new IncomingTransfer("03.01.2024", 150.0, "Gift", "David", "Alice");
        outgoingTransfer = new OutgoingTransfer("04.01.2024", 75.0, "Payment", "Alice", "Eve");
    }

    @Test
    void testConstructorValid() {
        assertDoesNotThrow(() -> {
            Transfer t = new Transfer("15.03.2024", 500.0, "Transfer", "Alice", "Bob");
            assertEquals("15.03.2024", t.getDate());
            assertEquals(500.0, t.getAmount());
            assertEquals("Transfer", t.getDescription());
            assertEquals("Alice", t.getSender());
            assertEquals("Bob", t.getRecipient());
        });
    }

    @Test
    void testConstructorZeroAmount() {
        assertDoesNotThrow(() -> {
            Transfer t = new Transfer("01.01.2024", 0.0, "Test", "Alice", "Bob");
            assertEquals(0.0, t.getAmount());
        });
    }

    @Test
    void testConstructorNegativeAmount() {
        assertThrows(TransactionAttributeException.class, () ->
                new Transfer("01.01.2024", -100.0, "Invalid", "Alice", "Bob"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 50.0, 100.0, 1000.0, 9999.99})
    void testConstructorValidAmounts(double amount) {
        assertDoesNotThrow(() -> {
            Transfer t = new Transfer("01.01.2024", amount, "Test", "Alice", "Bob");
            assertEquals(amount, t.getAmount());
        });
    }

    @Test
    void testConstructorAllFields() throws TransactionAttributeException {
        Transfer t = new Transfer("10.05.2024", 250.5, "Birthday gift", "Mom", "Son");

        assertEquals("10.05.2024", t.getDate());
        assertEquals(250.5, t.getAmount(), 0.001);
        assertEquals("Birthday gift", t.getDescription());
        assertEquals("Mom", t.getSender());
        assertEquals("Son", t.getRecipient());
    }


    @Test
    void testCopyConstructor() throws TransactionAttributeException {
        Transfer copy = new Transfer(transfer1);

        assertEquals(transfer1.getDate(), copy.getDate());
        assertEquals(transfer1.getAmount(), copy.getAmount());
        assertEquals(transfer1.getDescription(), copy.getDescription());
        assertEquals(transfer1.getSender(), copy.getSender());
        assertEquals(transfer1.getRecipient(), copy.getRecipient());

        // Verify they are different objects
        assertNotSame(transfer1, copy);

        // But equal in content
        assertEquals(transfer1, copy);
    }

    @Test
    void testCopyConstructorIndependence() throws TransactionAttributeException {
        Transfer copy = new Transfer(transfer1);

        // Modify the copy
        copy.setSender("NewSender");
        copy.setRecipient("NewRecipient");
        copy.setAmount(999.0);

        // Original should remain unchanged
        assertEquals("Alice", transfer1.getSender());
        assertEquals("Bob", transfer1.getRecipient());
        assertEquals(100.0, transfer1.getAmount());

        // Copy should have new values
        assertEquals("NewSender", copy.getSender());
        assertEquals("NewRecipient", copy.getRecipient());
        assertEquals(999.0, copy.getAmount());
    }

    @Test
    void testCopyConstructorWithIncomingTransfer() throws TransactionAttributeException {
        IncomingTransfer copy = new IncomingTransfer(incomingTransfer);

        assertEquals(incomingTransfer.getDate(), copy.getDate());
        assertEquals(incomingTransfer.getAmount(), copy.getAmount());
        assertEquals(incomingTransfer.getDescription(), copy.getDescription());
        assertEquals(incomingTransfer.getSender(), copy.getSender());
        assertEquals(incomingTransfer.getRecipient(), copy.getRecipient());
    }

    @Test
    void testCopyConstructorWithOutgoingTransfer() throws TransactionAttributeException {
        OutgoingTransfer copy = new OutgoingTransfer(outgoingTransfer);

        assertEquals(outgoingTransfer.getDate(), copy.getDate());
        assertEquals(outgoingTransfer.getAmount(), copy.getAmount());
        assertEquals(outgoingTransfer.getDescription(), copy.getDescription());
        assertEquals(outgoingTransfer.getSender(), copy.getSender());
        assertEquals(outgoingTransfer.getRecipient(), copy.getRecipient());
    }


    @Test
    void testCalculateTransfer() {
        // Transfer returns the amount as-is
        assertEquals(100.0, transfer1.calculate(), 0.001);
        assertEquals(200.0, transfer2.calculate(), 0.001);
    }

    @Test
    void testCalculateIncomingTransfer() {
        // IncomingTransfer returns positive amount
        assertEquals(150.0, incomingTransfer.calculate(), 0.001);
    }

    @Test
    void testCalculateOutgoingTransfer() {
        // OutgoingTransfer returns negative amount
        assertEquals(-75.0, outgoingTransfer.calculate(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 50.0, 100.0, 500.0, 1000.0})
    void testCalculateIncomingTransferVariousAmounts(double amount) throws TransactionAttributeException {
        IncomingTransfer incoming = new IncomingTransfer("01.01.2024", amount, "Test", "Sender", "Recipient");
        assertEquals(amount, incoming.calculate(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 50.0, 100.0, 500.0, 1000.0})
    void testCalculateOutgoingTransferVariousAmounts(double amount) throws TransactionAttributeException {
        OutgoingTransfer outgoing = new OutgoingTransfer("01.01.2024", amount, "Test", "Sender", "Recipient");
        assertEquals(-amount, outgoing.calculate(), 0.001);
    }

    @Test
    void testCalculateZeroAmount() throws TransactionAttributeException {
        Transfer t = new Transfer("01.01.2024", 0.0, "Zero", "Alice", "Bob");
        assertEquals(0.0, t.calculate(), 0.001);
    }

    @Test
    void testCalculateIncomingTransferZeroAmount() throws TransactionAttributeException {
        IncomingTransfer incoming = new IncomingTransfer("01.01.2024", 0.0, "Zero", "Alice", "Bob");
        assertEquals(0.0, incoming.calculate(), 0.001);
    }

    @Test
    void testCalculateOutgoingTransferZeroAmount() throws TransactionAttributeException {
        OutgoingTransfer outgoing = new OutgoingTransfer("01.01.2024", 0.0, "Zero", "Alice", "Bob");
        assertEquals(0.0, outgoing.calculate(), 0.001);
    }

    @Test
    void testCalculateAfterAmountChange() throws TransactionAttributeException {
        Transfer t = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        assertEquals(100.0, t.calculate(), 0.001);

        t.setAmount(250.0);
        assertEquals(250.0, t.calculate(), 0.001);
    }

    @Test
    void testCalculateIncomingVsOutgoing() throws TransactionAttributeException {
        double amount = 100.0;
        IncomingTransfer incoming = new IncomingTransfer("01.01.2024", amount, "Test", "A", "B");
        OutgoingTransfer outgoing = new OutgoingTransfer("01.01.2024", amount, "Test", "A", "B");

        // Incoming is positive
        assertTrue(incoming.calculate() > 0);

        // Outgoing is negative
        assertTrue(outgoing.calculate() < 0);

        // Their absolute values are equal
        assertEquals(Math.abs(incoming.calculate()), Math.abs(outgoing.calculate()), 0.001);
    }


    @Test
    void testEqualsSameObject() {
        assertEquals(transfer1, transfer1);
    }

    @Test
    void testEqualsIdenticalTransfers() {
        assertEquals(transfer1, transfer3);
        assertEquals(transfer3, transfer1);
    }

    @Test
    void testEqualsDifferentTransfers() {
        assertNotEquals(transfer1, transfer2);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(null, transfer1);
    }

    @Test
    void testEqualsDifferentClass() {
        assertNotEquals(transfer1, "Not a Transfer");
    }

    @Test
    void testEqualsDifferentDate() throws TransactionAttributeException {
        Transfer t = new Transfer("02.01.2024", 100.0, "Payment", "Alice", "Bob");
        assertNotEquals(transfer1, t);
    }

    @Test
    void testEqualsDifferentAmount() throws TransactionAttributeException {
        Transfer t = new Transfer("01.01.2024", 150.0, "Payment", "Alice", "Bob");
        assertNotEquals(transfer1, t);
    }

    @Test
    void testEqualsDifferentDescription() throws TransactionAttributeException {
        Transfer t = new Transfer("01.01.2024", 100.0, "Gift", "Alice", "Bob");
        assertNotEquals(transfer1, t);
    }

    @Test
    void testEqualsDifferentSender() throws TransactionAttributeException {
        Transfer t = new Transfer("01.01.2024", 100.0, "Payment", "Charlie", "Bob");
        assertNotEquals(transfer1, t);
    }

    @Test
    void testEqualsDifferentRecipient() throws TransactionAttributeException {
        Transfer t = new Transfer("01.01.2024", 100.0, "Payment", "Alice", "Charlie");
        assertNotEquals(transfer1, t);
    }

    @Test
    void testEqualsSymmetric() throws TransactionAttributeException {
        Transfer t1 = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        Transfer t2 = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");

        assertEquals(t1, t2);
        assertEquals(t2, t1);
    }

    @Test
    void testEqualsTransitive() throws TransactionAttributeException {
        Transfer t1 = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        Transfer t2 = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        Transfer t3 = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");

        assertEquals(t1, t2);
        assertEquals(t2, t3);
        assertEquals(t1, t3);
    }

    @Test
    void testEqualsIncomingTransfer() throws TransactionAttributeException {
        IncomingTransfer in1 = new IncomingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        IncomingTransfer in2 = new IncomingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");

        assertEquals(in1, in2);
    }

    @Test
    void testEqualsOutgoingTransfer() throws TransactionAttributeException {
        OutgoingTransfer out1 = new OutgoingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        OutgoingTransfer out2 = new OutgoingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");

        assertEquals(out1, out2);
    }

    @Test
    void testEqualsIncomingVsOutgoingTransfer() throws TransactionAttributeException {
        IncomingTransfer incoming = new IncomingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        OutgoingTransfer outgoing = new OutgoingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");

        // Different classes, should not be equal
        assertNotEquals(incoming, outgoing);
    }

    @Test
    void testEqualsTransferVsIncomingTransfer() throws TransactionAttributeException {
        Transfer transfer = new Transfer("01.01.2024", 100.0, "Test", "Alice", "Bob");
        IncomingTransfer incoming = new IncomingTransfer("01.01.2024", 100.0, "Test", "Alice", "Bob");

        // Different classes, should not be equal
        assertNotEquals(transfer, incoming);
    }

    @Test
    void testToStringContainsAllFields() {
        String str = transfer1.toString();

        assertTrue(str.contains("01.01.2024"));
        assertTrue(str.contains("100.0"));
        assertTrue(str.contains("Payment"));
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("Bob"));
    }

    @Test
    void testToStringContainsSenderRecipient() {
        String str = transfer1.toString();

        assertTrue(str.toLowerCase().contains("sender") || str.contains("Alice"));
        assertTrue(str.toLowerCase().contains("recipient") || str.contains("Bob"));
    }

    @Test
    void testToStringDifferentForDifferentTransfers() {
        String str1 = transfer1.toString();
        String str2 = transfer2.toString();

        assertNotEquals(str1, str2);
    }

    @Test
    void testToStringIncomingTransfer() {
        String str = incomingTransfer.toString();

        assertTrue(str.contains("150.0"));
        assertTrue(str.contains("Gift"));
        assertTrue(str.contains("David"));
        assertTrue(str.contains("Alice"));
    }

    @Test
    void testToStringOutgoingTransfer() {
        String str = outgoingTransfer.toString();

        assertTrue(str.contains("75.0"));
        assertTrue(str.contains("Payment"));
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("Eve"));
    }

    @Test
    void testToStringContainsCalculatedValue() {
        String str = transfer1.toString();
        assertTrue(str.toLowerCase().contains("calculated") || str.contains("100.0"));
    }

    // ==================== Setter/Getter Tests ====================

    @Test
    void testSetSender() {
        transfer1.setSender("NewSender");
        assertEquals("NewSender", transfer1.getSender());
    }

    @Test
    void testSetRecipient() {
        transfer1.setRecipient("NewRecipient");
        assertEquals("NewRecipient", transfer1.getRecipient());
    }

    @Test
    void testSetAmountValid() throws TransactionAttributeException {
        transfer1.setAmount(500.0);
        assertEquals(500.0, transfer1.getAmount());
    }

    @Test
    void testSetAmountNegative() {
        assertThrows(TransactionAttributeException.class, () ->
                transfer1.setAmount(-100.0));
    }

    @Test
    void testSetAmountZero() throws TransactionAttributeException {
        assertDoesNotThrow(() -> transfer1.setAmount(0.0));
        assertEquals(0.0, transfer1.getAmount());
    }

    @Test
    void testSetAmountIncomingTransfer() throws TransactionAttributeException {
        incomingTransfer.setAmount(300.0);
        assertEquals(300.0, incomingTransfer.getAmount());
        assertEquals(300.0, incomingTransfer.calculate(), 0.001);
    }

    @Test
    void testSetAmountOutgoingTransfer() throws TransactionAttributeException {
        outgoingTransfer.setAmount(200.0);
        assertEquals(200.0, outgoingTransfer.getAmount());
        assertEquals(-200.0, outgoingTransfer.calculate(), 0.001);
    }
}