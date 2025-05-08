package UtangPiutang;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import UtangPiutang.Utang;

public class RingkasanUtangController {

    @FXML private TableView<Utang> tableUtang;
    @FXML private TableColumn<Utang, String> colNama;
    @FXML private TableColumn<Utang, Double> colTotal;
    @FXML private TableColumn<Utang, Double> colSisa;
    @FXML private TableColumn<Utang, Double> colDibayar;
    @FXML private Label labelJumlah;
    @FXML private Label labelSisa;
    @FXML private Label labelDibayar;

    private final ObservableList<Utang> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colSisa.setCellValueFactory(new PropertyValueFactory<>("sisa"));
        colDibayar.setCellValueFactory(new PropertyValueFactory<>("dibayar"));

        tableUtang.setItems(data);
        updateRingkasan();
    }

    @FXML
    private void handleTambah() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tambah Utang");
        dialog.setHeaderText("Masukkan data utang (format: nama,total,dibayar)");
        dialog.setContentText("Contoh: Marvin,1000000,200000");

        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] parts = input.split(",");
                String nama = parts[0];
                double total = Double.parseDouble(parts[1]);
                double dibayar = Double.parseDouble(parts[2]);
                double sisa = total - dibayar;

                data.add(new Utang(nama, total, sisa, dibayar));
                updateRingkasan();
            } catch (Exception e) {
                showAlert("Format tidak valid!");
            }
        });
    }

    @FXML
    private void handleEdit() {
        Utang selected = tableUtang.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Pilih data yang ingin diedit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.format("%s,%.0f,%.0f", selected.getNama(), selected.getTotal(), selected.getDibayar()));
        dialog.setTitle("Edit Utang");
        dialog.setHeaderText("Edit data utang (format: nama,total,dibayar)");
        dialog.setContentText("Contoh: Marvin,1000000,200000");

        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] parts = input.split(",");
                selected.setNama(parts[0]);
                double total = Double.parseDouble(parts[1]);
                double dibayar = Double.parseDouble(parts[2]);
                double sisa = total - dibayar;

                selected.setTotal(total);
                selected.setDibayar(dibayar);
                selected.setSisa(sisa);
                tableUtang.refresh();
                updateRingkasan();
            } catch (Exception e) {
                showAlert("Format tidak valid!");
            }
        });
    }

    @FXML
    private void handleHapus() {
        Utang selected = tableUtang.getSelectionModel().getSelectedItem();
        if (selected != null) {
            data.remove(selected);
            updateRingkasan();
        } else {
            showAlert("Pilih data yang ingin dihapus.");
        }
    }

    private void updateRingkasan() {
        double total = data.stream().mapToDouble(Utang::getTotal).sum();
        double sisa = data.stream().mapToDouble(Utang::getSisa).sum();
        double dibayar = data.stream().mapToDouble(Utang::getDibayar).sum();

        labelJumlah.setText("Jumlah Utang: Rp " + format(total));
        labelSisa.setText("Sisa Utang: Rp " + format(sisa));
        labelDibayar.setText("Yang Sudah Dibayar: Rp " + format(dibayar));
    }

    private String format(double val) {
        return String.format("%,.0f", val).replace(",", ".");
    }

    private void showAlert(String pesan) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    public static class Utang {
        private String nama;
        private double total;
        private double sisa;
        private double dibayar;

        public Utang(String nama, double total, double sisa, double dibayar) {
            this.nama = nama;
            this.total = total;
            this.sisa = sisa;
            this.dibayar = dibayar;
        }

        public String getNama() { return nama; }
        public double getTotal() { return total; }
        public double getSisa() { return sisa; }
        public double getDibayar() { return dibayar; }

        public void setNama(String nama) { this.nama = nama; }
        public void setTotal(double total) { this.total = total; }
        public void setSisa(double sisa) { this.sisa = sisa; }
        public void setDibayar(double dibayar) { this.dibayar = dibayar; }
    }
}
