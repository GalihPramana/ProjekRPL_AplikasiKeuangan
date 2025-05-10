package id.ac.ukdw.www.rplbo.homepage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private Button loginButton;

    @FXML
    private Button registerButton;


    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (UserManager.isValidUser(username, password)) {
            errorLabel.setText("");
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("homepage-view.fxml"));
                Scene scene = loginButton.getScene();

                scene.setRoot(loginRoot);
                Stage stage = (Stage) scene.getWindow();
                stage.setScene(scene);
                stage.setTitle("Aplikasi Keuangan");

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
