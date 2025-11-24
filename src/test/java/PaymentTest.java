import bank.*;
import bank.exceptions.TransactionAttributeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test class for {@link Payment}.
 * Tests constructor, copy constructor, calculate(), equals(), and toString().
 */
class PaymentTest {
    private Payment payment1;
    private Payment payment2;
    private Payment payment3;

    @BeforeEach
    void init() throws TransactionAttributeException {
        // Positive payment (deposit) with 10% incoming interest
        payment1 = new Payment("01.01.2024", 100.0, "Salary", 0.1, 0.05);

        // Negative payment (withdrawal) with 5% outgoing interest
        payment2 = new Payment("02.01.2024", -200.0, "Rent", 0.1, 0.05);

        // Another positive payment for equality tests
        payment3 = new Payment("01.01.2024", 100.0, "Salary", 0.1, 0.05);
    }


    @Test
    void testConstructorValid() {
        assertDoesNotThrow(() -> {
            Payment p = new Payment("15.03.2024", 500.0, "Bonus", 0.15, 0.08);
            assertEquals("15.03.2024", p.getDate());
            assertEquals(500.0, p.getAmount());
            assertEquals("Bonus", p.getDescription());
            assertEquals(0.15, p.getIncomingInterest());
            assertEquals(0.08, p.getOutgoingInterest());
        });
    }

