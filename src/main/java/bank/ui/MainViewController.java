package bank.ui;

import bank.PrivateBank;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainViewController {

    private final String ACCOUNT_VIEW_PATH = "/AccountView.fxml";

    @FXML
    private ListView<String> accountListView;

    private PrivateBank privateBank;
    private ObservableList<String> accountList;

    @FXML
    public void initialize() {
        try {
            // PrivateBank initialisieren (passen Sie den Pfad an!)
            privateBank = new PrivateBank("SUPERBANK", 0.05, 0.1, "./accounts");

            // ObservableList für automatische UI-Updates
            accountList = FXCollections.observableArrayList(privateBank.getAllAccounts());
            accountListView.setItems(accountList);

        } catch (Exception e) {
            showError("Fehler beim Laden der Bank", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddAccount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neuen Account erstellen");
        dialog.setHeaderText("Account hinzufügen");
        dialog.setContentText("Accountname:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(accountName -> {
            if (accountName.trim().isEmpty()) {
                showError("Ungültige Eingabe", "Accountname darf nicht leer sein!");
                return;
            }

            try {
                privateBank.createAccount(accountName);
                accountList.add(accountName);
                showInfo("Erfolg", "Account '" + accountName + "' wurde erstellt.");
            } catch (AccountAlreadyExistsException e) {
                showError("Fehler", "Account existiert bereits!");
            } catch (IOException e) {
                showError("Fehler beim Erstellen", e.getMessage());
            }
        });
    }

    @FXML
    private void handleSelectAccount() {
        String selectedAccount = accountListView.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Account aus.");
            return;
        }
        openAccountView(selectedAccount);
    }

    @FXML
    private void handleDeleteAccount() {
        String selectedAccount = accountListView.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showWarning("Keine Auswahl", "Bitte wählen Sie zuerst einen Account aus.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Account löschen");
        alert.setHeaderText("Account wirklich löschen?");
        alert.setContentText("Möchten Sie den Account '" + selectedAccount + "' wirklich löschen?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                privateBank.deleteAccount(selectedAccount);
                accountList.remove(selectedAccount);
                showInfo("Erfolg", "Account wurde gelöscht.");
            } catch (AccountDoesNotExistException e) {
                showError("Fehler", "Account existiert nicht!");
            } catch (IOException e) {
                showError("Fehler beim Löschen", e.getMessage());
            }
        }
    }

    private void openAccountView(String accountName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ACCOUNT_VIEW_PATH));
            Scene scene = new Scene(loader.load());

            // Controller holen und Account übergeben
            AccountViewController controller = loader.getController();
            controller.initData(accountName, privateBank, (Stage) accountListView.getScene().getWindow());

            Stage stage = (Stage) accountListView.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            showError("Fehler beim Laden", e.getMessage());
            e.printStackTrace();
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