package id.ac.ukdw.www.rplbo.homepage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class TransactionController {

    @FXML private Button btnDebt;
    @FXML private Button btnHome;
    @FXML private Button btnLogout;
    @FXML private Button btnTransaction;

    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    @FXML private Button pemasukkanButton;
    @FXML private Button pengeluaranButton;

    @FXML private Label lblDate;
    @FXML private Label lblWelcome;
    @FXML private Label totalSaldoLabel;

    @FXML private TextField idTransaksiField;
    @FXML private TextField sourceNameField;
    @FXML private TextField jumlahField;
    @FXML private TextField descriptionField;
    @FXML private TextField tanggalField;

    @FXML private TableView<Transaction> tableView;
    @FXML private TableColumn<Transaction, String> idColumn;
    @FXML private TableColumn<Transaction, String> sourceNameColumn;
    @FXML private TableColumn<Transaction, Integer> jumlahColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> tanggalColumn;

    // daftar in-memory
    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1) setup kolom
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        sourceNameColumn.setCellValueFactory(new PropertyValueFactory<>("sourceName"));
        jumlahColumn.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        jumlahColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Transaction, Integer> call(TableColumn<Transaction, Integer> param) {
                return new TableCell<>() {
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
                };
            }
        });

        // 3) bind list ke tabel
        tableView.setItems(transactionList);
        tableView.getSortOrder().add(idColumn);

        // 4) saat baris dipilih, isi form
        tableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) populateFields(newSel);
                });
    }

    @FXML
    void onClearClick(ActionEvent event) {
        handleClear();
    }

    @FXML
    void onDeleteClick(ActionEvent event) {
        handleDelete();
    }

    @FXML
    void onPemasukkanClick(ActionEvent event) {
        handlePemasukan(true);
    }

    @FXML
    void onPengeluaranClick(ActionEvent event) {
        handlePemasukan(false);
    }

    @FXML
    void onDebtClick(ActionEvent event) {
        // Ke Halaman Debt
    }

    @FXML
    void onHomeClick(ActionEvent event) {
        try {
            Parent transactionRoot = FXMLLoader.load(getClass().getResource("homepage-view.fxml"));
            Scene scene = btnTransaction.getScene();
            scene.setRoot(transactionRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        // Logout
    }

    @FXML
    void onTransactionClick(ActionEvent event) {
        // Sudah berada di halaman
    }


    /**
     * @param isIncome true = pemasukan, false = pengeluaran
     */
    private void handlePemasukan(boolean isIncome) {
        String id         = idTransaksiField.getText().trim();
        String sumber     = sourceNameField.getText().trim();
        String jmlText    = jumlahField.getText().trim();
        String desc       = descriptionField.getText().trim();
        String tanggal    = tanggalField.getText().trim();

        // validasi
        if (id.isEmpty() || sumber.isEmpty() || jmlText.isEmpty() || tanggal.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "ID, Kategori, Nominal, dan Tanggal harus diisi.");
            return;
        }

        int jml;
        try {
            jml = Integer.parseInt(jmlText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR,"Input Error","Nominal harus berupa angka");
            return;
        }

        if (jml <= 0) {
            showAlert(Alert.AlertType.ERROR,"Input Error","Nominal harus lebih besar dari 0.");
            return;
        }

        // jika pengeluaran, pakai sign negatif
        if (!isIncome) {
            jml = -Math.abs(jml);
        }

        // update atau tambah baru
        Transaction existing = findTransactionById(id);
        if (existing != null) {
            existing.setSourceName(sumber);
            existing.setJumlah(jml);
            existing.setDescription(desc);
            existing.setTanggal(tanggal);
            tableView.refresh();
        } else {
            transactionList.add(new Transaction(id, sumber, jml, desc, tanggal));
        }

        handleClear();
        updateTotalSaldo();
    }

    private void handleDelete() {
        Transaction sel = tableView.getSelectionModel().getSelectedItem();
        if (sel != null) {
            transactionList.remove(sel);
            handleClear();
            updateTotalSaldo();
        } else {
            showAlert(Alert.AlertType.WARNING,"Delete Error","Pilih transaksi yang ingin dihapus.");
        }
    }

    private void handleClear() {
        idTransaksiField.clear();
        sourceNameField.clear();
        jumlahField.clear();
        descriptionField.clear();
        tanggalField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void populateFields(Transaction t) {
        idTransaksiField.setText(t.getIdTransaksi());
        sourceNameField.setText(t.getSourceName());
        jumlahField.setText(String.valueOf(t.getJumlah()));
        descriptionField.setText(t.getDescription());
        tanggalField.setText(t.getTanggal());
    }

    private Transaction findTransactionById(String id) {
        for (Transaction t : transactionList) {
            if (t.getIdTransaksi().equals(id)) return t;
        }
        return null;
    }

    private void updateTotalSaldo() {
        int total = transactionList.stream()
                .mapToInt(Transaction::getJumlah)
                .sum();
        totalSaldoLabel.setText(
                String.format("Total Saldo : ( RP %,d )", total).replace(',', '.')
        );
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}