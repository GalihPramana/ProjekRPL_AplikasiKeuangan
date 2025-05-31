package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class UtangPiutangController {

    @FXML
    private ChoiceBox<String> cbStatus;

    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTransaction;

    @FXML
    private TextField tfKepadaSiapa;

    @FXML
    private DatePicker dpTanggal;

    @FXML
    private TextField tfJumlahUtang;

    @FXML
    private TextField tfFilter;

    @FXML
    private TableView<UtangPiutang> table;

    @FXML
    private TableColumn<UtangPiutang, String> colKepadaSiapa;

    @FXML
    private TableColumn<UtangPiutang, String> colTanggal;

    @FXML
    private TableColumn<UtangPiutang, Integer> colJumlahUtang;

    @FXML
    private TableColumn<UtangPiutang, String> colStatus;

    private ObservableList<UtangPiutang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colKepadaSiapa.setCellValueFactory(new PropertyValueFactory<>("kepadaSiapa"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlahUtang.setCellValueFactory(new PropertyValueFactory<>("jumlahUtang"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.setEditable(true);
        cbStatus.getItems().addAll("Lunas", "Belum Lunas");

        loadDataFromDatabase();
    }

    @FXML
    public void handleTambahHutang() {
        tambahTransaksi("Hutang");
    }

    @FXML
    public void handleTambahPiutang() {
        tambahTransaksi("Piutang");
    }

    private void tambahTransaksi(String jenis) {
        String kepada = tfKepadaSiapa.getText();
        LocalDate tanggal = dpTanggal.getValue();
        int jumlah;

        try {
            jumlah = Integer.parseInt(tfJumlahUtang.getText());
        } catch (NumberFormatException e) {
            showAlert("Jumlah tidak valid");
            return;
        }

        String status = cbStatus.getValue();
        if (kepada.isEmpty() || tanggal == null || status == null || status.isEmpty()) {
            showAlert("Lengkapi semua data");
            return;
        }

        if (jenis.equals("Hutang")) {
            jumlah = -Math.abs(jumlah);
        } else {
            jumlah = Math.abs(jumlah);
        }

        String sql = "INSERT INTO utang_piutang (username, kepada, tanggal, nominal, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "User");
            pstmt.setString(2, kepada);
            pstmt.setString(3, tanggal.toString());
            pstmt.setInt(4, jumlah);
            pstmt.setString(5, status);
            pstmt.executeUpdate();

            data.add(new UtangPiutang("User", kepada, tanggal.toString(), jumlah, status));
            clearForm();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menyimpan ke database: " + e.getMessage());
        }
    }

    @FXML
    public void handleHapus() {
        UtangPiutang selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String sql = "DELETE FROM utang_piutang WHERE username = ? AND kepada = ? AND tanggal = ? AND nominal = ? AND status = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, selected.getNama());
                pstmt.setString(2, selected.getKepadaSiapa());
                pstmt.setString(3, selected.getTanggal());
                pstmt.setInt(4, selected.getJumlahUtang());
                pstmt.setString(5, selected.getStatus());

                pstmt.executeUpdate();
                data.remove(selected);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Gagal menghapus data.");
            }
        }
    }

    @FXML
    public void handleFilter() {
        String filter = tfFilter.getText().toLowerCase();
        if (filter.isEmpty()) {
            table.setItems(data);
            return;
        }

        ObservableList<UtangPiutang> filtered = FXCollections.observableArrayList();
        for (UtangPiutang t : data) {
            if (t.getKepadaSiapa().toLowerCase().contains(filter)) {
                filtered.add(t);
            }
        }
        table.setItems(filtered);
    }

    private void clearForm() {
        tfKepadaSiapa.clear();
        tfJumlahUtang.clear();
        dpTanggal.setValue(null);
        cbStatus.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDataFromDatabase() {
        data.clear();
        String sql = "SELECT * FROM utang_piutang";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new UtangPiutang(
                        rs.getString("username"),
                        rs.getString("kepada"),
                        rs.getString("tanggal"),
                        rs.getInt("nominal"),
                        rs.getString("status")
                ));
            }
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onHomeClick(ActionEvent actionEvent) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("homepage-view.fxml"));
            Scene scene = btnHome.getScene();
            scene.setRoot(homeRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onTransactionClick(ActionEvent actionEvent) {
        try {
            Parent transactionRoot = FXMLLoader.load(getClass().getResource("transaction-view.fxml"));
            Scene scene = btnTransaction.getScene();
            scene.setRoot(transactionRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDebtClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onKategoriClick(ActionEvent event) {
        try {
            Parent kategoriRoot = FXMLLoader.load(getClass().getResource("kategori-view.fxml"));
            Scene scene = btnTransaction.getScene();
            scene.setRoot(kategoriRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLogoutClick(ActionEvent actionEvent) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = btnLogout.getScene();
            scene.setRoot(loginRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}