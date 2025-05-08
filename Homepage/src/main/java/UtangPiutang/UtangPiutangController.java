package UtangPiutang;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class UtangPiutangController {

    @FXML
    private void handleTambahData() {
        showInfo("Menambahkan data utang/piutang.\nData: Nama, No Kontak, Nominal, Tanggal, Status, Catatan");
    }

    @FXML
    private void handleEditData() {
        showInfo("Mengedit data utang/piutang.");
    }

    @FXML
    private void handleHapusData() {
        showInfo("Menghapus data utang/piutang.");
    }

    @FXML
    private void handleTampilkanLog() {
        showInfo("Menampilkan log riwayat pengeditan.");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}