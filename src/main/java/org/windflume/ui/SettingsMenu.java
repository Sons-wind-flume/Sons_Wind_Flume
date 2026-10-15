package org.windflume.ui;

import java.awt.Desktop;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.windflume.config.ConfigLoader;

public final class SettingsMenu {

    private SettingsMenu() {
    }

    public static void setUp(JFrame window, ConfigLoader.Result loaded) {
        window.setJMenuBar(createMenuBar(window, loaded.path));

        if (!loaded.warnings.isEmpty()) {
            showWarnings(window, loaded);
        }
    }

    private static JMenuBar createMenuBar(JFrame window, Path configFile) {
        JMenuItem openFile = new JMenuItem("Open config file");
        openFile.addActionListener(e -> open(window, configFile));

        JMenuItem openFolder = new JMenuItem("Show config folder");
        openFolder.addActionListener(e -> open(window, configFile.toAbsolutePath().getParent()));

        JMenuItem restartNote = new JMenuItem("Changes apply after restarting the app");
        restartNote.setEnabled(false); 

        JMenu menu = new JMenu("Settings (staff)");
        menu.add(openFile);
        menu.add(openFolder);
        menu.addSeparator();
        menu.add(restartNote);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        return menuBar;
    }

    private static void open(JFrame window, Path path) {
        try {
            Desktop.getDesktop().open(path.toFile());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(window,
                    "It could not be opened automatically. You can find it here:\n" + path,
                    "Settings", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void showWarnings(JFrame window, ConfigLoader.Result loaded) {
        StringBuilder message = new StringBuilder(
                "Some settings in the config file could not be used, so defaults were used instead:\n\n");
        for (String warning : loaded.warnings) {
            message.append("- ").append(warning).append("\n");
            System.out.println("Config warning: " + warning);
        }
        message.append("\nConfig file: ").append(loaded.path);

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(window, message.toString(),
                "Configuration warning", JOptionPane.WARNING_MESSAGE));
    }
}