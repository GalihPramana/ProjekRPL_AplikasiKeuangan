// TransactionController.java
package id.ac.ukdw.www.rplbo.homepage;

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
import javafx.util.Callback;

import java.io.IOException;
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
    @FXML private TableColumn<Transaction, String> idColumn, sourceNameColumn, descriptionColumn, tanggalColumn;
    @FXML private TableColumn<Transaction, Integer> jumlahColumn;

    private final ObservableList<Transaction> transactionList = HomepageController.DataProvider.TRANSACTIONS;
    private final FilteredList<Transaction> filteredTransactionList = new FilteredList<>(transactionList, p -> true);
    private Transaction selectedTransaction;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
    private int batasBulanan = 0;

    @FXML
    public void initialize() {
        ObservableList<String> kategoriOptions = FXCollections.observableArrayList("Makan", "Transportasi", "Gaji", "Belanja", "Lainnya");
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

        toggleUpdateButtonState(false);
        updateTotalSaldo();
        lblDate.setText(LocalDate.now().format(displayDateFormatter));
    }

    @FXML void onClearClick(ActionEvent event) { handleClear(); }
    @FXML void onDeleteClick(ActionEvent event) { handleDelete(); }
    @FXML void onPemasukkanClick(ActionEvent event) { handlePemasukan(true); }
    @FXML void onPengeluaranClick(ActionEvent event) { handlePemasukan(false); }
    @FXML void onUpdateClick(ActionEvent event) { handleUpdate(); }
    @FXML void onDebtClick(ActionEvent event) { changeScene("UtangPiutang.fxml", btnDebt); }
    @FXML void onHomeClick(ActionEvent event) { changeScene("homepage-view.fxml", btnHome); }
    @FXML
    void onKategoriClick(ActionEvent event) {
        try {
            Parent kategoriRoot = FXMLLoader.load(getClass().getResource("kategori-view.fxml"));
            Scene scene = btnKategori.getScene();
            scene.setRoot(kategoriRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML void onLogoutClick(ActionEvent event) { changeScene("Login.fxml", btnLogout); }
    @FXML void onTransactionClick(ActionEvent event) { }

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

    private void handlePemasukan(boolean isIncome) {
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

        if (jml <= 0) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Nominal harus lebih besar dari 0.");
            return;
        }

        if (!isIncome) {
            jml = -Math.abs(jml);
        }

        String newId = "TRX" + (transactionList.size() + 1);
        Transaction newTransaction = new Transaction(newId, sumber, jml, desc, tanggal.format(formatter));
        transactionList.add(newTransaction);

        handleClear();
        updateTotalSaldo();
        updateBatasBulanan();
        cekBatasPengeluaranBulanan();
    }

    private void handleUpdate() {
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "Update Error", "Pilih transaksi yang ingin diubah.");
            return;
        }

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

        if (jml == 0) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Nominal tidak boleh 0.");
            return;
        }

        selectedTransaction.setSourceName(sumber);
        selectedTransaction.setJumlah(jml);
        selectedTransaction.setDescription(desc);
        selectedTransaction.setTanggal(tanggal.format(formatter));

        int selectedIndex = transactionList.indexOf(selectedTransaction);
        if (selectedIndex >= 0) {
            transactionList.set(selectedIndex, selectedTransaction);
        }

        handleClear();
        updateTotalSaldo();
        updateBatasBulanan();
        cekBatasPengeluaranBulanan();
    }

    private void handleDelete() {
        Transaction sel = tableView.getSelectionModel().getSelectedItem();
        if (sel != null) {
            transactionList.remove(sel);
            handleClear();
            updateTotalSaldo();
        } else {
            showAlert(Alert.AlertType.WARNING, "Delete Error", "Pilih transaksi yang ingin dihapus.");
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
            showAlert(Alert.AlertType.WARNING,
                    "Peringatan Pengeluaran!",
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

    @FXML
    public void onlogClick(ActionEvent actionEvent) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("Log.fxml"));
            Scene scene = logButton.getScene();
            scene.setRoot(loginRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}