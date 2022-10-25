module ovh.miroslaw.timefx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.databind;
    requires com.simtechdata.sceneonefx;

    opens ovh.miroslaw.timefx to javafx.fxml;
    exports ovh.miroslaw.timefx;
    exports ovh.miroslaw.timefx.model;
    opens ovh.miroslaw.timefx.model to javafx.fxml;
    exports ovh.miroslaw.timefx.pie;
    opens ovh.miroslaw.timefx.pie to javafx.fxml;
    exports ovh.miroslaw.timefx.area;
    opens ovh.miroslaw.timefx.area to javafx.fxml;
}
