package id.ac.ukdw.www.rplbo.catatankeuangan;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (UserManager.isValidUser(username, password)) {
            errorLabel.setText("");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
                BorderPane mainView = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(mainView);
                stage.setScene(scene);
                stage.setTitle("Aplikasi Keuangan - Dashboard");

            } catch (IOException e) {
                errorLabel.setText("Gagal memuat halaman utama.");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Username atau password salah.");
        }
    }
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            BorderPane registerView = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(registerView));
            stage.setTitle("Register - Aplikasi Keuangan");
        } catch (IOException e) {
            errorLabel.setText("Gagal membuka halaman register.");
            e.printStackTrace();
        }
    }
}
