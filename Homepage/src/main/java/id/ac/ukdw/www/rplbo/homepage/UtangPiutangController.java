package id.ac.ukdw.www.rplbo.homepage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class UtangPiutangController {

    @FXML private TextField tfKepadaSiapa;
    @FXML private DatePicker dpTanggal;
    @FXML private TextField tfJumlahUtang;
    @FXML private TextField tfFilter;
    @FXML private TableView<Transaksi> table;
    @FXML private TableColumn<Transaksi, String> colKepadaSiapa;
    @FXML private TableColumn<Transaksi, LocalDate> colTanggal;
    @FXML private TableColumn<Transaksi, Double> colJumlahUtang;

    private ObservableList<Transaksi> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colKepadaSiapa.setCellValueFactory(new PropertyValueFactory<>("kepadaSiapa"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlahUtang.setCellValueFactory(new PropertyValueFactory<>("jumlah"));

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
        double jumlah;

        try {
            jumlah = Double.parseDouble(tfJumlahUtang.getText());
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

        data.add(new Transaksi(kepada, tanggal, jumlah));
        clearForm();
    }

    @FXML
    public void handleHapus() {
        Transaksi selected = table.getSelectionModel().getSelectedItem();
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

        ObservableList<Transaksi> filtered = FXCollections.observableArrayList();
        for (Transaksi t : data) {
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

    // Inner class untuk model data
    public static class Transaksi {
        private final String kepadaSiapa;
        private final LocalDate tanggal;
        private final double jumlah;

        public Transaksi(String kepadaSiapa, LocalDate tanggal, double jumlah) {
            this.kepadaSiapa = kepadaSiapa;
            this.tanggal = tanggal;
            this.jumlah = jumlah;
        }

        public String getKepadaSiapa() {
            return kepadaSiapa;
        }

        public LocalDate getTanggal() {
            return tanggal;
        }

        public double getJumlah() {
            return jumlah;
        }
    }
}
