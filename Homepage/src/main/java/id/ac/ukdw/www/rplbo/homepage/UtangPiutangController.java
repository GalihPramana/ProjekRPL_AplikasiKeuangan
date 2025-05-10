package id.ac.ukdw.www.rplbo.homepage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class UtangPiutangController {
    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTransaction;

    @FXML private TextField tfJumlahUtang, tfSisaUtang, tfSudahDibayar, tfFilter;
    @FXML private TableView<UtangPiutang> table;
    @FXML private TableColumn<UtangPiutang, Integer> colJumlah, colSisa, colBayar;

    private ObservableList<UtangPiutang> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colJumlah.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getJumlahUtang()).asObject());
        colSisa.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getSisaUtang()).asObject());
        colBayar.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getSudahDibayar()).asObject());

        table.setItems(data);
    }

    @FXML
    public void handleTambah() {
        int jumlah = Integer.parseInt(tfJumlahUtang.getText());
        int sisa = Integer.parseInt(tfSisaUtang.getText());
        int bayar = Integer.parseInt(tfSudahDibayar.getText());
        data.add(new UtangPiutang(jumlah, sisa, bayar));
        bersihkan();
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
        String filterText = tfFilter.getText();
        if (filterText.isEmpty()) {
            table.setItems(data);
        } else {
            ObservableList<UtangPiutang> filtered = FXCollections.observableArrayList();
            for (UtangPiutang u : data) {
                if (String.valueOf(u.getJumlahUtang()).contains(filterText) ||
                        String.valueOf(u.getSisaUtang()).contains(filterText) ||
                        String.valueOf(u.getSudahDibayar()).contains(filterText)) {
                    filtered.add(u);
                }
            }
            table.setItems(filtered);
        }
    }

    private void bersihkan() {
        tfJumlahUtang.clear();
        tfSisaUtang.clear();
        tfSudahDibayar.clear();
    }

    @FXML
    void onDebtClick(ActionEvent event) {
        //Halaman ini
    }

    @FXML
    void onHomeClick(ActionEvent event) {
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

    @FXML
    void onLogoutClick(ActionEvent event) {
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

    @FXML
    void onTransactionClick(ActionEvent event) {
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

}
