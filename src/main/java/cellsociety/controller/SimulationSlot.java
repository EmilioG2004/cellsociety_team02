package cellsociety.controller;

import cellsociety.Model.Simulation.Simulation;
import cellsociety.Model.Simulation.SimulationFactory;
import cellsociety.config.SimulationConfig;
import cellsociety.view.SimulationPanel;
import javafx.scene.Node;

/**
 * Container for a single multi-simulation slot (panel + presenter/driver).
 *
 * <p>Purpose: provides a uniform interface for {@link MultiSimController} to
 * manage multiple panels and their simulations.</p>
 *
 * <p>Assumptions: {@link SimulationPresenter} is non-null and manages its own
 * driver lifecycle.</p>
 *
 * <p>Dependencies: {@link SimulationPanel}, {@link SimulationPresenter}, and
 * {@link SimulationFactory} for resets.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationSlot slot = new SimulationSlot(index, panel, presenter);
 * slot.setSimulation(sim);
 * slot.start();
 * }</pre>
 *
 * @author Emilio Garcia
 */
public class SimulationSlot {

  private final int myIndex;
  private final SimulationPanel myPanel;
  private final SimulationPresenter myPresenter;
  private SimulationConfig myConfig;

  /**
   * Creates a slot wrapper for the given panel and presenter.
   *
   * <p>Assumptions: {@code panel} and {@code presenter} are non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param index zero-based slot index
   * @param panel render-only panel
   * @param presenter presenter/driver for the slot
   */
  public SimulationSlot(int index, SimulationPanel panel, SimulationPresenter presenter) {
    myIndex = index;
    myPanel = panel;
    myPresenter = presenter;
    myPanel.setOnCellClicked(myPresenter::toggleCellState);
  }

  /**
   * Returns this slot's index.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return zero-based slot index
   */
  public int getIndex() {
    return myIndex;
  }

  /**
   * Returns the panel's node for embedding in layouts.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return panel root node
   */
  public Node getNode() {
    return myPanel.getNode();
  }

  /**
   * Returns the presenter that drives this slot.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return slot presenter
   */
  public SimulationPresenter getPresenter() {
    return myPresenter;
  }

  /**
   * Installs a simulation and renders it immediately.
   *
   * <p>Assumptions: {@code sim} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param sim simulation to display
   */
  public void setSimulation(Simulation sim) {
    myPresenter.setSimulation(sim, sim.getGridViewData().getRows(), sim.getGridViewData().getCols());
  }

  /**
   * Stores the configuration used for future resets.
   *
   * <p>Assumptions: {@code config} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param config configuration snapshot
   */
  public void setConfig(SimulationConfig config) {
    myConfig = config;
  }

  /**
   * Returns this slot's stored configuration.
   *
   * @return configuration snapshot used to build the current simulation
   */
  public SimulationConfig getConfig() {
    if (myConfig == null) {
      throw new IllegalStateException("No configuration set for slot " + myIndex);
    }
    return myConfig;
  }

  /**
   * Resets this slot to its stored initial configuration.
   *
   * <p>Assumptions: {@link #setConfig(SimulationConfig)} has been called.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void reset() {
    if (myConfig == null) throw new IllegalArgumentException("No configuration set.");
    Simulation sim = SimulationFactory.createSimulation(myConfig);
    setSimulation(sim);
  }

  /**
   * Toggles grid outlines for this slot.
   *
   * <p>Assumptions: a simulation is installed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void toggleOutline() {
    if (!hasSimulation()) return;
    myPanel.toggleOutline(myPresenter.getDriver().getSimulation().getGridViewData());
  }

  /**
   * Starts the simulation driver for this slot.
   *
   * <p>Assumptions: a simulation is installed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void start() {
    myPresenter.getDriver().start();
  }

  /**
   * Pauses the simulation driver for this slot.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void pause() {
    myPresenter.getDriver().pause();
  }

  /**
   * Steps the simulation forward once for this slot.
   *
   * <p>Assumptions: a simulation is installed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   */
  public void step() {
    myPresenter.getDriver().step();
  }

  /**
   * Steps the simulation backward once for this slot.
   *
   * <p>Assumptions: a simulation is installed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return true if the step-back succeeded, false if no history is available
   */
  public boolean stepBack() {
    return myPresenter.getDriver().stepBack();
  }

  /**
   * Indicates whether this slot currently has a simulation installed.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return true if a simulation is present
   */
  public boolean hasSimulation() {
    return myPresenter.getDriver().hasSimulation();
  }

  /**
   * Returns whether this slot is stepping forward.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return true if forward direction is enabled
   */
  public boolean isForwardDirection() {
    return myPresenter.getDriver().isForwardDirection();
  }

  /**
   * Sets the stepping direction for this slot.
   *
   * <p>Assumptions: a simulation is installed.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param forward true for forward, false for backward
   */
  public void setDirection(boolean forward) {
    myPresenter.getDriver().setDirection(forward);
  }

  /**
   * Sets the stepping speed for this slot.
   *
   * <p>Assumptions: {@code stepsPerSecond} is positive.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param stepsPerSecond target steps per second
   */
  public void setSpeed(double stepsPerSecond) {
    myPresenter.getDriver().setSpeed(stepsPerSecond);
  }

  public double getSpeed() { return myPresenter.getDriver().getStepsPerSecond(); }

  /**
   * Returns the current stepping speed for this slot.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return steps per second
   */
  public double getStepsPerSecond() {
    return myPresenter.getDriver().getStepsPerSecond();
  }

  public SimulationPanel getMyPanel() { return myPanel; }
}
