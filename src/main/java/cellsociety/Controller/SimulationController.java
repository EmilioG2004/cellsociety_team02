package cellsociety.Controller;

import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationDriver;
import cellsociety.Model.SimulationFactory;
import cellsociety.View.SimulationView;
import cellsociety.config.ConfigLoader;
import cellsociety.config.SimulationConfig;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Connects user interactions in the SimulationView to the underlying simulation logic.
 * This class is responsible for wiring UI controls to simulation actions, loading
 * configuration files, and triggering rendering updates as the simulation advances.
 *
 * Assumptions:
 * - The SimulationView provided to this controller is fully initialized.
 * - All buttons and the grid view returned by the view are non-null.
 * - A Simulation is set on the SimulationDriver before the driver begins stepping.
 * - Configuration files selected by the user conform to the format expected by ConfigLoader.
 *
 * Dependencies:
 * - SimulationView for UI controls and rendering.
 * - SimulationDriver for managing simulation execution and timing.
 * - SimulationFactory for constructing simulations from configuration data.
 * - ConfigLoader and SimulationConfig for reading configuration files.
 * - JavaFX Platform to ensure UI updates occur on the JavaFX Application Thread.
 *
 * Example usage:
 * SimulationView view = new SimulationView();
 * SimulationController controller = new SimulationController(view);
 *
 * @author Emilio Garcia
 */
public class SimulationController {

  private final SimulationView mySimView;
  private final SimulationDriver myDriver;
  private final ConfigLoader myConfigLoader;
  private SimulationConfig initialConfig;
  private static final double cellSize = 20.0;

  /**
   * Constructs a SimulationController for the given view.
   * Initializes the simulation driver and configuration loader, wires all UI controls,
   * and registers a callback to re-render the grid after each simulation step.
   *
   * Assumptions:
   * - simView is non-null and exposes all required UI components.
   *
   * @param simView the view whose controls are connected to simulation behavior
   */
  public SimulationController(SimulationView simView) {
    this.mySimView = simView;
    this.myDriver = new SimulationDriver();
    this.myConfigLoader = new ConfigLoader();
    wireButtons();

    myDriver.setOnStepCallback(
        () ->
            Platform.runLater(
                () -> {
                  Simulation mySim = myDriver.getSimulation();
                  mySimView
                      .getMyGridView()
                      .render(mySim.getGrid(), mySim.getRule().getStateColors());
                }));
  }

  /**
   * Sets the active simulation in the driver and renders its current state in the view.
   *
   * Purpose:
   * Allows a simulation to be installed programmatically without using the upload button.
   *
   * Assumptions:
   * - sim is non-null.
   * - The simulation's rule provides a complete mapping of states to colors.
   *
   * @param sim the simulation to run and display
   */
  public void setSimulation(Simulation sim) {
    myDriver.setSimulation(sim);
    mySimView.getMyGridView().render(sim.getGrid(), sim.getRule().getStateColors());
  }

  /**
   * Wires all UI buttons in the view to their corresponding controller actions.
   * This method exists to keep constructor logic concise and organized.
   */
  private void wireButtons() {
    wireStart();
    wirePause();
    wireSave();
    wireUpload();
    wireSpeedUp();
    wireSlowDown();
    wireReset();
  }

  /**
   * Wires the Start button to begin stepping the simulation.
   *
   * Assumptions:
   * - The driver can safely handle a start request at the time the button is pressed.
   */
  private void wireStart() {
    mySimView.getMyStartButton().setOnAction(e -> myDriver.start());
  }

  /**
   * Wires the Pause button to stop stepping the simulation.
   */
  private void wirePause() {
    mySimView.getMyPauseButton().setOnAction(e -> myDriver.pause());
  }

  /**
   * Wires the Speed Up button to increase the simulation speed.
   *
   * Assumptions:
   * - The driver accepts the updated speed value.
   */
  private void wireSpeedUp() {
    mySimView
        .getMySpeedUpButton()
        .setOnAction(e -> myDriver.setSpeed(myDriver.getStepsPerSecond() * 2));
  }

  /**
   * Wires the Slow Down button to decrease the simulation speed.
   *
   * Assumptions:
   * - The driver accepts the updated speed value.
   */
  private void wireSlowDown() {
    mySimView
        .getMySlowDownButton()
        .setOnAction(e -> myDriver.setSpeed(myDriver.getStepsPerSecond() / 2.0));
  }

  /**
   * Wires the Save button.
   * This method exists to connect the save control to the controller layer.
   */
  private void wireSave() {
    mySimView.getMySaveButton().setOnAction(e -> {});
  }

  /**
   * Wires the Upload button to load a configuration file, create a simulation,
   * and render the resulting grid.
   *
   * Purpose:
   * Allows users to select a configuration file and replace the current simulation.
   *
   * Assumptions:
   * - The file chooser returns a valid File.
   * - The selected file can be parsed into a SimulationConfig.
   *
   * Exceptions:
   * - ParserConfigurationException if the XML parser cannot be configured.
   * - SAXException if the file does not match the expected XML structure.
   * - IOException if the file cannot be read.
   */
  private void wireUpload() {
    mySimView
        .getMyUploadButton()
        .setOnAction(
            e -> {
              File myFile = mySimView.showOpenConfigDialog();
              try {
                SimulationConfig myConfig = myConfigLoader.loadConfig(myFile);
                setInitialConfig(myConfig);
                Simulation mySim = SimulationFactory.createSimulation(myConfig);
                myDriver.setSimulation(mySim);
                mySimView.setGrid(
                    mySim.getGrid().getRows(),
                    mySim.getGrid().getCols(),
                    cellSize);
                mySimView
                    .getMyGridView()
                    .render(mySim.getGrid(), mySim.getRule().getStateColors());
              } catch (ParserConfigurationException | SAXException | IOException ex) {
                throw new RuntimeException(ex);
              }
            });
  }

  public void setInitialConfig(SimulationConfig config) { this.initialConfig = config; }

  public void wireReset(){
    mySimView.getMyResetButton()
        .setOnAction(e -> {
          if (this.initialConfig == null) { return; }
          Simulation mySim = SimulationFactory.createSimulation(this.initialConfig);
          setSimulation(mySim);
    });
  }
}
