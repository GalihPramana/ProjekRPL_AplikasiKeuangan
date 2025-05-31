package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import id.ac.ukdw.www.rplbo.homepage.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    private TextField passwordVisibleField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        // Sinkronisasi isi password
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        showPasswordCheckBox.setOnAction(event -> {
            boolean show = showPasswordCheckBox.isSelected();
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);
            passwordVisibleField.setVisible(show);
            passwordVisibleField.setManaged(show);
        });
    }



    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (loginUser(username, password)) {
            SessionManager.set("user", username);
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

    public boolean loginUser(String username, String password) {
        Connection conn = DBConnection.getConnection();
        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login berhasil.");
                return true;
            } else {
                System.out.println("Login gagal: user tidak ditemukan.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
