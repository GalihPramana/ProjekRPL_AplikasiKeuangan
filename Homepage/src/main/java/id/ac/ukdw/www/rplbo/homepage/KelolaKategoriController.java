package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;

public class KelolaKategoriController {
    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnKategori;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTambahKategori;

    @FXML
    private Button btnTransaction;

    @FXML
    private Button clearButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<KelolaKategori, String> kategoriColumn;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTotalKategori;

    @FXML
    private Label lblWelcome;

    @FXML
    private TableView<KelolaKategori> tableViewKategori;

    @FXML
    private TextField txtFilter;

    @FXML
    private TextField txtNamaKategori;

    @FXML
    private Button updateButton;


    // Tempat data dan filtering data
    private ObservableList<KelolaKategori> dataKategori = FXCollections.observableArrayList();
    private FilteredList<KelolaKategori> filteredKategoriList;

    //Koneksi ke DB
    private Connection connection;

    public void initialize() {
        //Setup kolom tabel
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));

        // Bungkus dataKategori dengan FilteredList agar bisa di filter
        filteredKategoriList = new FilteredList<>(dataKategori, p -> true);
        tableViewKategori.setItems(filteredKategoriList);

        // Menggunakan listener pada txtFilter
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredKategoriList.setPredicate(createPredicate(newValue));
        });

        // Menampilkan tanggal di lblDate
        java.time.format.DateTimeFormatter displayDateFormatter =
                java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy");
        lblDate.setText(java.time.LocalDate.now().format(displayDateFormatter));

        // Inisiasi koneksi ke tabel database
        getConnection();
        loadAllKategori();

        // Menampilkan jumlah total kategori yang pernah dibuat
        lblTotalKategori.setText("Total: " + dataKategori.size());

        // Saat baris tabel dipilih menampilkan txtNamaKategori dengan nilai yang dipilih
        tableViewKategori.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtNamaKategori.setText(newValue.getNamaKategori());
            }
        });

    }

    //Method koneksi database
    private Connection getConnection() {
        if (connection == null) {
            connection = DBConnection.getConnection();
        }
        return connection;
    }

    //Method close koneksi database
    private void closeConnection() {
        DBConnection.closeConnection();
    }

    // Mengambil data dari DB ke list dataKategori
    private void loadAllKategori() {
        dataKategori.clear();
        String query = "SELECT nama_kategori FROM kategori";
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String namaKategori = rs.getString("nama_kategori");
                dataKategori.add(new KelolaKategori(namaKategori));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lblTotalKategori.setText("Total: " + dataKategori.size());
    }

    // Predicate untuk filter dataKategori
    private Predicate<KelolaKategori> createPredicate(String searchText) {
        return kategori -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return kategori.getNamaKategori().toLowerCase().contains(searchText.toLowerCase());
        };
    }

    // Event and Button
    @FXML
    void onClearClick(ActionEvent event) {
        tableViewKategori.getSelectionModel().clearSelection();
        txtFilter.clear();
        txtNamaKategori.clear();
        txtNamaKategori.requestFocus();
    }

    @FXML
    void onDeleteClick(ActionEvent event) {
        KelolaKategori selected = tableViewKategori.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (deleteKategori(selected.getNamaKategori())) {
                dataKategori.remove(selected);
                lblTotalKategori.setText("Total: " + dataKategori.size());
            }
        } else {
            showAlert("Gagal menghapus kategori!");
        }
    }

    @FXML
    void onTambahkanClick(ActionEvent event) {
        String txtKategori = txtNamaKategori.getText().trim();
        if (txtKategori.isEmpty()) {
            showAlert("Nama kategori tidak boleh kosong!");
        } else {
            if (addKategori(txtKategori)){
                dataKategori.add(new KelolaKategori(txtKategori));
                lblTotalKategori.setText("Total: " + dataKategori.size());
                txtNamaKategori.clear();
                txtNamaKategori.requestFocus();
            } else {
                showAlert("Kategori gagal ditambahkan atau sudah pernah ada!");
            }
        }
    }

    @FXML
    void onUpdateClick(ActionEvent event) {
        KelolaKategori selected = tableViewKategori.getSelectionModel().getSelectedItem();
        String newNamaKategori = txtNamaKategori.getText().trim();
        if (selected == null) {
            showAlert("Pilih nama kategori yang ingin diperbaharui!");
            return;
        }
        if (newNamaKategori.isEmpty()) {
            showAlert("Nama kategori tidak boleh kosong!");
            return;
        }

        String oldNamaKategori = selected.getNamaKategori();
        if(updateKategori(oldNamaKategori,newNamaKategori)) {
            selected.setNamaKategori(newNamaKategori);
            tableViewKategori.refresh();
            lblTotalKategori.setText("Total: " + dataKategori.size());
        } else {
            showAlert("Gagal memperbaharui nama kategori!");
        }
    }

    // Fungsi CRUD
    private boolean addKategori(String namaKategori) {
        String sql = "INSERT INTO kategori(nama_kategori) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean updateKategori(String oldNamaKategori, String newNamaKategori) {
        String sql = "UPDATE kategori SET nama_kategori = ? WHERE nama_kategori = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)){
            ps.setString(1, oldNamaKategori);
            ps.setString(2, newNamaKategori);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean deleteKategori(String namaKategori) {
        String sql = "DELETE FROM kategori WHERE nama_kategori = ?";
        try(PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        try {
            Parent debtRoot = FXMLLoader.load(getClass().getResource("UtangPiutang.fxml"));
            Scene scene = btnDebt.getScene();
            scene.setRoot(debtRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onKategoriClick(ActionEvent event) {
        // Sudah berada di halaman ini
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

