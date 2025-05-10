package UtangPiutang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

<<<<<<<< HEAD:Homepage/src/main/java/UtangPiutang/FinancialApplication.java
public class FinancialApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FinancialApplication.class.getResource("Register.fxml"));
========
public class UtangPiutangApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UtangPiutangApp.class.getResource("/id/ac/ukdw/www/rplbo/homepage/UtangPiutang.fxml"));
>>>>>>>> Marvin_71230971:Homepage/src/main/java/UtangPiutang/UtangPiutangApp.java
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Financial Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}