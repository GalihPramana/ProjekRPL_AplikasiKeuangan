package UtangPiutang;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UtangPiutangController {

    @FXML private TextField tfNamaPenghutang, tfJumlah, tfDibayar, tfKepada;
    @FXML private DatePicker dpTanggal;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField tfFilter;

    @FXML private TableView<UtangPiutang> table;
    @FXML private TableColumn<UtangPiutang, String> colNama, colTanggal, colKepada, colStatus;
    @FXML private TableColumn<UtangPiutang, Integer> colJumlah, colDibayar, colSisa;

    private final ObservableList<UtangPiutang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Belum Lunas", "Sebagian", "Lunas"));

        colNama.setCellValueFactory(val -> new SimpleStringProperty(val.getValue().getNama()));
        colTanggal.setCellValueFactory(val -> new SimpleStringProperty(val.getValue().getTanggal()));
        colKepada.setCellValueFactory(val -> new SimpleStringProperty(val.getValue().getKepada()));
        colStatus.setCellValueFactory(val -> new SimpleStringProperty(val.getValue().getStatus()));
        colJumlah.setCellValueFactory(val -> new SimpleIntegerProperty(val.getValue().getJumlahUtang()).asObject());
        colDibayar.setCellValueFactory(val -> new SimpleIntegerProperty(val.getValue().getSudahDibayar()).asObject());
        colSisa.setCellValueFactory(val -> new SimpleIntegerProperty(val.getValue().getSisaUtang()).asObject());

        table.setItems(data);
    }

    @FXML
    public void tambahData() {
        try {
            String nama = tfNamaPenghutang.getText().trim();
            String kepada = tfKepada.getText().trim();
            String tanggal = dpTanggal.getValue() != null ? dpTanggal.getValue().toString() : "";
            int jumlah = Integer.parseInt(tfJumlah.getText().trim());
            int dibayar = Integer.parseInt(tfDibayar.getText().trim());
            String status = cbStatus.getValue();

            int sisa = jumlah - dibayar;
            data.add(new UtangPiutang(nama, kepada, tanggal, jumlah, dibayar, sisa, status));
            clearForm();
        } catch (Exception e) {
            showAlert("Input tidak valid", "Pastikan semua kolom terisi dengan benar.");
        }
    }

    @FXML
    public void hapusData() {
        UtangPiutang selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            data.remove(selected);
        }
    }

    @FXML
    public void filterData() {
        String keyword = tfFilter.getText().toLowerCase();
        if (keyword.isEmpty()) {
            table.setItems(data);
        } else {
            ObservableList<UtangPiutang> filtered = FXCollections.observableArrayList();
            for (UtangPiutang up : data) {
                if (up.getNama().toLowerCase().contains(keyword) ||
                        up.getKepada().toLowerCase().contains(keyword) ||
                        up.getTanggal().toLowerCase().contains(keyword) ||
                        up.getStatus().toLowerCase().contains(keyword) ||
                        String.valueOf(up.getJumlahUtang()).contains(keyword) ||
                        String.valueOf(up.getSudahDibayar()).contains(keyword) ||
                        String.valueOf(up.getSisaUtang()).contains(keyword)) {
                    filtered.add(up);
                }
            }
            table.setItems(filtered);
        }
    }

    private void clearForm() {
        tfNamaPenghutang.clear();
        tfKepada.clear();
        tfJumlah.clear();
        tfDibayar.clear();
        dpTanggal.setValue(null);
        cbStatus.setValue(null);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}