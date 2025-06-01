package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import id.ac.ukdw.www.rplbo.homepage.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.util.function.Predicate;

public class KelolaKategoriController {

    @FXML private Button btnDebt, btnHome, btnLogout, btnTransaction, btnKategori;
    @FXML private Button btnTambahKategori, clearButton, deleteButton, updateButton;
    @FXML private TableColumn<KelolaKategori, String> kategoriColumn;
    @FXML private Label lblDate, lblTotalKategori, lblWelcome;
    @FXML private TableView<KelolaKategori> tableViewKategori;
    @FXML private TextField txtFilter, txtNamaKategori;

    private final ObservableList<KelolaKategori> dataKategori = FXCollections.observableArrayList();
    private FilteredList<KelolaKategori> filteredKategoriList;
    private Connection connection;

    public void initialize() {
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        filteredKategoriList = new FilteredList<>(dataKategori, p -> true);
        tableViewKategori.setItems(filteredKategoriList);

        txtFilter.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredKategoriList.setPredicate(createPredicate(newVal));
        });

        java.time.format.DateTimeFormatter displayDateFormatter = java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy");
        lblDate.setText(LocalDate.now().format(displayDateFormatter));

        getConnection();
        loadAllKategori();
        lblTotalKategori.setText("Total: " + dataKategori.size());

        tableViewKategori.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNamaKategori.setText(newVal.getNamaKategori());
            }
        });
    }

    private Connection getConnection() {
        if (connection == null) {
            connection = DBConnection.getConnection();
        }
        return connection;
    }

    private void loadAllKategori() {
        dataKategori.clear();
        String username = (String) SessionManager.get("user");
        if (username == null || username.isEmpty()) return;

        String query = "SELECT nama_kategori FROM kategori WHERE username = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dataKategori.add(new KelolaKategori(rs.getString("nama_kategori")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableViewKategori.setItems(filteredKategoriList);
        tableViewKategori.refresh();
        lblTotalKategori.setText("Total: " + dataKategori.size());
    }

    private Predicate<KelolaKategori> createPredicate(String searchText) {
        return kategori -> searchText == null || searchText.isEmpty() || kategori.getNamaKategori().toLowerCase().contains(searchText.toLowerCase());
    }

    @FXML void onClearClick(ActionEvent event) {
        tableViewKategori.getSelectionModel().clearSelection();
        txtFilter.clear();
        txtNamaKategori.clear();
        txtNamaKategori.requestFocus();
    }

    @FXML void onDeleteClick(ActionEvent event) {
        KelolaKategori selected = tableViewKategori.getSelectionModel().getSelectedItem();
        if (selected != null && deleteKategori(selected.getNamaKategori())) {
            dataKategori.remove(selected);
            lblTotalKategori.setText("Total: " + dataKategori.size());
            tableViewKategori.refresh();
        } else {
            showAlert("Gagal menghapus kategori!");
        }
    }

    @FXML void onTambahkanClick(ActionEvent event) {
        String nama = txtNamaKategori.getText().trim();
        String username = (String) SessionManager.get("user");
        if (nama.isEmpty()) {
            showAlert("Nama kategori tidak boleh kosong!");
            return;
        }
        if (username == null || username.isEmpty()) {
            showAlert("User belum login!");
            return;
        }

        if (addKategori(nama)) {
            dataKategori.add(new KelolaKategori(nama));
            tableViewKategori.setItems(filteredKategoriList);
            tableViewKategori.scrollTo(dataKategori.size() - 1);
            tableViewKategori.refresh();
            lblTotalKategori.setText("Total: " + dataKategori.size());
            txtFilter.clear();
            txtNamaKategori.clear();
            txtNamaKategori.requestFocus();
        } else {
            showAlert("Kategori gagal ditambahkan atau sudah ada!");
        }
    }

    @FXML void onUpdateClick(ActionEvent event) {
        KelolaKategori selected = tableViewKategori.getSelectionModel().getSelectedItem();
        String newNama = txtNamaKategori.getText().trim();
        if (selected == null || newNama.isEmpty()) {
            showAlert("Pilih dan isi nama kategori yang ingin diperbarui!");
            return;
        }
        String oldNama = selected.getNamaKategori();
        if (updateKategori(oldNama, newNama)) {
            selected.setNamaKategori(newNama);
            tableViewKategori.refresh();
            lblTotalKategori.setText("Total: " + dataKategori.size());
        } else {
            showAlert("Gagal memperbarui nama kategori!");
        }
    }

    private boolean addKategori(String namaKategori) {
        String username = (String) SessionManager.get("user");
        String sql = "INSERT INTO kategori(nama_kategori, username) VALUES (?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean updateKategori(String oldNama, String newNama) {
        String username = (String) SessionManager.get("user");
        String sql = "UPDATE kategori SET nama_kategori = ? WHERE nama_kategori = ? AND username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, newNama);
            ps.setString(2, oldNama);
            ps.setString(3, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean deleteKategori(String namaKategori) {
        String username = (String) SessionManager.get("user");
        String sql = "DELETE FROM kategori WHERE nama_kategori = ? AND username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML public void onHomeClick(ActionEvent e) { changeScene("homepage-view.fxml", btnHome); }
    @FXML public void onTransactionClick(ActionEvent e) { changeScene("transaction-view.fxml", btnTransaction); }
    @FXML public void onDebtClick(ActionEvent e) { changeScene("UtangPiutang.fxml", btnDebt); }
    @FXML public void onKategoriClick(ActionEvent e) {}
    @FXML public void onLogoutClick(ActionEvent e) { changeScene("Login.fxml", btnLogout); }

    private void changeScene(String fxml, Button btn) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = btn.getScene();
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
