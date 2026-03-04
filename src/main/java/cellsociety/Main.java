package cellsociety;

import static cellsociety.view.constants.SimulationUiConstants.DATA_FILE_FOLDER;
import static cellsociety.view.constants.SimulationUiConstants.VERSION_KEY;
import static cellsociety.view.constants.SimulationUiConstants.XML_FILE_EXTENSION;

import cellsociety.controller.SimulationController;
import cellsociety.view.SimulationView;
import cellsociety.view.constants.ResourcePaths;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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
