module id.ac.ukdw.www.rplbo.homepage {
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.materialdesignicons;
    requires javafx.controls;


    opens id.ac.ukdw.www.rplbo.homepage to javafx.fxml;
    exports UtangPiutang;

    opens UtangPiutang to javafx.fxml;


}