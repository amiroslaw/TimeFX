package ovh.miroslaw.timefx;

import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Application;
import javafx.stage.Stage;
import ovh.miroslaw.timefx.area.AreaView;
import ovh.miroslaw.timefx.pie.PieView;

import static ovh.miroslaw.timefx.model.ViewEnum.AREA;
import static ovh.miroslaw.timefx.model.ViewEnum.PIE;

public class TimeFX extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("TimeFX");

        final PieView pieView = new PieView();
        final AreaView areaView = new AreaView();
        SceneOne.set(PIE.name(), pieView.getPane()).centered().build();
        SceneOne.set(AREA.name(), areaView.getPane()).centered().build();
        SceneOne.show(AREA.name());
        final String pieChartStyle = getClass().getResource("/css/pie-chart.css").toExternalForm();
        SceneOne.setStyleSheetsForAll(pieChartStyle);
    }

    public static void main(String[] args) {
        launch();
    }
}

