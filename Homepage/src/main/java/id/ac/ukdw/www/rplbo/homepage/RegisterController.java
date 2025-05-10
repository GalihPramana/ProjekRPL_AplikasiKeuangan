package id.ac.ukdw.www.rplbo.homepage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    public Button registerButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Field tidak boleh kosong.");
        } else if (!password.equals(confirm)) {
            messageLabel.setText("Password tidak cocok.");
        } else if (UserManager.userExists(username)) {
            messageLabel.setText("Username sudah terdaftar.");
        } else {
            UserManager.addUser(username, password);  // Nyimpen data pengguna ke file lokal
            messageLabel.setText("Registrasi berhasil! Silakan login.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        BorderPane loginView = loader.load();
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(loginView));
        stage.setTitle("Login Aplikasi Keuangan");
    }


}
