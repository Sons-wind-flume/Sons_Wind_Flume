package org.windflume;

import javax.swing.SwingUtilities;

import org.windflume.importer.Reader1;

import org.windflume.ui.Canvas;

public class Main {

    public static void main(String[] args) {
        Reader1.readFile("config.txt");
        SwingUtilities.invokeLater(() -> {
            new Canvas();
        });
    }
}