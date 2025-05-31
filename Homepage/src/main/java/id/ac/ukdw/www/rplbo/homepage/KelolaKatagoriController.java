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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class KelolaKatagoriController {
    @FXML
    private Button btnDebt;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnKategori;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnTambahKategori;

    @FXML
    private Button btnTransaction;

    @FXML
    private Button clearButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<KelolaKategori, String> kategoriColumn;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTotalKategori;

    @FXML
    private Label lblWelcome;

    @FXML
    private TableView<KelolaKategori> tableViewKategori;

    @FXML
    private TextField txtFilter;

    @FXML
    private TextField txtNamaKategori;

    @FXML
    private Button updateButton;

    @FXML
    private Button logbutton;

    private ObservableList<KelolaKategori> dataKategori = FXCollections.observableArrayList();

    public void initialize() {
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        tableViewKategori.setEditable(true);
        tableViewKategori.setItems(dataKategori);
    }

    @FXML
    void onClearClick(ActionEvent event) {
        tableViewKategori.getSelectionModel().clearSelection();
        txtFilter.clear();
        txtNamaKategori.clear();
        txtNamaKategori.requestFocus();
    }

    @FXML
    void onDeleteClick(ActionEvent event) {
        KelolaKategori selected = tableViewKategori.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dataKategori.remove(selected);
        }
    }

    @FXML
    void onKeyFilter(KeyEvent event) {

    }


    @FXML
    void onTambahkanClick(ActionEvent event) {
        String txtKategori = txtNamaKategori.getText();
        if (txtKategori.isEmpty()) {
            showAlert("Nama kategori tidak boleh kosong");
        } else {
            tableViewKategori.setItems(dataKategori);
        }
    }

    @FXML
    void onUpdateClick(ActionEvent event) {

    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
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

    @FXML
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

    @FXML
    public void onDebtClick(ActionEvent actionEvent) {
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
    void onKategoriClick(ActionEvent event) {
        // Sudah berada di halaman ini
    }

    @FXML
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

