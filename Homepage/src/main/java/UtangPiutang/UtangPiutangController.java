package UtangPiutang;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    }

    @FXML
    void onHomeClick(ActionEvent event) {

    }

    @FXML
    void onLogoutClick(ActionEvent event) {

    }

    @FXML
    void onTransactionClick(ActionEvent event) {

    }

}
