package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {
    public Button registerButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private TextField passwordVisibleField;
    @FXML private TextField confirmPasswordVisibleField;
    @FXML private CheckBox showPasswordCheckbox;


    @FXML
    public void initialize() {
        // Sinkronisasi isi password
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        showPasswordCheckbox.setOnAction(e -> {
            boolean show = showPasswordCheckbox.isSelected();

            //menampilkan password yang baru dibuat
            passwordVisibleField.setVisible(show);
            passwordVisibleField.setManaged(show);
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);

            // menampilkan konfirmasi password
            confirmPasswordVisibleField.setVisible(show);
            confirmPasswordVisibleField.setManaged(show);
            confirmPasswordField.setVisible(!show);
            confirmPasswordField.setManaged(!show);
        });
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Field tidak boleh kosong.");
        } else if (!password.equals(confirm)) {
            messageLabel.setText("Password tidak cocok.");
        } else if (usernameExists(username)) {
            messageLabel.setText("Username sudah terdaftar.");
        } else {
            registerUser(username, password);
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

    public void registerUser(String username, String password) {
        Connection conn = DBConnection.getConnection();
        try {
            String insertSql = "INSERT INTO users(username, password) VALUES(?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            System.out.println("Registrasi berhasil.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Terjadi kesalahan saat registrasi.");
        }
    }

    private boolean usernameExists(String username) {
        Connection conn = DBConnection.getConnection();
        try {
            String checkSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
