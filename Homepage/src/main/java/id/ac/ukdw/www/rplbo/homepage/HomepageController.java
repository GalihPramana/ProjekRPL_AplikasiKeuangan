package id.ac.ukdw.www.rplbo.homepage;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import id.ac.ukdw.www.rplbo.homepage.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HomepageController implements Initializable {

    // Button untuk pindah halaman
    @FXML private Button btnDebt;
    @FXML private Button btnHome;
    @FXML private Button btnKategori;
    @FXML private Button btnLogout;
    @FXML private Button btnTransaction;

    // Label untuk mengganti text
    @FXML private Label lblBalance;  // Menampilkan saldo total
    @FXML private Label lblDate; // Menampilkan tangggal
    @FXML private Label lblTotalDebt; // Menampilkan total hutang
    @FXML private Label lblTotalReceivables; // Menampilkan total piutang
    @FXML private Label lblWelcome; // Untuk menyapa user

    @FXML private PieChart pieCashflowThisMonth; // Pie chart arus kas
    @FXML private PieChart pieIncomeThisMonth; // Pie chart pemasukan
    @FXML private PieChart pieOutcomeThisMonth; // Pie chart pengeluaran

    // Linechart untuk laporan saldo mingguan (X,Y)
    @FXML private LineChart<String, Number> lineBalanceReport;


    // Menampilkan tanggal di header sesuai format
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

    private final DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // List untuk menampung semua data transaksi user
    private final List<TransactionRecord> allTransactions = new ArrayList<>();

    // List data utang piutang user
    private final List<DebtRecord> allDebts = new ArrayList<>();

    // Initialize awal
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Menampilkan tulisan "Selamat datang user"
        String username = (String) SessionManager.get("user");
        if (username != null && !username.isEmpty()) {
            lblWelcome.setText("Selamat datang, " + username + "!");
        } else {
            lblWelcome.setText("Selamat datang!");
        }
        // Mengeset tanggal sesuai tanggal hari ini
        lblDate.setText(LocalDate.now().format(displayDateFormatter));

        // Mengambil seluruh data terbaru dari database ke allTransactions dan allDebts
        loadAllDataFromDatabase();

        // Mengupdate / refresh seluruh dashboard (PieChart + Label + LineChart)
        updateDashboard();
    }

    // Mengambil data dari Sqlite
    private void loadAllDataFromDatabase() {
        // Mengosongkan list allTransactions dan allDebts untuk menghilangkan data lama
        allTransactions.clear();
        allDebts.clear();

        // Mengambil username dari session manager
        String username = (String) SessionManager.get("user");
        if (username == null || username.isEmpty()) {
            // Mengecek jika belum login maka data tidak di load
            return;
        }

        // Mengambil data dari tabel transaksi sesuai username
        String sqlTrans = """
            SELECT kategori, nominal, tanggal, description
            FROM transaksi
            WHERE username = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTrans)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString("kategori");
                int nominal = rs.getInt("nominal");
                String tanggal = rs.getString("tanggal");
                String description = rs.getString("description");
                allTransactions.add(new TransactionRecord(kategori, nominal, tanggal, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mengambil data dari tabel utang_piutang
        String sqlDebt = """
            SELECT nominal, status, tanggal, kepada
            FROM utang_piutang
            WHERE username = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps2 = conn.prepareStatement(sqlDebt)) {

            ps2.setString(1, username);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                int nominal = rs2.getInt("nominal");
                String status = rs2.getString("status");     // "lunas" / "belum lunas"
                String tanggal = rs2.getString("tanggal");   // "dd/MM/yyyy"
                String kepada = rs2.getString("kepada");
                allDebts.add(new DebtRecord(nominal, status, tanggal, kepada));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update seluruh grafik dan label
    private void updateDashboard() {
        // Menampilkan total saldo yang dimiliki
        int totalSaldo = allTransactions.stream() // Mengubah list menjadi stream untuk mendapat fitur (filter,map,dll)
                .mapToInt(TransactionRecord::getNominal) // Mengambil nominal dari tiap objek di stream lalu diubah menjadi IntStream
                .sum();
        lblBalance.setText(String.format("Rp %,d", totalSaldo).replace(',', '.'));

        // Menampilkan pie chart pendapatan
        Map<String, Integer> incMap = allTransactions.stream()
                .filter(t -> t.getNominal() > 0) // Hanya ambil yang nominalnya positif
                .collect(Collectors.groupingBy( // Mengembalikan map (K,V)
                        TransactionRecord::getKategori, //Ambil semua nama kategori
                        Collectors.summingInt(TransactionRecord::getNominal) // Jumlahkan nominal per kategori
                ));
        pieIncomeThisMonth.getData().setAll(
                incMap.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        );
        pieIncomeThisMonth.setLabelsVisible(true);
        pieIncomeThisMonth.setLegendVisible(true);
        pieIncomeThisMonth.setLegendSide(javafx.geometry.Side.BOTTOM);

        // Menampilkan pie chart pengeluaran
        Map<String, Integer> outMap = allTransactions.stream()
                .filter(t -> t.getNominal() < 0)  // Ambil yang nominalnya negatif
                .collect(Collectors.groupingBy(
                        TransactionRecord::getKategori,
                        Collectors.summingInt(t -> Math.abs(t.getNominal()))
                ));
        pieOutcomeThisMonth.getData().setAll(
                outMap.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        );
        pieOutcomeThisMonth.setLabelsVisible(true);
        pieOutcomeThisMonth.setLegendVisible(true);
        pieOutcomeThisMonth.setLegendSide(javafx.geometry.Side.BOTTOM);

        // Menampilkan pie chart arus kas
        int totalIn  = incMap.values().stream().mapToInt(i -> i).sum();
        int totalOut = outMap.values().stream().mapToInt(i -> i).sum();
        pieCashflowThisMonth.getData().setAll(
                List.of(
                        new PieChart.Data("Pemasukan", totalIn),
                        new PieChart.Data("Pengeluaran", totalOut)
                )
        );
        pieCashflowThisMonth.setLabelsVisible(true);
        pieCashflowThisMonth.setLegendVisible(true);
        pieCashflowThisMonth.setLegendSide(javafx.geometry.Side.BOTTOM);

        // Menampilkan total hutang dan piutang
        int totalPiutang = allDebts.stream()
                .filter(d -> d.getNominal() > 0)      // piutang dianggap nominal > 0
                .mapToInt(DebtRecord::getNominal)
                .sum();
        int totalHutang = allDebts.stream()
                .filter(d -> d.getNominal() < 0)     // karena hutang (-) maka harus di absolutekan
                .mapToInt(d -> Math.abs(d.getNominal()))
                .sum();
        lblTotalReceivables.setText(String.format("Rp %,d", totalPiutang).replace(',', '.'));
        lblTotalDebt.setText(String.format("Rp %,d", totalHutang).replace(',', '.'));


       // Line chart saldo 7 hari terakhir
        lineBalanceReport.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Saldo Harian");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(dbFormatter);  // "dd/MM/yyyy" sesuai penyimpanan di DB

            // hitung jumlah transaksi pada hari itu
            int sumOfThatDay = allTransactions.stream()
                    .filter(t -> t.getTanggal().equals(dateStr))
                    .mapToInt(TransactionRecord::getNominal)
                    .sum();

            // label yang muncul di sumbu X, misalnya "8 Mei"
            String labelX = date.format(DateTimeFormatter.ofPattern("d MMM"));
            // Tambahkan titik di chart
            series.getData().add(new XYChart.Data<>(labelX, sumOfThatDay));
        }
        lineBalanceReport.getData().add(series);
        lineBalanceReport.setLegendVisible(false);
        lineBalanceReport.setCreateSymbols(true);  // tampilkan titik per data
    }




     //TransactionRecord merepresentasikan satu baris pada tabel "transaksi"
     // Kolom: kategori, nominal, tanggal, description.

    public static class TransactionRecord {
        private final String kategori;
        private final int nominal;
        private final String tanggal;
        private final String description;

        public TransactionRecord(String kategori, int nominal, String tanggal, String description) {
            this.kategori = kategori;
            this.nominal = nominal;
            this.tanggal = tanggal;
            this.description = description;
        }
        public String getKategori() { return kategori; }
        public int getNominal() { return nominal; }
        public String getTanggal() { return tanggal; }
        public String getDescription() { return description; }
    }


     // DebtRecord merepresentasikan satu baris pada tabel "utang_piutang".
     // Kolom: nominal (positif=piutang, negatif=hutang), status, tanggal, kepada.

    public static class DebtRecord {
        private final int nominal;
        private final String status;
        private final String tanggal;
        private final String kepada;

        public DebtRecord(int nominal, String status, String tanggal, String kepada) {
            this.nominal = nominal;
            this.status = status;
            this.tanggal = tanggal;
            this.kepada = kepada;
        }
        public int getNominal() { return nominal; }
        public String getStatus() { return status; }
        public String getTanggal() { return tanggal; }
        public String getKepada() { return kepada; }
    }

    // Pindah pages
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
}
