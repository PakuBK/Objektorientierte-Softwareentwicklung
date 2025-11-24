package bank;

import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Implementation of a private bank that manages accounts and transactions.
 * <p>
 * A {@code PrivateBank} stores a collection of accounts, each associated with a list of transactions.
 * It also manages incoming and outgoing interest rates, which are applied to payments.
 * </p>
 */
public class PrivateBank implements Bank {
    /**
     * The name of the bank.
     */
    private String name;

    /**
     * Controller for managing incoming and outgoing interest rates.
     */
    private final InterestController interestController;

    /**
     * Map storing accounts and their associated list of transactions.
     * Key: account name, Value: list of transactions.
     */
    private final Map<String, List<Transaction>> accountsToTransactions;

    /**
     * The path where account data is stored in json.
     */
    private String directoryName = "./";

    /**
     * GSON instance to serialize and deserialize account data
     */
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Payment.class, new PaymentAdapter())
            .registerTypeAdapter(Transaction.class, new TransactionAdapter())
            .setPrettyPrinting().create();

    /**
     * Returns the bank's current incoming interest rate.
     *
     * @return the incoming interest as a decimal (0..1)
     */
    public double getIncomingInterest() {
        return interestController.getIncomingInterest();
    }

    /**
     * Returns the bank's current outgoing interest rate.
     *
     * @return the outgoing interest as a decimal (0..1)
     */
    public double getOutgoingInterest() {
        return interestController.getOutgoingInterest();
    }

    /**
     * Returns the name of the bank.
     *
     * @return the bank's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the bank.
     *
     * @param val the new name
     */
    public void setName(String val) {
        this.name = val;
    }

    /**
     * Sets the outgoing interest rate for the bank.
     *
     * @param val the new outgoing interest (0..1)
     * @throws IllegalArgumentException if the value is outside 0..1
     */
    public void setOutgoingInterest(double val) throws TransactionAttributeException {
        interestController.setOutgoingInterest(val);
    }

    /**
     * Sets the incoming interest rate for the bank.
     *
     * @param val the new incoming interest (0..1)
     * @throws IllegalArgumentException if the value is outside 0..1
     */
    public void setIncomingInterest(double val) throws TransactionAttributeException {
        interestController.setIncomingInterest(val);
    }

    /**
     * Constructs a new PrivateBank with the specified name and interest rates.
     *
     * @param name            the name of the bank
     * @param incomingInterest the incoming interest rate (0..1)
     * @param outgoingInterest the outgoing interest rate (0..1)
     */
    public PrivateBank(String name, double incomingInterest, double outgoingInterest, String directoryName) throws IOException,
            TransactionAlreadyExistException,TransactionAttributeException, AccountAlreadyExistsException {
        this.name = name;
        this.interestController = new InterestController();
        try {
            this.interestController.setIncomingInterest(incomingInterest);
            this.interestController.setOutgoingInterest(outgoingInterest);
        }
        catch (IllegalArgumentException e) {
            throw new TransactionAttributeException();
        }
        this.accountsToTransactions = new HashMap<>();
        this.directoryName = directoryName;

        File dir = new File(this.directoryName);
        dir.mkdirs();

        File[] files = dir.listFiles((d, filename) -> filename.endsWith(".json"));
        if (files != null && files.length > 0) {
            for (File file: files) {
                String accountName = file.getName().replace("Konto ", "").replace(".json", "");
                List<Transaction> list = readAccount(accountName);
                this.accountsToTransactions.put(accountName, list);
            }
        }
    }

    /**
     * Copy constructor that creates a new PrivateBank from another instance.
     *
     * @param other the bank to copy
     */
    public PrivateBank(PrivateBank other) throws IOException,
    TransactionAlreadyExistException,TransactionAttributeException, AccountAlreadyExistsException{
        this(other.getName(), other.getIncomingInterest(), other.getOutgoingInterest(), other.directoryName);
    }

    /**
     * Returns a string representation of the bank, including name, interest rates, and accounts.
     *
     * @return a string describing the bank
     */
    @Override
    public String toString() {
        return "PrivateBank{" +
                "name='" + name + '\'' +
                ", interestController=" + interestController +
                ", accountsToTransactions=" + accountsToTransactions +
                ", directoryName=" + directoryName +
                '}';
    }

    /**
     * Compares this bank with another object for equality.
     * Two banks are equal if they have the same name, interest rates, and account data.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateBank that = (PrivateBank) o;
        return name.equals(that.name) &&
                interestController.equals(that.interestController) &&
                accountsToTransactions.equals(that.accountsToTransactions);
    }

    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        if (this.accountsToTransactions.get(account) != null) throw new AccountAlreadyExistsException();
        this.accountsToTransactions.put(account, new ArrayList<>());
        writeAccount(account, new ArrayList<>());
    }

    /**
     * Adds an account (with specified transactions) to the bank.
     * Important: duplicate transactions must not be added to the account!
     *
     * @param account      the account to be added
     * @param transactions a list of already existing transactions which should be added to the newly created account
     * @throws AccountAlreadyExistsException    if the account already exists
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException,
            TransactionAlreadyExistException, TransactionAttributeException, IOException {
        this.createAccount(account);
        for (Transaction transaction: transactions) {
            try {
                this.addTransaction(account, transaction);
            }
            catch (AccountDoesNotExistException e) {
                throw new RuntimeException("Unexpected: account not found after creation.", e);
            }
        }
        writeAccount(account, accountsToTransactions.get(account));
    }

    /**
     * Adds a transaction to an already existing account.
     *
     * @param account     the account to which the transaction is added
     * @param transaction the transaction which should be added to the specified account
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) throw new AccountDoesNotExistException();
        if (this.containsTransaction(account, transaction)) throw new TransactionAlreadyExistException();

        if (transaction instanceof Payment payment) {
            payment.setIncomingInterest(this.getIncomingInterest());
            payment.setOutgoingInterest(this.getOutgoingInterest());

            // Validierung, auch wenn meiner Meinung nach unnötig
            if (payment.getIncomingInterest() < 0 || payment.getIncomingInterest() > 1
                    || payment.getOutgoingInterest() < 0 || payment.getOutgoingInterest() > 1) {
                throw new TransactionAttributeException(
                        "Payment interest rates must be between 0 and 1");
            }
        }
        if (transaction.getAmount() == 0) {
            throw new TransactionAttributeException("Transaction amount cannot be zero");
        }

        if (transaction instanceof Transfer t && t.getAmount() < 0) {
            throw new TransactionAttributeException("Transfer amount cannot be negative");
        }

        transactions.add(transaction);
        writeAccount(account, transactions);
    }

    /**
     * Removes a transaction from an account. If the transaction does not exist, an exception is
     * thrown.
     *
     * @param account     the account from which the transaction is removed
     * @param transaction the transaction which is removed from the specified account
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionDoesNotExistException if the transaction cannot be found
     */
    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) throw new AccountDoesNotExistException();
        if (!this.containsTransaction(account, transaction)) throw new TransactionDoesNotExistException();
        transactions.remove(transaction);
        writeAccount(account, transactions);
    }

    /**
     * Checks whether the specified transaction for a given account exists.
     *
     * @param account     the account from which the transaction is checked
     * @param transaction the transaction to search/look for
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) return false;
        return transactions.contains(transaction);
    }

    /**
     * Calculates and returns the current account balance.
     *
     * @param account the selected account
     * @return the current account balance
     */
    @Override
    public double getAccountBalance(String account) {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) return 0;
        double sum = 0;
        for (Transaction t :transactions) {
            sum += t.calculate();
        }
        return sum;
    }
    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        return this.accountsToTransactions.get(account);
    }

    /**
     * Returns a sorted list (-> calculated amounts) of transactions for a specific account. Sorts the list either in ascending or descending order
     * (or empty).
     *
     * @param account the selected account
     * @param asc     selects if the transaction list is sorted in ascending or descending order
     * @return the sorted list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) return null;

        List<Transaction> transactions_sorted = new ArrayList<>(transactions);
        transactions_sorted.sort(Comparator.comparing(Transaction::calculate));
        if (!asc) Collections.reverse(transactions_sorted);
        return transactions_sorted;
    }

    /**
     * Returns a list of either positive or negative transactions (-> calculated amounts).
     *
     * @param account  the selected account
     * @param positive selects if positive or negative transactions are listed
     * @return the list of all transactions by type
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) return null;

        List<Transaction> filtered = new ArrayList<>(transactions);
        return filtered.stream().filter(t -> positive ? t.calculate() >= 0 : t.calculate() < 0).toList();
    }

    /**
     * Serializes an account and their corresponding transactions into json
     * @param accountName name of the account
     * @param transactions list of transactions
     * @throws IOException
     */
    public void writeAccount(String accountName, List<Transaction> transactions) throws IOException {
        File file = new File(directoryName, "Konto " + accountName + ".json");
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            Type type = new TypeToken<List<Transaction>>() {}.getType();
            gson.toJson(transactions, type, writer);
        }
    }

    /**
     * Deserialize an accounts transactions from json
     * @param accountName name of the account
     * @return List of transactions
     * @throws IOException
     */
    public List<Transaction> readAccount(String accountName) throws IOException {
        File file = new File(directoryName, "Konto " + accountName + ".json");

        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Transaction>>() {}.getType();
            return gson.fromJson(reader, type);
        }
    }



}
