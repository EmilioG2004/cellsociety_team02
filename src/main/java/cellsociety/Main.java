package cellsociety;

import cellsociety.controller.SimulationController;
import cellsociety.view.SimulationView;
import cellsociety.view.constants.ResourcePaths;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Feel free to completely change this code or delete it entirely. 
 */
public class Main extends Application {

    /**
     * @see Application#start(Stage)
     */
    @Override
    public void start (Stage primaryStage) {
        ResourceBundle bundle = ResourceBundle.getBundle(ResourcePaths.BUNDLE_ENGLISH);
        buildGUI(primaryStage, bundle);
    }

    private void buildGUI(Stage stage, ResourceBundle bundle){
        SimulationView view = new SimulationView(bundle);
        Scene scene = new Scene(view.getRoot());
        SimulationController controller = new SimulationController(view, scene, bundle);

        stage.setScene(scene);
        view.applyTheme(scene, "Light");
        stage.setMaximized(true);

        stage.show();
    }

    /**
     * Start the program, give complete control to JavaFX.
     *
     * Default version of main() is actually included within JavaFX, so this is not technically necessary!
     */
    public static void main (String[] args) {
        launch(args);
    }
}
