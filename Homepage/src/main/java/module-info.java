module id.ac.ukdw.www.rplbo.homepage {
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.materialdesignicons;
    requires javafx.controls;
    requires java.sql;


    opens id.ac.ukdw.www.rplbo.homepage to javafx.fxml;
    exports id.ac.ukdw.www.rplbo.homepage;


}