public class App {

    public static void main(String[] args) {

        TrialConfig config;
        boolean configWarning = false;

        try {

            config = ConfigLoader.loadConfig(
                    "config.json"
            );

        } catch (Exception e) {

            config = TrialConfig.defaults();
            configWarning = true;
        }

        new Canvas(
                config,
                configWarning
        );
    }
}