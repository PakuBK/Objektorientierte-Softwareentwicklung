package bank;

import bank.exceptions.*;

import java.util.*;

/**
 * Alternative Implementation of a private bank that manages accounts and transactions.
 * This Class calculates the {@code getAccountBalance} with type checking instead of relying on subclasses.
 * <p>
 * A {@code PrivateBank} stores a collection of accounts, each associated with a list of transactions.
 * It also manages incoming and outgoing interest rates, which are applied to payments.
 * </p>
 */
public class PrivateBankAlt implements Bank {
    /**
     * The name of the bank.
     */
    private String name;

    /**
     * Controller for managing incoming and outgoing interest rates.
     */
    private InterestController interestController;

    /**
     * Map storing accounts and their associated list of transactions.
     * Key: account name, Value: list of transactions.
     */
    private Map<String, List<Transaction>> accountsToTransactions;

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
    public void setOutgoingInterest(double val) throws IllegalArgumentException {
        interestController.setOutgoingInterest(val);
    }

    /**
     * Sets the incoming interest rate for the bank.
     *
     * @param val the new incoming interest (0..1)
     * @throws IllegalArgumentException if the value is outside 0..1
     */
    public void setIncomingInterest(double val) throws IllegalArgumentException {
        interestController.setIncomingInterest(val);
    }

    /**
     * Constructs a new PrivateBank with the specified name and interest rates.
     *
     * @param name            the name of the bank
     * @param incomingInterest the incoming interest rate (0..1)
     * @param outgoingInterest the outgoing interest rate (0..1)
     */
    public PrivateBankAlt(String name, double incomingInterest, double outgoingInterest) {
        this.name = name;
        this.interestController = new InterestController();
        this.interestController.setIncomingInterest(incomingInterest);
        this.interestController.setOutgoingInterest(outgoingInterest);
        this.accountsToTransactions = new HashMap<>();
    }

    /**
     * Copy constructor that creates a new PrivateBank from another instance.
     *
     * @param other the bank to copy
     */
    public PrivateBankAlt(PrivateBank other) {
        this(other.getName(), other.getIncomingInterest(), other.getOutgoingInterest());
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
        PrivateBankAlt that = (PrivateBankAlt) o;
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
    public void createAccount(String account) throws AccountAlreadyExistsException {
        if (this.accountsToTransactions.get(account) != null) throw new AccountAlreadyExistsException();
        this.accountsToTransactions.put(account, new ArrayList<>());
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
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException {
        this.createAccount(account);
        for (Transaction transaction: transactions) {
            try {
                this.addTransaction(account, transaction);
            }
            catch (AccountDoesNotExistException e) {
                throw new RuntimeException("Unexpected: account not found after creation.", e);
            }
        }
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
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) throw new AccountDoesNotExistException();
        if (this.containsTransaction(account, transaction)) throw new TransactionAlreadyExistException();

        if (transaction instanceof Payment payment) {
            payment.setIncomingInterest(this.getIncomingInterest());
            payment.setOutgoingInterest(this.getOutgoingInterest());
        }

        // ich weiß nicht ob ich extra validieren muss, da transfer niemals ein falschen Transaction Attribute haben kann
        // man könnte das so machen, aber tbh ich find das doof, das das verhalten von transactions in der private bank definiert wird und nicht mehr innerhalb von transaction bzw. deren subtypen
        if (transaction.getAmount() == 0) {
            throw new TransactionAttributeException("Transaction amount cannot be zero");
        }

        if (transaction instanceof Transfer t && t.getAmount() < 0) {
            throw new TransactionAttributeException("Transfer amount cannot be negative");
        }

        transactions.add(transaction);
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
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException {
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) throw new AccountDoesNotExistException();
        if (!this.containsTransaction(account, transaction)) throw new TransactionDoesNotExistException();
        transactions.remove(transaction);
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
    public double getAccountBalance(String account){
        List<Transaction> transactions = this.getTransactions(account);
        if (transactions == null) return 0;
        double sum = 0;
        for (Transaction t: transactions) {
            try {
                // i.m.o. unnötig (?)
                if (t instanceof Payment payment) {

                    // Validierung (Variante 2)
                    if (payment.getIncomingInterest() < 0 || payment.getIncomingInterest() > 1 ||
                            payment.getOutgoingInterest() < 0 || payment.getOutgoingInterest() > 1) {
                        throw new TransactionAttributeException("Invalid interest rates in Payment");
                    }
                    sum += payment.calculate();
                }
                else if (t instanceof Transfer transfer) {
                    double amount = transfer.getAmount();
                    if (amount < 0) throw new TransactionAttributeException();
                    if (transfer.getSender().equals(account)) amount *= -1;
                    sum += amount;
                }
                else {
                    sum += t.calculate();
                }
            }
            catch (TransactionAttributeException e) {
                // kann error werfen, müsste dann aber das interface anfassen, was ich nicht soll
                System.err.println("Invalide Transaction skipped");
            }
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
}
