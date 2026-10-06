package org.windflume.ui;

public enum GaugeRole {
    SPHERE("Sphere"),
    TEST_OBJECT("Test Object");

    private final String label;

    GaugeRole(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