    @Test
    void testConstructorZeroAmount() {
        assertThrows(TransactionAttributeException.class, () ->
                new Payment("01.01.2024", 0.0, "Invalid", 0.1, 0.05));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.5, 2.0, -1.0})
    void testConstructorInvalidIncomingInterest(double invalidInterest) {
        assertThrows(TransactionAttributeException.class, () ->
                new Payment("01.01.2024", 100.0, "Test", invalidInterest, 0.05));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.5, 2.0, -1.0})
    void testConstructorInvalidOutgoingInterest(double invalidInterest) {
        assertThrows(TransactionAttributeException.class, () ->
                new Payment("01.01.2024", 100.0, "Test", 0.1, invalidInterest));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.1, 0.5, 0.99, 1.0})
    void testConstructorValidInterestRange(double validInterest) {
        assertDoesNotThrow(() ->
                new Payment("01.01.2024", 100.0, "Test", validInterest, validInterest));
    }


    @Test
    void testCopyConstructor() throws TransactionAttributeException {
        Payment copy = new Payment(payment1);

        assertEquals(payment1.getDate(), copy.getDate());
        assertEquals(payment1.getAmount(), copy.getAmount());
        assertEquals(payment1.getDescription(), copy.getDescription());
        assertEquals(payment1.getIncomingInterest(), copy.getIncomingInterest());
        assertEquals(payment1.getOutgoingInterest(), copy.getOutgoingInterest());

        // Verify they are different objects
        assertNotSame(payment1, copy);

        // But equal in content
        assertEquals(payment1, copy);
    }

    @Test
    void testCopyConstructorIndependence() throws TransactionAttributeException {
        Payment copy = new Payment(payment1);

        // Modify the copy
        copy.setIncomingInterest(0.2);
        copy.setOutgoingInterest(0.15);

        // Original should remain unchanged
        assertEquals(0.1, payment1.getIncomingInterest());
        assertEquals(0.05, payment1.getOutgoingInterest());

        // Copy should have new values
        assertEquals(0.2, copy.getIncomingInterest());
        assertEquals(0.15, copy.getOutgoingInterest());
    }



    @Test
    void testCalculatePositiveAmountWithIncomingInterest() {
        // 100 * (1 - 0.1) = 90.0
        assertEquals(90.0, payment1.calculate(), 0.001);
    }

    @Test
    void testCalculateNegativeAmountWithOutgoingInterest() {
        // -200 * (1 + 0.05) = -210.0
        assertEquals(-210.0, payment2.calculate(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {100.0, 250.0, 1000.0, 50.5})
    void testCalculatePositiveAmounts(double amount) throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", amount, "Test", 0.1, 0.05);
        double expected = amount * (1 - 0.1);
        assertEquals(expected, p.calculate(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100.0, -250.0, -1000.0, -50.5})
    void testCalculateNegativeAmounts(double amount) throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", amount, "Test", 0.1, 0.05);
        double expected = amount * (1 + 0.05);
        assertEquals(expected, p.calculate(), 0.001);
    }

    @Test
    void testCalculateWithZeroIncomingInterest() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 100.0, "Test", 0.0, 0.0);
        assertEquals(100.0, p.calculate(), 0.001);
    }

    @Test
    void testCalculateWithMaximumIncomingInterest() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 100.0, "Test", 1.0, 0.0);
        assertEquals(0.0, p.calculate(), 0.001);
    }

    @Test
    void testCalculateWithMaximumOutgoingInterest() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", -100.0, "Test", 0.0, 1.0);

        assertEquals(-200.0, p.calculate(), 0.001);
    }

    @Test
    void testCalculateAfterChangingInterest() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 100.0, "Test", 0.1, 0.05);
        assertEquals(90.0, p.calculate(), 0.001);

        p.setIncomingInterest(0.2);
        assertEquals(80.0, p.calculate(), 0.001);
    }


    @Test
    void testEqualsSameObject() {
        assertEquals(payment1, payment1);
    }

    @Test
    void testEqualsIdenticalPayments() {
        assertEquals(payment1, payment3);
        assertEquals(payment3, payment1);
    }

    @Test
    void testEqualsDifferentPayments() {
        assertNotEquals(payment1, payment2);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(null, payment1);
    }

    @Test
    void testEqualsDifferentClass() {
        assertNotEquals(payment1, "Not a Payment");
    }

    @Test
    void testEqualsDifferentDate() throws TransactionAttributeException {
        Payment p = new Payment("02.01.2024", 100.0, "Salary", 0.1, 0.05);
        assertNotEquals(payment1, p);
    }

    @Test
    void testEqualsDifferentAmount() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 150.0, "Salary", 0.1, 0.05);
        assertNotEquals(payment1, p);
    }

    @Test
    void testEqualsDifferentDescription() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 100.0, "Bonus", 0.1, 0.05);
        assertNotEquals(payment1, p);
    }

    @Test
    void testEqualsDifferentIncomingInterest() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 100.0, "Salary", 0.2, 0.05);
        assertNotEquals(payment1, p);
    }

    @Test
    void testEqualsDifferentOutgoingInterest() throws TransactionAttributeException {
        Payment p = new Payment("01.01.2024", 100.0, "Salary", 0.1, 0.1);
        assertNotEquals(payment1, p);
    }

    @Test
    void testEqualsSymmetric() throws TransactionAttributeException {
        Payment p1 = new Payment("01.01.2024", 100.0, "Test", 0.1, 0.05);
        Payment p2 = new Payment("01.01.2024", 100.0, "Test", 0.1, 0.05);

        assertEquals(p1, p2);
        assertEquals(p2, p1);
    }

    @Test
    void testEqualsTransitive() throws TransactionAttributeException {
        Payment p1 = new Payment("01.01.2024", 100.0, "Test", 0.1, 0.05);
        Payment p2 = new Payment("01.01.2024", 100.0, "Test", 0.1, 0.05);
        Payment p3 = new Payment("01.01.2024", 100.0, "Test", 0.1, 0.05);

        assertEquals(p1, p2);
        assertEquals(p2, p3);
        assertEquals(p1, p3);
    }

    @Test
    void testToStringContainsAllFields() {
        String str = payment1.toString();

        assertTrue(str.contains("01.01.2024"));
        assertTrue(str.contains("100.0"));
        assertTrue(str.contains("Salary"));
        assertTrue(str.contains("0.1"));
        assertTrue(str.contains("0.05"));
    }

    @Test
    void testToStringFormat() {
        String str = payment1.toString();
        assertTrue(str.toLowerCase().contains("payment") ||
                str.toLowerCase().contains("date") ||
                str.toLowerCase().contains("amount"));
    }

    @Test
    void testToStringDifferentForDifferentPayments() {
        String str1 = payment1.toString();
        String str2 = payment2.toString();

        assertNotEquals(str1, str2);
    }

    @Test
    void testToStringNegativeAmount() {
        String str = payment2.toString();
        assertTrue(str.contains("-200.0") || str.contains("200.0"));
    }

    @Test
    void testSetIncomingInterest() throws TransactionAttributeException {
        payment1.setIncomingInterest(0.15);
        assertEquals(0.15, payment1.getIncomingInterest());
    }

    @Test
    void testSetOutgoingInterest() throws TransactionAttributeException {
        payment1.setOutgoingInterest(0.08);
        assertEquals(0.08, payment1.getOutgoingInterest());
    }

    @Test
    void testSetInvalidIncomingInterest() {
        assertThrows(TransactionAttributeException.class, () ->
                payment1.setIncomingInterest(1.5));
    }

    @Test
    void testSetInvalidOutgoingInterest() {
        assertThrows(TransactionAttributeException.class, () ->
                payment1.setOutgoingInterest(-0.1));
    }
}