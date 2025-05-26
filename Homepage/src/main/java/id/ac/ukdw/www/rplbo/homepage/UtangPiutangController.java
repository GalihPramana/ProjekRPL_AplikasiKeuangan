package id.ac.ukdw.www.rplbo.homepage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class UtangPiutangController {

    @FXML private TextField tfKepadaSiapa;
    @FXML private DatePicker dpTanggal;
    @FXML private TextField tfJumlahUtang;
    @FXML private TextField tfFilter;

    @FXML private TableView<UtangPiutang> table;
    @FXML private TableColumn<UtangPiutang, String> colKepadaSiapa;
    @FXML private TableColumn<UtangPiutang, String> colTanggal;
    @FXML private TableColumn<UtangPiutang, Integer> colJumlahUtang;
    @FXML private TableColumn<UtangPiutang, String> colStatus;

    private ObservableList<UtangPiutang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colKepadaSiapa.setCellValueFactory(new PropertyValueFactory<>("kepadaSiapa"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlahUtang.setCellValueFactory(new PropertyValueFactory<>("jumlahUtang"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn("Belum Lunas", "Lunas"));
        colStatus.setOnEditCommit(event -> {
            UtangPiutang transaksi = event.getRowValue();
            transaksi.setStatus(event.getNewValue());
        });

        table.setEditable(true);
        table.setItems(data);
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

        if (kepada.isEmpty() || tanggal == null) {
            showAlert("Lengkapi semua data");
            return;
        }

        if (jenis.equals("Hutang")) {
            jumlah = -Math.abs(jumlah); // negatif untuk hutang
        } else {
            jumlah = Math.abs(jumlah);  // positif untuk piutang
        }

        data.add(new UtangPiutang("User", kepada, tanggal.toString(), jumlah, "Belum Lunas"));
        clearForm();
    }

    @FXML
    public void handleHapus() {
        UtangPiutang selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            data.remove(selected);
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
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
