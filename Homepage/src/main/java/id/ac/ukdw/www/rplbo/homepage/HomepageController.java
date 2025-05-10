package id.ac.ukdw.www.rplbo.homepage;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomepageController implements Initializable {

    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

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

    @FXML
    void onDebtClick(ActionEvent event) {

    }

    @FXML
    void onHomeClick(ActionEvent event) {
        // Sudah berada di halaman ini
    }

    @FXML
    void onLogoutClick(ActionEvent event) {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}