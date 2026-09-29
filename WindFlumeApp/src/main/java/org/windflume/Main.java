package org.windflume;

import javax.swing.SwingUtilities;
import org.windflume.ui.Canvas;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Canvas();
        });
    }
}