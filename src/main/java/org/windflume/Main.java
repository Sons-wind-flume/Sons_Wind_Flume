package org.windflume;

import javax.swing.SwingUtilities;

import org.windflume.config.ConfigLoader;
import org.windflume.importer.Reader1;
import org.windflume.ui.Canvas;
import org.windflume.ui.SettingsMenu;

public class Main {

    public static void main(String[] args) {
        ConfigLoader.Result loaded = ConfigLoader.load();
        Reader1.config = loaded.config;

        SwingUtilities.invokeLater(() -> {
            Canvas canvas = new Canvas();
            SettingsMenu.setUp(canvas, loaded);
        });
    }
}