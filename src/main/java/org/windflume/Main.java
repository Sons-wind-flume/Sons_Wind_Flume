package org.windflume;

import javax.swing.SwingUtilities;

import org.windflume.config.ConfigLoader;
import org.windflume.importer.Reader1;
import org.windflume.ui.Canvas;

public class Main {

    public static void main(String[] args) {
        ConfigLoader.Result loaded = ConfigLoader.load();
        Reader1.config = loaded.config;
        
        SwingUtilities.invokeLater(() -> {
            new Canvas();
        });
    }
}