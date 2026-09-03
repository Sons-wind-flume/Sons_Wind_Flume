import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        // Window title
        Frame window = new Frame("Dashboard");
        Label title = new Label("Dashboard", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        // Trial
        Label trialTitle = new Label("Trial in progress");
        trialTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        Panel trialFields = new Panel(new GridLayout(6, 2, 10, 5));

        trialFields.add(new Label("Current velocity"));
        trialFields.add(new Label("1.24 m/s"));

        trialFields.add(new Label("Min velocity"));
        trialFields.add(new Label("0.82 m/s"));

        trialFields.add(new Label("Max velocity"));
        trialFields.add(new Label("1.83 m/s"));

        trialFields.add(new Label("Duration"));
        trialFields.add(new Label("00:14"));

        trialFields.add(new Label("Median CD"));
        trialFields.add(new Label("0.41"));

        trialFields.add(new Label("Max force"));
        trialFields.add(new Label("12.7 N"));

        Panel trialSection = new Panel(new BorderLayout(0, 10));
        trialSection.add(trialTitle, BorderLayout.NORTH);
        trialSection.add(trialFields, BorderLayout.CENTER);

        // Water velocity graph
        Label graphTitle = new Label("Water velocity over time");
        graphTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        Panel graph = new Panel();

        Panel graphSection = new Panel(new BorderLayout(0, 10));
        graphSection.add(graphTitle, BorderLayout.NORTH);
        graphSection.add(graph, BorderLayout.CENTER);

        // Layout
        Panel content = new Panel(new GridLayout(2, 1, 0, 20));
        content.add(trialSection);
        content.add(graphSection);

        Panel root = new Panel(new BorderLayout(20, 20));
        root.add(title, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        window.add(root);

        window.setSize(600, 500);
        window.setMinimumSize(new Dimension(400, 350));

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
