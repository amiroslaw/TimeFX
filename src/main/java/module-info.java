module ovh.miroslaw.timefx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.databind;

    opens ovh.miroslaw.timefx to javafx.fxml;
    exports ovh.miroslaw.timefx;
}
