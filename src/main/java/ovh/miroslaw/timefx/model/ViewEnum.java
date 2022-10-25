package ovh.miroslaw.timefx.model;

public enum ViewEnum {
    PIE("_Pie chart"), AREA("_Area chart");

    private final String label;
    ViewEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
