import bank.*;
import bank.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrivateBankTest {
    private PrivateBank bank;
    private String testDirectory;
    private Payment payment1;
    private Payment payment2;
    private Transfer transfer1;
    private Transfer transfer2;

    @BeforeEach
    void init() throws IOException, TransactionAlreadyExistException, TransactionAttributeException, AccountAlreadyExistsException {
        testDirectory = "./testBankData/";
        bank = new PrivateBank("TestBank", 0.1, 0.05, testDirectory);

        // Create test transactions
        payment1 = new Payment("01.01.2024", 100.0, "Salary", 0.1, 0.05);
        payment2 = new Payment("02.01.2024", -50.0, "Expense", 0.1, 0.05);
        transfer1 = new Transfer("03.01.2024", 200.0, "Transfer In", "Alice", "Bob");
        transfer2 = new Transfer("04.01.2024", 150.0, "Transfer Out", "Bob", "Alice");
    }

    @AfterEach
    void cleanup() throws IOException {
        // Delete all test files
        File dir = new File(testDirectory);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dir.delete();
        }
    }

    @Test
    void testConstructor() {
        assertEquals("TestBank", bank.getName());
        assertEquals(0.1, bank.getIncomingInterest());
        assertEquals(0.05, bank.getOutgoingInterest());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.5})
    void testConstructorInvalidInterest(double invalidInterest) {
        assertThrows(TransactionAttributeException.class, () -> {
            new PrivateBank("InvalidBank", invalidInterest, 0.05, testDirectory);
        });
    }

    @Test
    void testCopyConstructor() throws IOException, TransactionAlreadyExistException, TransactionAttributeException, AccountAlreadyExistsException {
        bank.createAccount("TestAccount");
        PrivateBank copyBank = new PrivateBank(bank);

        assertEquals(bank.getName(), copyBank.getName());
        assertEquals(bank.getIncomingInterest(), copyBank.getIncomingInterest());
        assertEquals(bank.getOutgoingInterest(), copyBank.getOutgoingInterest());
    }

    @Test
    void testCreateAccount() throws AccountAlreadyExistsException, IOException {
        assertDoesNotThrow(() -> bank.createAccount("Alice"));
        assertNotNull(bank.getTransactions("Alice"));
        assertTrue(bank.getTransactions("Alice").isEmpty());
    }

    @Test
    void testCreateAccountAlreadyExists() throws AccountAlreadyExistsException, IOException {
        bank.createAccount("Alice");
        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("Alice"));
    }

    @Test
    void testCreateAccountWithTransactions() throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, IOException {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(payment1);
        transactions.add(transfer1);

        bank.createAccount("Bob", transactions);

        assertEquals(2, bank.getTransactions("Bob").size());
        assertTrue(bank.containsTransaction("Bob", payment1));
        assertTrue(bank.containsTransaction("Bob", transfer1));
    }

    @Test
    void testCreateAccountWithDuplicateTransactions() throws AccountAlreadyExistsException, IOException {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(payment1);
        transactions.add(payment1); // Duplicate

        assertThrows(TransactionAlreadyExistException.class, () ->
                bank.createAccount("Charlie", transactions));
    }

    @Test
    void testAddTransaction() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);

        assertTrue(bank.containsTransaction("Alice", payment1));
        assertEquals(1, bank.getTransactions("Alice").size());
    }

    @Test
    void testAddTransactionToNonExistentAccount() {
        assertThrows(AccountDoesNotExistException.class, () ->
                bank.addTransaction("NonExistent", payment1));
    }

    @Test
    void testAddDuplicateTransaction() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);

        assertThrows(TransactionAlreadyExistException.class, () ->
                bank.addTransaction("Alice", payment1));
    }

    @Test
    void testAddTransactionWithZeroAmount() throws AccountAlreadyExistsException, IOException, TransactionAttributeException {
        bank.createAccount("Alice");
        Payment zeroPayment = new Payment("05.01.2024", 0.0, "Zero", 0.1, 0.05);

        assertThrows(TransactionAttributeException.class, () ->
                bank.addTransaction("Alice", zeroPayment));
    }

    @Test
    void testAddTransferWithNegativeAmount() throws AccountAlreadyExistsException, IOException, TransactionAttributeException {
        bank.createAccount("Alice");

        assertThrows(TransactionAttributeException.class, () ->
                new Transfer("06.01.2024", -100.0, "Negative", "Alice", "Bob"));
    }

    @Test
    void testRemoveTransaction() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, TransactionDoesNotExistException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);
        bank.removeTransaction("Alice", payment1);

        assertFalse(bank.containsTransaction("Alice", payment1));
        assertTrue(bank.getTransactions("Alice").isEmpty());
    }

    @Test
    void testRemoveTransactionFromNonExistentAccount() {
        assertThrows(AccountDoesNotExistException.class, () ->
                bank.removeTransaction("NonExistent", payment1));
    }

    @Test
    void testRemoveNonExistentTransaction() throws AccountAlreadyExistsException, IOException {
        bank.createAccount("Alice");

        assertThrows(TransactionDoesNotExistException.class, () ->
                bank.removeTransaction("Alice", payment1));
    }

    @Test
    void testContainsTransaction() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);

        assertTrue(bank.containsTransaction("Alice", payment1));
        assertFalse(bank.containsTransaction("Alice", payment2));
    }

    @Test
    void testContainsTransactionNonExistentAccount() {
        assertFalse(bank.containsTransaction("NonExistent", payment1));
    }

    @ParameterizedTest
    @ValueSource(doubles = {100.0, -50.0, 200.0})
    void testGetAccountBalance(double amount) throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        Payment payment = new Payment("10.01.2024", amount, "Test", 0.1, 0.05);
        bank.addTransaction("Alice", payment);

        double expected = payment.calculate();
        assertEquals(expected, bank.getAccountBalance("Alice"), 0.001);
    }

    @Test
    void testGetAccountBalanceMultipleTransactions() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);
        bank.addTransaction("Alice", payment2);

        double expected = payment1.calculate() + payment2.calculate();
        assertEquals(expected, bank.getAccountBalance("Alice"), 0.001);
    }

    @Test
    void testGetTransactions() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);
        bank.addTransaction("Alice", transfer1);

        List<Transaction> transactions = bank.getTransactions("Alice");
        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(payment1));
        assertTrue(transactions.contains(transfer1));
    }

    @Test
    void testGetTransactionsSortedAscending() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);  // 110.0 (100 + 10% interest)
        bank.addTransaction("Alice", payment2);  // -47.5 (-50 - 5% outgoing)
        bank.addTransaction("Alice", transfer1); // 200.0

        List<Transaction> sorted = bank.getTransactionsSorted("Alice", true);

        assertEquals(3, sorted.size());
        assertTrue(sorted.get(0).calculate() <= sorted.get(1).calculate());
        assertTrue(sorted.get(1).calculate() <= sorted.get(2).calculate());
    }

    @Test
    void testGetTransactionsSortedDescending() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);
        bank.addTransaction("Alice", payment2);
        bank.addTransaction("Alice", transfer1);

        List<Transaction> sorted = bank.getTransactionsSorted("Alice", false);

        assertEquals(3, sorted.size());
        assertTrue(sorted.get(0).calculate() >= sorted.get(1).calculate());
        assertTrue(sorted.get(1).calculate() >= sorted.get(2).calculate());
    }

    @Test
    void testGetTransactionsByTypePositive() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);  // Positive
        bank.addTransaction("Alice", payment2);  // Negative
        bank.addTransaction("Alice", transfer1); // Positive

        List<Transaction> positive = bank.getTransactionsByType("Alice", true);

        assertEquals(2, positive.size());
        assertTrue(positive.stream().allMatch(t -> t.calculate() >= 0));
    }

    @Test
    void testGetTransactionsByTypeNegative() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("Alice");
        bank.addTransaction("Alice", payment1);  // Positive
        bank.addTransaction("Alice", payment2);  // Negative
        bank.addTransaction("Alice", transfer1); // Positive

        List<Transaction> negative = bank.getTransactionsByType("Alice", false);

        assertEquals(1, negative.size());
        assertTrue(negative.stream().allMatch(t -> t.calculate() < 0));
    }

    @Test
    void testEquals() throws IOException, TransactionAlreadyExistException, TransactionAttributeException, AccountAlreadyExistsException {
        PrivateBank bank1 = new PrivateBank("Bank1", 0.1, 0.05, testDirectory + "1/");
        PrivateBank bank2 = new PrivateBank("Bank1", 0.1, 0.05, testDirectory + "2/");
        PrivateBank bank3 = new PrivateBank("Bank2", 0.1, 0.05, testDirectory + "3/");

        assertEquals(bank1, bank2);
        assertNotEquals(bank1, bank3);
        assertNotEquals(bank1, null);
        assertNotEquals(bank1, "NotABank");
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(bank, bank);
    }

    @Test
    void testToString() {
        String str = bank.toString();
        assertTrue(str.contains("TestBank"));
        assertTrue(str.contains("interestController"));
        assertTrue(str.contains("accountsToTransactions"));
    }

    @Test
    void testPersistence() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("PersistTest");
        bank.addTransaction("PersistTest", payment1);

        // Create new bank instance - should load persisted data
        PrivateBank newBank = new PrivateBank("TestBank", 0.1, 0.05, testDirectory);

        assertNotNull(newBank.getTransactions("PersistTest"));
        assertEquals(1, newBank.getTransactions("PersistTest").size());
        assertTrue(newBank.containsTransaction("PersistTest", payment1));
    }
}