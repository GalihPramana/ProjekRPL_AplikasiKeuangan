package id.ac.ukdw.www.rplbo.homepage;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

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

    }

    @FXML
    void onLogoutClick(ActionEvent event) {

    }

    @FXML
    void onTransactionClick(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}