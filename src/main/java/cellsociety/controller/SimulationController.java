package cellsociety.controller;

import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationDriver;
import cellsociety.view.SimulationView;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import java.text.MessageFormat;
import javafx.application.Platform;
import javafx.scene.Scene;

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
  private final Scene myScene;
  private SimulationConfig initialConfig;
  private final ConfigSaver myConfigSaver;
  private final WiringManager myWires;
  private final MultiSimController myMultiController;
  private boolean myMultiMode;

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
  public SimulationController(SimulationView simView, Scene scene) {
    mySimView = simView;
    myDriver = new SimulationDriver();
    myConfigLoader = new ConfigLoader();
    myConfigSaver = new ConfigSaver();
    myScene = scene;
    myMultiController = new MultiSimController(mySimView.getMyBundle());
    myWires = new WiringManager(
        mySimView,
        myDriver,
        myConfigLoader,
        myConfigSaver,
        myScene,
        this::setSimulation,
        this::setInitialConfig,
        () -> initialConfig,
        this:: openMultiSimDashboard,
        this::isMultiMode,
        () -> myMultiController.startAllLoaded(),
        () -> myMultiController.pauseAll(),
        () -> myMultiController.stepAllLoaded(),
        () -> myMultiController.stepBackAllLoaded(),
        () -> myMultiController.resetAllLoaded(),
        () -> myMultiController.toggleOutlineAll(),
        () -> myMultiController.speedUpAll(2.0),
        () -> myMultiController.slowDownAll(2.0),
        () -> myMultiController.toggleDirectionAll()
    );
    myMultiMode = false;

    myWires.wireAll();

    myDriver.setOnStepCallback(() -> Platform.runLater(this::refreshViewFromSimulation));
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
    myMultiMode = false;
    myDriver.setSimulation(sim);
    mySimView.getMyGridView().render(sim.getReadOnlyGrid(), sim.getRule().getStateColors());
    mySimView.getMyGridView().setOnCellClicked((row, col) -> {
      if (!myDriver.hasSimulation()) return;
      myDriver.toggleCellState(row, col);
      Platform.runLater(this::refreshViewFromSimulation);
    });

    updateGenerationLabel();
  }

  /**
 * Re-renders the grid view and stack size label from the driver's current simulation.
 * No-op if no simulation is set. Call on the JavaFX thread (e.g. via Platform.runLater).
 */
  private void refreshViewFromSimulation() {
    if (!myDriver.hasSimulation()) return;
    Simulation sim = myDriver.getSimulation();
    mySimView.getMyGridView().render(sim.getReadOnlyGrid(), sim.getRule().getStateColors());
    updateGenerationLabel();
  }

  public void setInitialConfig(SimulationConfig config) { this.initialConfig = config; }

  private void updateGenerationLabel() {
    Simulation sim = myDriver.getSimulation();
    if (sim == null) return;

    int generation = sim.getGeneration();

    mySimView.getMyButtons().getStateStackSizeLabel()
        .setText(MessageFormat.format(mySimView.getMyBundle().getString("label.generation"), generation));
  }

  public void openMultiSimDashboard() {
    myMultiMode = true;
    mySimView.setCenterContent(myMultiController.getNode());
  }

  public boolean isMultiMode() {
    return myMultiMode;
  }
}
