package UtangPiutang;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.UtangData;

public class UtangPiutangController {

    @FXML private Label labelJumlahUtang;
    @FXML private Label labelSisaUtang;
    @FXML private Label labelSudahDibayar;

    @FXML private TableView<UtangData> tableViewUtang;
    @FXML private TableColumn<UtangData, String> colNama;
    @FXML private TableColumn<UtangData, Double> colTotal, colSisa, colDibayar;

    private final ObservableList<UtangData> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Contoh data dummy
        dataList.addAll(
                new UtangData("Andi", 1000000, 400000, 600000),
                new UtangData("Budi", 750000, 250000, 500000),
                new UtangData("Citra", 500000, 100000, 400000)
        );

        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colSisa.setCellValueFactory(new PropertyValueFactory<>("sisa"));
        colDibayar.setCellValueFactory(new PropertyValueFactory<>("dibayar"));

        tableViewUtang.setItems(dataList);

        updateRingkasan();
    }

    private void updateRingkasan() {
        double totalUtang = 0;
        double totalSisa = 0;
        double totalBayar = 0;

        for (UtangData data : dataList) {
            totalUtang += data.getTotal();
            totalSisa += data.getSisa();
            totalBayar += data.getDibayar();
        }

        labelJumlahUtang.setText(formatRupiah(totalUtang));
        labelSisaUtang.setText(formatRupiah(totalSisa));
        labelSudahDibayar.setText(formatRupiah(totalBayar));
    }

    private String formatRupiah(double nominal) {
        return String.format("Rp%,.0f", nominal).replace(",", ".");
    }
}
