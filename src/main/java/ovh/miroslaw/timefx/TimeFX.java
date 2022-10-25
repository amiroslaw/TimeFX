package ovh.miroslaw.timefx;

import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Application;
import javafx.stage.Stage;
import ovh.miroslaw.timefx.area.AreaView;
import ovh.miroslaw.timefx.model.Task;
import ovh.miroslaw.timefx.pie.PieView;

import java.util.List;

import static ovh.miroslaw.timefx.model.ViewEnum.AREA;
import static ovh.miroslaw.timefx.model.ViewEnum.PIE;

// results will be different form timewarrior - this application doesn't split tasks at the moon.
public class TimeFX extends Application {

    final List<Task> tasks = DataReader.getData();

    @Override
    public void start(Stage stage) {
        stage.setTitle("TimeFX");

        final PieView pieView = new PieView();
        final AreaView areaView = new AreaView();
        SceneOne.set(PIE.name(), pieView.getPane(tasks)).centered().build();
        SceneOne.set(AREA.name(), areaView.getPane(tasks)).centered().build();
        SceneOne.show(AREA.name());
        final String pieChartStyle = getClass().getResource("/css/pie-chart.css").toExternalForm();
        SceneOne.setStyleSheetsForAll(pieChartStyle);
    }

    public static void main(String[] args) {
        launch();
    }
}

