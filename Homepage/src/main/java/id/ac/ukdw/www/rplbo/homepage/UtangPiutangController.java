package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import id.ac.ukdw.www.rplbo.homepage.util.SessionManager;
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
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class UtangPiutangController {

    @FXML private ChoiceBox<String> cbStatus;
    @FXML private Button btnDebt, btnHome, btnLogout, btnTransaction, btnKategori, btnEdit;
    @FXML private TextField tfKepadaSiapa, tfJumlahUtang, tfFilter;
    @FXML private DatePicker dpTanggal;
    @FXML private TableView<UtangPiutang> table;
    @FXML private TableColumn<UtangPiutang, String> colKepadaSiapa, colTanggal, colStatus;
    @FXML private TableColumn<UtangPiutang, Integer> colJumlahUtang;

    private final ObservableList<UtangPiutang> data = FXCollections.observableArrayList();
    private UtangPiutang selectedForEdit = null;

    @FXML
    public void initialize() {
        colKepadaSiapa.setCellValueFactory(new PropertyValueFactory<>("kepadaSiapa"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlahUtang.setCellValueFactory(new PropertyValueFactory<>("jumlahUtang"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cbStatus.getItems().addAll("Lunas", "Belum Lunas");
        loadDataFromDatabase();

        table.setOnMouseClicked((MouseEvent event) -> {
            UtangPiutang selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                tfKepadaSiapa.setText(selected.getKepadaSiapa());
                tfJumlahUtang.setText(String.valueOf(Math.abs(selected.getJumlahUtang())));
                dpTanggal.setValue(LocalDate.parse(selected.getTanggal()));
                cbStatus.setValue(selected.getStatus());
                selectedForEdit = selected;
            }
        });
    }

    @FXML
    public void handleTambahHutang() {
        tambahTransaksi(false);
    }

    @FXML
    public void handleTambahPiutang() {
        tambahTransaksi(true);
    }

    private void tambahTransaksi(boolean isPiutang) {
        String username = (String) SessionManager.get("user");
        String kepada = tfKepadaSiapa.getText().trim();
        LocalDate tanggal = dpTanggal.getValue();
        String status = cbStatus.getValue();

        if (kepada.isEmpty() || tanggal == null || status == null || status.isEmpty()) {
            showAlert("Lengkapi semua data terlebih dahulu.");
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(tfJumlahUtang.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Jumlah harus berupa angka.");
            return;
        }

        if (!isPiutang) {
            jumlah = -Math.abs(jumlah);
        }

        String sql = "INSERT INTO utang_piutang (username, kepada, tanggal, nominal, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, kepada);
            pstmt.setString(3, tanggal.toString());
            pstmt.setInt(4, jumlah);
            pstmt.setString(5, status);
            pstmt.executeUpdate();

            data.add(new UtangPiutang(username, kepada, tanggal.toString(), jumlah, status));
            table.setItems(data);
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menyimpan ke database: " + e.getMessage());
        }
    }

    @FXML
    public void handleEdit() {
        if (selectedForEdit == null) {
            showAlert("Pilih data yang ingin diedit.");
            return;
        }

        String kepada = tfKepadaSiapa.getText().trim();
        LocalDate tanggal = dpTanggal.getValue();
        String status = cbStatus.getValue();

        if (kepada.isEmpty() || tanggal == null || status == null || status.isEmpty()) {
            showAlert("Lengkapi semua data terlebih dahulu.");
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(tfJumlahUtang.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Jumlah harus berupa angka.");
            return;
        }

        int nominalBaru = selectedForEdit.getJumlahUtang() < 0 ? -Math.abs(jumlah) : Math.abs(jumlah);
        String username = (String) SessionManager.get("user");

        String sql = "UPDATE utang_piutang SET kepada = ?, tanggal = ?, nominal = ?, status = ? " +
                "WHERE username = ? AND kepada = ? AND tanggal = ? AND nominal = ? AND status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kepada);
            pstmt.setString(2, tanggal.toString());
            pstmt.setInt(3, nominalBaru);
            pstmt.setString(4, status);

            pstmt.setString(5, selectedForEdit.getNama());
            pstmt.setString(6, selectedForEdit.getKepadaSiapa());
            pstmt.setString(7, selectedForEdit.getTanggal());
            pstmt.setInt(8, selectedForEdit.getJumlahUtang());
            pstmt.setString(9, selectedForEdit.getStatus());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Data berhasil diperbarui.");
                loadDataFromDatabase();
                clearForm();
                selectedForEdit = null;
            } else {
                showAlert("Gagal memperbarui data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal mengupdate data: " + e.getMessage());
        }
    }

    @FXML
    public void handleHapus() {
        UtangPiutang selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

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
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menghapus data: " + e.getMessage());
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
        String username = (String) SessionManager.get("user");
        String sql = "SELECT * FROM utang_piutang WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

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
            showAlert("Gagal memuat data: " + e.getMessage());
        }
    }

    @FXML public void onHomeClick(ActionEvent event) { changeScene("homepage-view.fxml", btnHome); }
    @FXML public void onTransactionClick(ActionEvent event) { changeScene("transaction-view.fxml", btnTransaction); }
    @FXML public void onDebtClick(ActionEvent event) {}
    @FXML public void onKategoriClick(ActionEvent event) { changeScene("kategori-view.fxml", btnKategori); }
    @FXML public void onLogoutClick(ActionEvent event) { changeScene("Login.fxml", btnLogout); }

    private void changeScene(String fxml, Button button) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = button.getScene();
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

