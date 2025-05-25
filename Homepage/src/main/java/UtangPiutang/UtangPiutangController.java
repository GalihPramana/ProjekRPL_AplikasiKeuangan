package UtangPiutang;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UtangPiutangController {
    @FXML private TextField tfNama, tfTanggal;
    @FXML private TextField tfJumlahUtang, tfSudahDibayar, tfFilter;
    @FXML private CheckBox cbLunas;

    @FXML private TableView<UtangPiutang> table;
    @FXML private TableColumn<UtangPiutang, String> colNama, colTanggal;
    @FXML private TableColumn<UtangPiutang, Integer> colJumlah, colSisa, colBayar;
    @FXML private TableColumn<UtangPiutang, Boolean> colLunas;

    private ObservableList<UtangPiutang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNama.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNama()));
        colTanggal.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTanggal()));
        colJumlah.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getJumlahUtang()).asObject());
        colBayar.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getSudahDibayar()).asObject());
        colSisa.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getSisaUtang()).asObject());
        colLunas.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isLunas()).asObject());

        table.setItems(data);
    }

    @FXML
    public void handleTambah() {
        try {
            String nama = tfNama.getText();
            String tanggal = tfTanggal.getText();
            int jumlah = Integer.parseInt(tfJumlahUtang.getText());
            int bayar = Integer.parseInt(tfSudahDibayar.getText());
            int sisa = jumlah - bayar;
            boolean lunas = cbLunas.isSelected();

            data.add(new UtangPiutang(nama, tanggal, jumlah, sisa, bayar, lunas));
            bersihkan();
        } catch (NumberFormatException e) {
            showAlert("Input tidak valid", "Jumlah dan pembayaran harus berupa angka.");
        }
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
        String filterText = tfFilter.getText().toLowerCase();
        if (filterText.isEmpty()) {
            table.setItems(data);
        } else {
            ObservableList<UtangPiutang> filtered = FXCollections.observableArrayList();
            for (UtangPiutang u : data) {
                if (u.getNama().toLowerCase().contains(filterText) ||
                        u.getTanggal().toLowerCase().contains(filterText) ||
                        String.valueOf(u.getJumlahUtang()).contains(filterText) ||
                        String.valueOf(u.getSisaUtang()).contains(filterText) ||
                        String.valueOf(u.getSudahDibayar()).contains(filterText) ||
                        (u.isLunas() ? "lunas" : "belum").contains(filterText)) {
                    filtered.add(u);
                }
            }
            table.setItems(filtered);
        }
    }

    private void bersihkan() {
        tfNama.clear();
        tfTanggal.clear();
        tfJumlahUtang.clear();
        tfSudahDibayar.clear();
        cbLunas.setSelected(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
