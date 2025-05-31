package id.ac.ukdw.www.rplbo.homepage;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.ListChangeListener;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import id.ac.ukdw.www.rplbo.homepage.util.SessionManager;


public class HomepageController implements Initializable {

    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnKategori;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTransaction;

    @FXML
    private Label lblBalance;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTotalDebt;

    @FXML
    private Label lblTotalReceivables;

    @FXML
    private Label lblWelcome;

    @FXML
    private LineChart<?, ?> lineBalanceReport;

    @FXML
    private PieChart pieCashflowThisMonth;

    @FXML
    private PieChart pieIncomeThisMonth;

    @FXML
    private PieChart pieOutcomeThisMonth;

    public class DataProvider {
        public static final ObservableList<Transaction> TRANSACTIONS = FXCollections.observableArrayList();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String username = (String) SessionManager.get("user");

        if (username != null) {
            lblWelcome.setText("Selamat datang, " + username + "!");
        } else {
            lblWelcome.setText("Selamat datang!");
        }
        DataProvider.TRANSACTIONS.addListener((ListChangeListener<Transaction>) change -> updateDashboard());
        updateDashboard();

        java.time.format.DateTimeFormatter displayDateFormatter =
                java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy");
        lblDate.setText(java.time.LocalDate.now().format(displayDateFormatter));
    }


    @FXML
    void onDebtClick(ActionEvent event) {
        try {
            Parent debtRoot = FXMLLoader.load(getClass().getResource("UtangPiutang.fxml"));
            Scene scene = btnDebt.getScene();
            scene.setRoot(debtRoot);
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

    @FXML
    void onHomeClick(ActionEvent event) {
        // Sudah berada di halaman ini
    }

    @FXML
    void onKategoriClick(ActionEvent event) {
        try {
            Parent kategoriRoot = FXMLLoader.load(getClass().getResource("kategori-view.fxml"));
            Scene scene = btnKategori.getScene();
            scene.setRoot(kategoriRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        try {
            SessionManager.clear();

            Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = btnLogout.getScene();
            scene.setRoot(loginRoot);
            Stage stage = (Stage) scene.getWindow();
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDashboard() {
        var txs = DataProvider.TRANSACTIONS;

        // 1) Total Saldo
        int totalSaldo = txs.stream()
                .mapToInt(Transaction::getJumlah)
                .sum();
        lblBalance.setText(String.format("Rp %,d", totalSaldo)
                .replace(',', '.'));

        // 2) Data Pendapatan per kategori
        var incMap = txs.stream()
                .filter(t -> t.getJumlah() > 0)
                .collect(java.util.stream.Collectors.groupingBy(
                        Transaction::getSourceName,
                        java.util.stream.Collectors.summingInt(Transaction::getJumlah)
                ));
        pieIncomeThisMonth.getData().setAll(
                incMap.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        );

        // 3) Data Pengeluaran per kategori
        var outMap = txs.stream()
                .filter(t -> t.getJumlah() < 0)
                .collect(java.util.stream.Collectors.groupingBy(
                        Transaction::getSourceName,
                        java.util.stream.Collectors.summingInt(t -> Math.abs(t.getJumlah()))
                ));
        pieOutcomeThisMonth.getData().setAll(
                outMap.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        );

        // 4) Arus Kas: sederhana = pemasukan vs pengeluaran total
        int totalIn  = incMap.values().stream().mapToInt(i->i).sum();
        int totalOut = outMap.values().stream().mapToInt(i->i).sum();
        pieCashflowThisMonth.getData().setAll(
                java.util.List.of(
                        new PieChart.Data("Pemasukan", totalIn),
                        new PieChart.Data("Pengeluaran", totalOut)
                )
        );
    }


}