package ovh.miroslaw.timefx;

import com.simtechdata.sceneonefx.SceneOne;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.Chart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ovh.miroslaw.timefx.model.Task;

import java.util.List;

import static ovh.miroslaw.timefx.model.ViewEnum.AREA;
import static ovh.miroslaw.timefx.model.ViewEnum.PIE;

public interface View {

    default MenuButton createMenu() {
        MenuButton viewMenu = new MenuButton("_Change chart");
        MenuItem pieMenu = new MenuItem(PIE.getLabel());
        MenuItem areaMenu = new MenuItem(AREA.getLabel());
        pieMenu.setOnAction(actionEvent -> SceneOne.show(PIE.name()));
        areaMenu.setOnAction(actionEvent -> SceneOne.show(AREA.name()));
        viewMenu.getItems().addAll(pieMenu, areaMenu);
        return viewMenu;
    }

    default VBox createMainPane(Chart chart, List<ChoiceBox<String>> choiceBoxes) {
        final MenuButton menu = createMenu();
        final HBox menuBox = new HBox();

        menuBox.setSpacing(20);
        menuBox.getChildren().add(menu);
        menuBox.getChildren().addAll(choiceBoxes);
        final VBox mainPane = new VBox();
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setPadding(new Insets(20));
        mainPane.getChildren().addAll(menuBox, chart);
        return mainPane;
    }

    Pane getPane(List<Task> tasks);

}
