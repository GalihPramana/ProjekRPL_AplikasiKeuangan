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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionController {

    @FXML private Button btnDebt, btnHome, btnLogout, btnTransaction, btnKategori, logButton;
    @FXML private Button clearButton, deleteButton, pemasukkanButton, pengeluaranButton, updateButton;
    @FXML private Label lblDate, lblWelcome, totalSaldoLabel;
    @FXML private ComboBox<String> kategoriComboBox, filterKategoriComboBox;
    @FXML private TextField jumlahField, descriptionField, batasBulananField;
    @FXML private DatePicker tanggalPicker;
    @FXML private TableView<Transaction> tableView;
    @FXML private TableColumn<Transaction, String> sourceNameColumn, descriptionColumn, tanggalColumn;
    @FXML private TableColumn<Transaction, Integer> jumlahColumn;

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private final FilteredList<Transaction> filteredTransactionList = new FilteredList<>(transactionList, p -> true);
    private Transaction selectedTransaction;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
    private int batasBulanan = 0;

    @FXML
    public void initialize() {
        lblWelcome.setText("Selamat datang, " + SessionManager.get("user") + "!");

        ObservableList<String> kategoriOptions = loadKategoriFromDatabase();
        kategoriComboBox.setItems(kategoriOptions);

        ObservableList<String> filterKategoriOptions = FXCollections.observableArrayList("Semua");
        filterKategoriOptions.addAll(kategoriOptions);
        filterKategoriComboBox.setItems(filterKategoriOptions);
        filterKategoriComboBox.setValue("Semua");
        filterKategoriComboBox.setOnAction(this::handleFilterKategori);

        sourceNameColumn.setCellValueFactory(new PropertyValueFactory<>("sourceName"));
        jumlahColumn.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        jumlahColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d", item).replace(',', '.'));
                    setTextFill(item < 0 ? Color.RED : Color.GREEN);
                }
            }
        });

        tableView.setItems(filteredTransactionList);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedTransaction = newSel;
            if (newSel != null) populateFields(newSel);
            toggleUpdateButtonState(newSel != null);
        });

        loadTransaksiFromDatabase();
        toggleUpdateButtonState(false);
        updateTotalSaldo();
        lblDate.setText(LocalDate.now().format(displayDateFormatter));
    }

    @FXML
    void handleFilterKategori(ActionEvent event) {
        String selectedKategori = filterKategoriComboBox.getValue();
        if ("Semua".equals(selectedKategori)) {
            filteredTransactionList.setPredicate(p -> true);
        } else {
            filteredTransactionList.setPredicate(p -> p.getSourceName().equals(selectedKategori));
        }
        updateTotalSaldo();
    }

    private ObservableList<String> loadKategoriFromDatabase() {
        ObservableList<String> kategoriList = FXCollections.observableArrayList();
        String username = (String) SessionManager.get("user");
        String sql = "SELECT nama_kategori FROM kategori WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                kategoriList.add(rs.getString("nama_kategori"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategoriList;
    }

    private void loadTransaksiFromDatabase() {
        transactionList.clear();
        String username = (String) SessionManager.get("user");
        String sql = "SELECT * FROM transaksi WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactionList.add(new Transaction(
                        String.valueOf(rs.getInt("transaksi_id")),
                        rs.getString("kategori"),
                        rs.getInt("nominal"),
                        rs.getString("description"),
                        rs.getString("tanggal")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML void onClearClick(ActionEvent event) { handleClear(); }
    @FXML void onDeleteClick(ActionEvent event) { deleteTransaksi(); }
    @FXML void onPemasukkanClick(ActionEvent event) { insertTransaksi(true); }
    @FXML void onPengeluaranClick(ActionEvent event) { insertTransaksi(false); }
    @FXML void onUpdateClick(ActionEvent event) { updateTransaksi(); }
    @FXML void onDebtClick(ActionEvent event) { changeScene("UtangPiutang.fxml", btnDebt); }
    @FXML void onHomeClick(ActionEvent event) { changeScene("homepage-view.fxml", btnHome); }
    @FXML void onKategoriClick(ActionEvent event) { changeScene("kategori-view.fxml", btnKategori); }
    @FXML void onLogoutClick(ActionEvent event) { changeScene("Login.fxml", btnLogout); }
    @FXML void onTransactionClick(ActionEvent event) {}
    @FXML void onlogClick(ActionEvent event) { changeScene("Log.fxml", logButton); }

    private void insertTransaksi(boolean isIncome) {
        String username = (String) SessionManager.get("user");
        String sumber = kategoriComboBox.getValue();
        String jmlText = jumlahField.getText().trim();
        String desc = descriptionField.getText().trim();
        LocalDate tanggal = tanggalPicker.getValue();

        if (sumber == null || jmlText.isEmpty() || tanggal == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Kategori, Nominal, dan Tanggal harus diisi.");
            return;
        }

        int jml;
        try {
            jml = Integer.parseInt(jmlText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Nominal harus berupa angka.");
            return;
        }

        if (!isIncome) {
            jml = -Math.abs(jml);
        }

        String sql = "INSERT INTO transaksi (username, kategori, nominal, tanggal, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, sumber);
            ps.setInt(3, jml);
            ps.setString(4, tanggal.format(formatter));
            ps.setString(5, desc);
            ps.executeUpdate();

            loadTransaksiFromDatabase();
            handleClear();
            updateTotalSaldo();
            updateBatasBulanan();
            cekBatasPengeluaranBulanan();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTransaksi() {
        if (selectedTransaction == null) return;
        String sumber = kategoriComboBox.getValue();
        String jmlText = jumlahField.getText().trim();
        String desc = descriptionField.getText().trim();
        LocalDate tanggal = tanggalPicker.getValue();

        int jml;
        try {
            jml = Integer.parseInt(jmlText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Nominal harus angka.");
            return;
        }

        String sql = "UPDATE transaksi SET kategori = ?, nominal = ?, tanggal = ?, description = ? WHERE transaksi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sumber);
            ps.setInt(2, jml);
            ps.setString(3, tanggal.format(formatter));
            ps.setString(4, desc);
            ps.setInt(5, Integer.parseInt(selectedTransaction.getIdTransaksi()));
            ps.executeUpdate();

            loadTransaksiFromDatabase();
            handleClear();
            updateTotalSaldo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTransaksi() {
        if (selectedTransaction == null) return;
        String sql = "DELETE FROM transaksi WHERE transaksi_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(selectedTransaction.getIdTransaksi()));
            ps.executeUpdate();
            loadTransaksiFromDatabase();
            handleClear();
            updateTotalSaldo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleClear() {
        kategoriComboBox.getSelectionModel().clearSelection();
        jumlahField.clear();
        descriptionField.clear();
        tanggalPicker.setValue(null);
        tableView.getSelectionModel().clearSelection();
        selectedTransaction = null;
        toggleUpdateButtonState(false);
    }

    private void populateFields(Transaction t) {
        kategoriComboBox.setValue(t.getSourceName());
        jumlahField.setText(String.valueOf(t.getJumlah()));
        descriptionField.setText(t.getDescription());
        tanggalPicker.setValue(LocalDate.parse(t.getTanggal(), formatter));
    }

    private void updateTotalSaldo() {
        int total = filteredTransactionList.stream().mapToInt(Transaction::getJumlah).sum();
        totalSaldoLabel.setText(String.format("Total Saldo : ( RP %,d )", total).replace(',', '.'));
    }

    private void updateBatasBulanan() {
        String input = batasBulananField.getText().trim();
        if (!input.isEmpty()) {
            try {
                batasBulanan = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Batas harus berupa angka.");
            }
        }
    }

    private void cekBatasPengeluaranBulanan() {
        LocalDate now = LocalDate.now();
        int totalPengeluaran = transactionList.stream()
                .filter(t -> {
                    LocalDate tgl = LocalDate.parse(t.getTanggal(), formatter);
                    return tgl.getMonth().equals(now.getMonth()) && tgl.getYear() == now.getYear() && t.getJumlah() < 0;
                })
                .mapToInt(Transaction::getJumlah)
                .sum();

        if (Math.abs(totalPengeluaran) >= batasBulanan && batasBulanan > 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan Pengeluaran!",
                    "Pengeluaran bulan ini telah melebihi batas Rp " + String.format("%,d", batasBulanan).replace(",", "."));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void changeScene(String fxmlFile, Button sourceButton) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = sourceButton.getScene();
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toggleUpdateButtonState(boolean enable) {
        if (updateButton != null) {
            updateButton.setDisable(!enable);
        }
    }
}
