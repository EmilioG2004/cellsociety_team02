package cellsociety.controller;

import cellsociety.Model.Simulation.Simulation;
import cellsociety.Model.Simulation.SimulationDriver;
import cellsociety.view.SimulationView;
import cellsociety.config.ConfigLoader;
import cellsociety.config.ConfigSaver;
import cellsociety.config.SimulationConfig;
import java.text.MessageFormat;
import javafx.application.Platform;
import javafx.scene.Scene;

/**
 * Primary controller that wires {@link SimulationView} to simulation logic.
 *
 * <p>Purpose: connects UI controls to a {@link SimulationDriver}, handles configuration
 * loading/saving through the {@link cellsociety.config} package, and refreshes the view
 * after simulation steps.</p>
 *
 * <p>Assumptions: {@link SimulationView} is fully initialized before wiring; configuration
 * files are valid and compatible with {@link cellsociety.config.ConfigLoader}.</p>
 *
 * <p>Dependencies: {@link SimulationView}, {@link SimulationDriver}, configuration loaders/savers,
 * and JavaFX {@link Platform} for UI thread updates.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationView view = new SimulationView(bundle);
 * Scene scene = new Scene(view.getRoot());
 * SimulationController controller = new SimulationController(view, scene);
 * }</pre>
 *
 * <p>Other details: when multi-sim mode is active, some controls route to
 * {@link MultiSimController} instead of the single-simulation driver.</p>
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
   * Constructs a controller and wires all UI controls.
   *
   * <p>Purpose: initializes the driver, loaders/savers, and the multi-sim controller, then
   * registers callbacks to refresh the UI after each step.</p>
   *
   * <p>Assumptions: {@code simView} is non-null and already constructed; {@code scene} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param simView the view whose controls are connected to simulation behavior
   * @param scene the scene used for theme updates
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
   * Installs a simulation into the driver and renders it immediately.
   *
   * <p>Purpose: allows a simulation to be installed programmatically without the upload flow.</p>
   *
   * <p>Assumptions: {@code sim} is non-null and provides a complete state-color mapping.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
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

  /**
   * Stores the initial configuration used for reset/save operations.
   *
   * <p>Purpose: enables resets to the original configuration snapshot.</p>
   *
   * <p>Assumptions: {@code config} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param config initial configuration associated with the current simulation
   */
  public void setInitialConfig(SimulationConfig config) { this.initialConfig = config; }

  private void updateGenerationLabel() {
    Simulation sim = myDriver.getSimulation();
    if (sim == null) return;

    int generation = sim.getGeneration();

    mySimView.getMyButtons().getStateStackSizeLabel()
        .setText(MessageFormat.format(mySimView.getMyBundle().getString("label.generation"), generation));
  }

  /**
   * Switches the center pane to the multi-simulation dashboard.
   *
   * <p>Purpose: allows users to view and control multiple simulations simultaneously.</p>
   *
   * <p>Assumptions: the multi-simulation controller is initialized.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void openMultiSimDashboard() {
    myMultiMode = true;
    mySimView.setCenterContent(myMultiController.getNode());
  }

  /**
   * Indicates whether multi-sim mode is currently active.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return true if the multi-simulation dashboard is displayed
   */
  public boolean isMultiMode() {
    return myMultiMode;
  }