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

    // ===================== FXML INJECTIONS =====================
    @FXML private Button btnDebt;
    @FXML private Button btnHome;
    @FXML private Button btnKategori;
    @FXML private Button btnLogout;
    @FXML private Button btnTransaction;

    @FXML private Label lblBalance;
    @FXML private Label lblDate;
    @FXML private Label lblTotalDebt;
    @FXML private Label lblTotalReceivables;
    @FXML private Label lblWelcome;

    @FXML private PieChart pieCashflowThisMonth;
    @FXML private PieChart pieIncomeThisMonth;
    @FXML private PieChart pieOutcomeThisMonth;

    // Karena FXML mendefinisikan lineBalanceReport dengan <CategoryAxis> dan <NumberAxis>,
    // di controller kita gunakan tipe generiknya:
    @FXML private LineChart<String, Number> lineBalanceReport;

    // Formatter global:
    // – displayDateFormatter: untuk menampilkan “8 Mei 2025” di header
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
    // – dbFormatter: diasumsikan tanggal di DB disimpan dalam format “dd/MM/yyyy”
    private final DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===================== DATA MODEL DI CONTROLLER =====================
    /** List yang menampung semua data transaksi milik user saat ini */
    private final List<TransactionRecord> allTransactions = new ArrayList<>();

    /** List yang menampung semua data utang/piutang milik user saat ini */
    private final List<DebtRecord> allDebts = new ArrayList<>();

    // =======================================================================================
    // initialize(): dijalankan sekali ketika FXML pertama kali di‐load
    // =======================================================================================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1) Tampilkan greeting dengan nama user (jika ada) + tanggal hari ini
        String username = (String) SessionManager.get("user");
        if (username != null && !username.isEmpty()) {
            lblWelcome.setText("Selamat datang, " + username + "!");
        } else {
            lblWelcome.setText("Selamat datang!");
        }
        lblDate.setText(LocalDate.now().format(displayDateFormatter));

        // 2) Tarik (pull) seluruh data terbaru dari database
        loadAllDataFromDatabase();

        // 3) Update seluruh dashboard (PieChart + Label + LineChart)
        updateDashboard();
    }

    // =======================================================================================
    // ================== PRIVATE METHOD: Load data dari SQLite ke in‐memory =================
    // =======================================================================================

    /**
     * loadAllDataFromDatabase():
     * – Mengosongkan allTransactions dan allDebts,
     * – Membaca semua baris dari tabel `transaksi` yang sesuai dengan username aktif,
     *   lalu menyimpannya ke allTransactions,
     * – Membaca semua baris dari tabel `utang_piutang` yang sesuai dengan username aktif,
     *   lalu menyimpannya ke allDebts.
     */
    private void loadAllDataFromDatabase() {
        allTransactions.clear();
        allDebts.clear();

        String username = (String) SessionManager.get("user");
        if (username == null || username.isEmpty()) {
            return; // tidak ada user, maka tidak tarik data
        }

        // ---- 1) Ambil data transaksi ----
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
                String tanggal = rs.getString("tanggal"); // diasumsikan "dd/MM/yyyy"
                String description = rs.getString("description");
                allTransactions.add(new TransactionRecord(kategori, nominal, tanggal, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // ---- 2) Ambil data utang/piutang ----
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
                String status = rs2.getString("status");     // misalnya "lunas" / "belum lunas"
                String tanggal = rs2.getString("tanggal");   // diasumsikan "dd/MM/yyyy"
                String kepada = rs2.getString("kepada");
                allDebts.add(new DebtRecord(nominal, status, tanggal, kepada));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =======================================================================================
    // ================== PRIVATE METHOD: Update seluruh bagian pada Dashboard =================
    // =======================================================================================
    /**
     * updateDashboard():
     * 1) Hitung total saldo (allTransactions: nominal >0 dan <0 ikut dijumlahkan)
     * 2) Isi PieChart pendapatan per kategori (nominal >0)
     * 3) Isi PieChart pengeluaran per kategori (nominal <0 di‐abs)
     * 4) Isi PieChart arus kas: total pemasukan vs total pengeluaran
     * 5) Isi Label total piutang & total hutang dari allDebts
     * 6) Isi LineChart saldo harian untuk 7 hari terakhir (bukan kumulatif, melainkan total transaksi pada tiap tanggal)
     */
    private void updateDashboard() {
        // ===================== 1) TOTAL SALDO =====================
        int totalSaldo = allTransactions.stream()
                .mapToInt(TransactionRecord::getNominal)
                .sum();
        lblBalance.setText(String.format("Rp %,d", totalSaldo).replace(',', '.'));

        // ===================== 2) PIE CHART PENDAPATAN =====================
        Map<String, Integer> incMap = allTransactions.stream()
                .filter(t -> t.getNominal() > 0)
                .collect(Collectors.groupingBy(
                        TransactionRecord::getKategori,
                        Collectors.summingInt(TransactionRecord::getNominal)
                ));
        pieIncomeThisMonth.getData().setAll(
                incMap.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        );
        pieIncomeThisMonth.setLabelsVisible(true);
        pieIncomeThisMonth.setLegendVisible(true);
        pieIncomeThisMonth.setLegendSide(javafx.geometry.Side.BOTTOM);

        // ===================== 3) PIE CHART PENGELUARAN =====================
        Map<String, Integer> outMap = allTransactions.stream()
                .filter(t -> t.getNominal() < 0)
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

        // ===================== 4) PIE CHART ARUS KAS =====================
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

        // ===================== 5) TOTAL HUTANG & PIUTANG =====================
        int totalPiutang = allDebts.stream()
                .filter(d -> d.getNominal() > 0)      // piutang dianggap nominal > 0
                .mapToInt(DebtRecord::getNominal)
                .sum();
        int totalHutang = allDebts.stream()
                .filter(d -> d.getNominal() < 0)     // hutang dianggap nominal < 0 (abs)
                .mapToInt(d -> Math.abs(d.getNominal()))
                .sum();
        lblTotalReceivables.setText(String.format("Rp %,d", totalPiutang).replace(',', '.'));
        lblTotalDebt.setText(String.format("Rp %,d", totalHutang).replace(',', '.'));

        // ===================== 6) LINe CHART SALDO 7 HARI TERAKHIR =====================
        // Kita ingin menampilkan "nominal per hari" (jumlah transaksi pada hari tsb),
        // bukan nilai kumulatif. Jika ingin kumulatif, tinggal dijumlah terus ke atas.
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

            // label yang muncul di sumbu X, misalnya "08 May" atau "8 Mei"
            String labelX = date.format(DateTimeFormatter.ofPattern("d MMM"));

            series.getData().add(new XYChart.Data<>(labelX, sumOfThatDay));
        }
        lineBalanceReport.getData().add(series);
        lineBalanceReport.setLegendVisible(false); // kita tidak perlu legend di sini
        lineBalanceReport.setCreateSymbols(true);  // tampilkan titik per data, bisa di‐off jika tidak diinginkan
    }

    // =======================================================================================
    // ================== KELAS PENDUKUNG (Model) ============================================
    // =======================================================================================

    /**
     * TransactionRecord merepresentasikan satu baris pada tabel "transaksi" (tanpa pk/id).
     * Kolom yang dipakai: kategori, nominal, tanggal, description.
     */
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

    /**
     * DebtRecord merepresentasikan satu baris pada tabel "utang_piutang".
     * Kolom yang dipakai: nominal (positif=piutang, negatif=hutang), status, tanggal, kepada.
     */
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
