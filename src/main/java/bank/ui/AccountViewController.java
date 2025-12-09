package bank.ui;

import bank.PrivateBank;
import bank.Transaction;
import bank.Payment;
import bank.Transfer;
import bank.IncomingTransfer;
import bank.OutgoingTransfer;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class AccountViewController {

    @FXML
    private Label accountNameLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private ListView<String> transactionListView;

    private String accountName;
    private PrivateBank privateBank;
    private Stage stage;
    private ObservableList<String> transactionList;

    /**
     * Initialisiert die View mit Account-Daten
     */
    public void initData(String accountName, PrivateBank privateBank, Stage stage) {
        this.accountName = accountName;
        this.privateBank = privateBank;
        this.stage = stage;

        accountNameLabel.setText("Account: " + accountName);
        updateView();
    }

    /**
     * Aktualisiert die gesamte Ansicht (Kontostand und Transaktionen)
     */
    private void updateView() {
        // Kontostand berechnen und anzeigen
        double balance = privateBank.getAccountBalance(accountName);
        balanceLabel.setText(String.format("%.2f €", balance));

        // Transaktionen laden
        List<Transaction> transactions = privateBank.getTransactions(accountName);
        transactionList = FXCollections.observableArrayList();

        for (Transaction t : transactions) {
            transactionList.add(t.toString());
        }

        transactionListView.setItems(transactionList);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/ui/MainView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Controller holen und PrivateBank übergeben
            MainViewController controller = loader.getController();
            controller.setPrivateBank(privateBank);
            
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Fehler beim Laden", e.getMessage());
        }
    }

    @FXML
    private void handleSortAscending() {
        List<Transaction> transactions = privateBank.getTransactionsSorted(accountName, true);
        updateTransactionList(transactions);
    }

    @FXML
    private void handleSortDescending() {
        List<Transaction> transactions = privateBank.getTransactionsSorted(accountName, false);
        updateTransactionList(transactions);
    }

    @FXML
    private void handleFilterPositive() {
        List<Transaction> transactions = privateBank.getTransactionsByType(accountName, true);
        updateTransactionList(transactions);
    }

    @FXML
    private void handleFilterNegative() {
        List<Transaction> transactions = privateBank.getTransactionsByType(accountName, false);
        updateTransactionList(transactions);
    }

    @FXML
    private void handleShowAll() {
        updateView();
    }

    @FXML
    private void handleDeleteTransaction() {
        int selectedIndex = transactionListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showWarning("Keine Auswahl", "Bitte wählen Sie zuerst eine Transaktion aus.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Transaktion löschen");
        alert.setHeaderText("Transaktion wirklich löschen?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                List<Transaction> transactions = privateBank.getTransactions(accountName);
                Transaction transactionToDelete = transactions.get(selectedIndex);

                privateBank.removeTransaction(accountName, transactionToDelete);
                updateView();
                showInfo("Erfolg", "Transaktion wurde gelöscht.");

            } catch (Exception e) {
                showError("Fehler beim Löschen", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddTransaction() {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Neue Transaktion");
        dialog.setHeaderText("Neue Transaktion erstellen");

        ButtonType createButtonType = new ButtonType("Erstellen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Type Selection
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Payment", "Transfer");
        typeCombo.setValue("Payment");

        TextField dateField = new TextField();
        dateField.setPromptText("Datum (z.B. 08.12.2025)");

        TextField amountField = new TextField();
        amountField.setPromptText("Betrag");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Beschreibung");

        // Felder für Payment
        TextField incomingInterestField = new TextField();
        incomingInterestField.setPromptText("Incoming Interest (0.0 - 1.0)");
        incomingInterestField.setVisible(true);
        
        TextField outgoingInterestField = new TextField();
        outgoingInterestField.setPromptText("Outgoing Interest (0.0 - 1.0)");
        outgoingInterestField.setVisible(true);

        // Felder für Transfer
        TextField senderField = new TextField();
        senderField.setPromptText("Sender");
        senderField.setVisible(false);

        TextField recipientField = new TextField();
        recipientField.setPromptText("Empfänger");
        recipientField.setVisible(false);

        // Dynamic field visibility
        typeCombo.setOnAction(e -> {
            boolean isPayment = typeCombo.getValue().equals("Payment");
            incomingInterestField.setVisible(isPayment);
            outgoingInterestField.setVisible(isPayment);
            senderField.setVisible(!isPayment);
            recipientField.setVisible(!isPayment);
        });

        grid.add(new Label("Typ:"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("Datum:"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Betrag:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Beschreibung:"), 0, 3);
        grid.add(descriptionField, 1, 3);
        grid.add(new Label("Incoming Interest:"), 0, 4);
        grid.add(incomingInterestField, 1, 4);
        grid.add(new Label("Outgoing Interest:"), 0, 5);
        grid.add(outgoingInterestField, 1, 5);
        grid.add(new Label("Sender:"), 0, 6);
        grid.add(senderField, 1, 6);
        grid.add(new Label("Empfänger:"), 0, 7);
        grid.add(recipientField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String date = dateField.getText();
                    String amountText = amountField.getText();
                    String description = descriptionField.getText();

                    // Validierung: Alle Pflichtfelder müssen ausgefüllt sein
                    if (date.isEmpty() || amountText.isEmpty() || description.isEmpty()) {
                        throw new IllegalArgumentException("Datum, Betrag und Beschreibung müssen ausgefüllt sein!");
                    }

                    double amount = Double.parseDouble(amountText);

                    if (typeCombo.getValue().equals("Payment")) {
                        String incomingInterestText = incomingInterestField.getText();
                        String outgoingInterestText = outgoingInterestField.getText();
                        
                        if (incomingInterestText.isEmpty() || outgoingInterestText.isEmpty()) {
                            throw new IllegalArgumentException("Beide Interest-Felder müssen ausgefüllt sein!");
                        }
                        
                        double incomingInterest = Double.parseDouble(incomingInterestText);
                        double outgoingInterest = Double.parseDouble(outgoingInterestText);
                        
                        return new Payment(date, amount, description, incomingInterest, outgoingInterest);
                        
                    } else { // Transfer
                        String sender = senderField.getText();
                        String recipient = recipientField.getText();

                        if (sender.isEmpty() || recipient.isEmpty()) {
                            throw new IllegalArgumentException("Sender und Empfänger müssen angegeben werden!");
                        }
                        
                        if (recipient.equals(this.accountName)) {
                            // Aktueller Account ist Empfänger -> IncomingTransfer
                            return new IncomingTransfer(date, amount, description, sender, recipient);
                        } else if (sender.equals(this.accountName)) {
                            // Aktueller Account ist Sender -> OutgoingTransfer
                            return new OutgoingTransfer(date, amount, description, sender, recipient);
                        } else {
                            throw new IllegalArgumentException(
                                "Entweder Sender oder Empfänger muss der aktuelle Account (" + this.accountName + ") sein!"
                            );
                        }
                    }
                } catch (NumberFormatException e) {
                    showError("Ungültige Eingabe", "Betrag und Zinsen müssen gültige Zahlen sein!");
                    return null;
                } catch (Exception e) {
                    showError("Ungültige Eingabe", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(transaction -> {
            try {
                privateBank.addTransaction(accountName, transaction);
                updateView(); // Aktualisiert auch den Kontostand
                showInfo("Erfolg", "Transaktion wurde hinzugefügt.");
            } catch (Exception e) {
                showError("Fehler beim Hinzufügen", e.getMessage());
            }
        });
    }

    private void updateTransactionList(List<Transaction> transactions) {
        transactionList.clear();
        for (Transaction t : transactions) {
            transactionList.add(t.toString());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}