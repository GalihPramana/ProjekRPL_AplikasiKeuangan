module id.ac.ukdw.www.rplbo.homepage {
    requires javafx.controls;
    requires javafx.fxml;


    opens id.ac.ukdw.www.rplbo.homepage to javafx.fxml;
    exports id.ac.ukdw.www.rplbo.homepage;
}