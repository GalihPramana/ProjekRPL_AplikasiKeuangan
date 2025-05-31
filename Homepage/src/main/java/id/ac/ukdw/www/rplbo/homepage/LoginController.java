package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password tidak boleh kosong.");
            return;
        }

        if (loginUser(username, password)) {
            try {
                Parent homeRoot = FXMLLoader.load(getClass().getResource("homepage-view.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(homeRoot));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Gagal memuat halaman utama.");
            }
        } else {
            errorLabel.setText("Username atau password salah.");
        }
    }

    private boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // asumsi password disimpan dalam teks biasa

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // return true jika user ditemukan

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Terjadi kesalahan koneksi.");
            return false;
        }
    }
}
