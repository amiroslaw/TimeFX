package ovh.miroslaw.timefx.model;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ovh.miroslaw.timefx.EnumHelper;

import java.util.function.Predicate;

public enum TagType {
    CONTEXTS(a -> a.startsWith("+")),
    PROJECTS(a -> a.startsWith("%")),
    TASKS(a -> !a.startsWith("%") && !a.startsWith("+"));

    private final Predicate<String> predicate;

    public static ChoiceBox<String> createChoiceBox() {
        ChoiceBox<String> tagTypes = new ChoiceBox<>(
                FXCollections.observableArrayList(EnumHelper.getNames(TagType.class)));
        tagTypes.setValue(EnumHelper.getLowerCase(CONTEXTS));
        return tagTypes;
    }

    TagType(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    public boolean test(String type) {
        return predicate.test(type);
    }

    public static TagType ofLowerCase(String lowerCase) {
        return TagType.valueOf(lowerCase.toUpperCase());
    }
}
