package id.ac.ukdw.www.rplbo.homepage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import id.ac.ukdw.www.rplbo.homepage.util.SessionManager;

public class FinancialApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader;

        if(SessionManager.isLoggedIn()){
        loader = new FXMLLoader(getClass().getResource("homepage-view.fxml"));
        }else {
            loader = new FXMLLoader(getClass().getResource("login.fxml"));
        }
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Financial Application");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        SessionManager.loadSession();
        launch(args);
    }
}