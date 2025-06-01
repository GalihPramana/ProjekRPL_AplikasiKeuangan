package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LogController {

    @FXML
    private Button btnTransaction;

    @FXML
    private TableView<LogTransaksi> logTable;

    @FXML
    private TableColumn<LogTransaksi, Integer> colId;
    @FXML
    private TableColumn<LogTransaksi, String> colUsername;
    @FXML
    private TableColumn<LogTransaksi, String> colAksi;
    @FXML
    private TableColumn<LogTransaksi, String> colKategori;
    @FXML
    private TableColumn<LogTransaksi, Integer> colNominal;
    @FXML
    private TableColumn<LogTransaksi, String> colDeskripsi;
    @FXML
    private TableColumn<LogTransaksi, String> colTanggalTransaksi;
    @FXML
    private TableColumn<LogTransaksi, String> colWaktuLog;

    private ObservableList<LogTransaksi> logList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colAksi.setCellValueFactory(new PropertyValueFactory<>("aksi"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colNominal.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colTanggalTransaksi.setCellValueFactory(new PropertyValueFactory<>("tanggalTransaksi"));
        colWaktuLog.setCellValueFactory(new PropertyValueFactory<>("waktuLog"));

        loadLogData();
    }

    private void loadLogData() {
        logList.clear();
        String sql = "SELECT * FROM log_transaksi ORDER BY waktu_log DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logList.add(new LogTransaksi(
                        rs.getInt("log_id"),
                        rs.getString("username"),
                        rs.getString("aksi"),
                        rs.getString("kategori"),
                        rs.getInt("nominal"),
                        rs.getString("deskripsi"),
                        rs.getString("tanggal_transaksi"),
                        rs.getString("waktu_log")
                ));
            }

            logTable.setItems(logList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onTransactionClick() {
        try {
            Parent kategoriRoot = FXMLLoader.load(getClass().getResource("transaction-view.fxml"));
            Scene scene = btnTransaction.getScene();
            scene.setRoot(kategoriRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
