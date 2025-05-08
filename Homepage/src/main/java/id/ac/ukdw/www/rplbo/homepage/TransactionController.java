package id.ac.ukdw.www.rplbo.homepage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransactionController {

    @FXML
    private TextField idTransaksiField;

    @FXML
    private TextField tanggalField;

    @FXML
    private TextField jumlahField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<Transaction> tableView;

    @FXML
    private TableColumn<Transaction, String> idColumn;

    @FXML
    private TableColumn<Transaction, String> tanggalColumn;

    @FXML
    private TableColumn<Transaction, String> jumlahColumn;

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set cell value factories untuk kolom tabel
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        jumlahColumn.setCellValueFactory(new PropertyValueFactory<>("jumlah"));

        // Set data ke TableView
        tableView.setItems(transactionList);

        // Event handler tombol
        addButton.setOnAction(event -> addTransaction());
        updateButton.setOnAction(event -> updateTransaction());
        deleteButton.setOnAction(event -> deleteTransaction());
        clearButton.setOnAction(event -> clearForm());

        // Saat baris tabel dipilih, tampilkan data di form
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showTransactionDetails(newSelection);
            }
        });

        // Disable tombol update dan delete saat tidak ada data terpilih
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void addTransaction() {
        if (!validateInput()) return;

        String id = idTransaksiField.getText().trim();
        String tanggal = tanggalField.getText().trim();
        String jumlah = jumlahField.getText().trim();

        // Cek apakah ID sudah ada
        boolean exists = transactionList.stream().anyMatch(t -> t.getIdTransaksi().equals(id));
        if (exists) {
            showAlert(Alert.AlertType.ERROR, "ID Transaksi sudah ada!");
            return;
        }

        Transaction newTransaction = new Transaction(id, tanggal, jumlah);
        transactionList.add(newTransaction);
        clearForm();
    }

    private void updateTransaction() {
        Transaction selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih data transaksi yang akan diupdate!");
            return;
        }

        if (!validateInput()) return;

        String id = idTransaksiField.getText().trim();
        String tanggal = tanggalField.getText().trim();
        String jumlah = jumlahField.getText().trim();

        // Jika ID diubah, cek apakah sudah ada ID lain yang sama
        if (!selected.getIdTransaksi().equals(id)) {
            boolean exists = transactionList.stream().anyMatch(t -> t.getIdTransaksi().equals(id));
            if (exists) {
                showAlert(Alert.AlertType.ERROR, "ID Transaksi sudah ada!");
                return;
            }
        }

        // Update data
        selected.setIdTransaksi(id);
        selected.setTanggal(tanggal);
        selected.setJumlah(jumlah);

        // Refresh tabel
        tableView.refresh();
        clearForm();
    }

    private void deleteTransaction() {
        Transaction selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih data transaksi yang akan dihapus!");
            return;
        }

        transactionList.remove(selected);
        clearForm();
    }

    private void clearForm() {
        idTransaksiField.clear();
        tanggalField.clear();
        jumlahField.clear();
        tableView.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }

    private void showTransactionDetails(Transaction transaction) {
        idTransaksiField.setText(transaction.getIdTransaksi());
        tanggalField.setText(transaction.getTanggal());
        jumlahField.setText(transaction.getJumlah());
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        addButton.setDisable(true);
    }

    private boolean validateInput() {
        String id = idTransaksiField.getText().trim();
        String tanggal = tanggalField.getText().trim();
        String jumlah = jumlahField.getText().trim();

        if (id.isEmpty() || tanggal.isEmpty() || jumlah.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Form input tidak boleh kosong!");
            return false;
        }

        if (!tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showAlert(Alert.AlertType.ERROR, "Format tanggal harus YYYY-MM-DD");
            return false;
        }

        try {
            Double.parseDouble(jumlah);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Jumlah harus berupa angka!");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Pemberitahuan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Model data transaksi dengan properti yang bisa diubah
    public static class Transaction {
        private String idTransaksi;
        private String tanggal;
        private String jumlah;

        public Transaction(String idTransaksi, String tanggal, String jumlah) {
            this.idTransaksi = idTransaksi;
            this.tanggal = tanggal;
            this.jumlah = jumlah;
        }

        public String getIdTransaksi() {
            return idTransaksi;
        }

        public void setIdTransaksi(String idTransaksi) {
            this.idTransaksi = idTransaksi;
        }

        public String getTanggal() {
            return tanggal;
        }

        public void setTanggal(String tanggal) {
            this.tanggal = tanggal;
        }

        public String getJumlah() {
            return jumlah;
        }

        public void setJumlah(String jumlah) {
            this.jumlah = jumlah;
        }
    }
}
