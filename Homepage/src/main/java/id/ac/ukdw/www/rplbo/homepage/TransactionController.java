package id.ac.ukdw.www.rplbo.homepage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class TransactionController {

    @FXML
    private TextField idTransaksiField;

    @FXML
    private TextField sourceNameField;

    @FXML
    private TextField jumlahField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField tanggalField;

    @FXML
    private Button submitButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<Transaction> tableView;

    @FXML
    private TableColumn<Transaction, String> idColumn;

    @FXML
    private TableColumn<Transaction, String> sourceNameColumn;

    @FXML
    private TableColumn<Transaction, Integer> jumlahColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, String> tanggalColumn;

    @FXML
    private Label totalSaldoLabel;

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set cell value factories
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        sourceNameColumn.setCellValueFactory(new PropertyValueFactory<>("sourceName"));
        jumlahColumn.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        // Enable sorting on columns (default enabled, but ensure)
        tableView.getSortOrder().add(idColumn);

        // Set cell factory for jumlahColumn to color text based on value
        jumlahColumn.setCellFactory(new Callback<TableColumn<Transaction, Integer>, TableCell<Transaction, Integer>>() {
            @Override
            public TableCell<Transaction, Integer> call(TableColumn<Transaction, Integer> param) {
                return new TableCell<Transaction, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(String.format("%,d", item).replace(',', '.'));
                            if (item < 0) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                };
            }
        });

        // Set items to table
        tableView.setItems(transactionList);

        // Button actions
        submitButton.setOnAction(e -> handleSubmit());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClear());

        // When selecting a row, populate fields
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        updateTotalSaldo();
    }

    private void handleSubmit() {
        String id = idTransaksiField.getText().trim();
        String sourceName = sourceNameField.getText().trim();
        String jumlahText = jumlahField.getText().trim();
        String description = descriptionField.getText().trim();
        String tanggal = tanggalField.getText().trim();

        if (id.isEmpty() || sourceName.isEmpty() || jumlahText.isEmpty() || tanggal.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "ID, Kategori, Nominal, dan Tanggal harus diisi.");
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Nominal harus berupa angka (boleh negatif).");
            return;
        }

        // Cek apakah ID sudah ada (update) atau baru (add)
        Transaction existing = findTransactionById(id);
        if (existing != null) {
            // Update existing
            existing.setSourceName(sourceName);
            existing.setJumlah(jumlah);
            existing.setDescription(description);
            existing.setTanggal(tanggal);
            tableView.refresh();
        } else {
            // Add new
            Transaction newTransaction = new Transaction(id, sourceName, jumlah, description, tanggal);
            transactionList.add(newTransaction);
        }

        clearFields();
        updateTotalSaldo();
    }

    private void handleDelete() {
        Transaction selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            transactionList.remove(selected);
            clearFields();
            updateTotalSaldo();
        } else {
            showAlert(Alert.AlertType.WARNING, "Delete Error", "Pilih transaksi yang ingin dihapus.");
        }
    }

    private void handleClear() {
        clearFields();
        tableView.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        idTransaksiField.setText("");
        sourceNameField.setText("");
        jumlahField.setText("");
        descriptionField.setText("");
        tanggalField.setText("");
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
            if (t.getIdTransaksi().equals(id)) {
                return t;
            }
        }
        return null;
    }

    private void updateTotalSaldo() {
        int total = 0;
        for (Transaction t : transactionList) {
            total += t.getJumlah();
        }
        totalSaldoLabel.setText(String.format("Total Saldo : ( RP %,d )", total).replace(',', '.'));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
