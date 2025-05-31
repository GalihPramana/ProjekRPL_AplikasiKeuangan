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
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class UtangPiutangController {

    @FXML
    public ChoiceBox cbStatus;

    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTransaction;

    @FXML
    private TextField tfKepadaSiapa;
    @FXML
    private DatePicker dpTanggal;
    @FXML
    private TextField tfJumlahUtang;
    @FXML
    private TextField tfFilter;

    @FXML
    private TableView<UtangPiutang> table;
    @FXML
    private TableColumn<UtangPiutang, String> colKepadaSiapa;
    @FXML
    private TableColumn<UtangPiutang, String> colTanggal;
    @FXML
    private TableColumn<UtangPiutang, Integer> colJumlahUtang;
    @FXML
    private TableColumn<UtangPiutang, String> colStatus;

    private ObservableList<UtangPiutang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colKepadaSiapa.setCellValueFactory(new PropertyValueFactory<>("kepadaSiapa"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJumlahUtang.setCellValueFactory(new PropertyValueFactory<>("jumlahUtang"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.setEditable(true);
        table.setItems(data);

        cbStatus.getItems().addAll("Lunas", "Belum Lunas");
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

        String status = (String) cbStatus.getValue();
        if (kepada.isEmpty() || tanggal == null || status == null || status.isEmpty()) {
            showAlert("Lengkapi semua data");
            return;
        }

        if (jenis.equals("Hutang")) {
            jumlah = -Math.abs(jumlah);
        } else {
            jumlah = Math.abs(jumlah);
        }

        data.add(new UtangPiutang("User", kepada, tanggal.toString(), jumlah, status));
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
        cbStatus.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onHomeClick(ActionEvent actionEvent) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("homepage-view.fxml"));
            Scene scene = btnHome.getScene();
            scene.setRoot(homeRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onTransactionClick(ActionEvent actionEvent) {
        try {
            Parent transactionRoot = FXMLLoader.load(getClass().getResource("transaction-view.fxml"));
            Scene scene = btnTransaction.getScene();
            scene.setRoot(transactionRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDebtClick(ActionEvent actionEvent) {
    }

    public void onLogoutClick(ActionEvent actionEvent) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = btnLogout.getScene();
            scene.setRoot(loginRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
