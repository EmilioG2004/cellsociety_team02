package cellsociety.controller;

import cellsociety.Model.Simulation.Simulation;
import cellsociety.Model.Simulation.SimulationDriver;
import cellsociety.view.SimulationRenderer;
import javafx.application.Platform;

/**
 * Lightweight presenter that drives a {@link SimulationRenderer} using a {@link SimulationDriver}.
 *
 * <p>Purpose: supports multi-simulation dashboards by pairing a driver with a render-only view
 * and keeping UI updates on the JavaFX thread.</p>
 *
 * <p>Assumptions: the renderer is ready to create a grid when a simulation is set.</p>
 *
 * <p>Dependencies: {@link SimulationDriver}, {@link SimulationRenderer}, and
 * JavaFX {@link Platform} for UI-thread updates.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationPresenter presenter = new SimulationPresenter(panel);
 * presenter.setSimulation(sim, rows, cols);
 * presenter.getDriver().start();
 * }</pre>
 *
 * @author Emilio Garcia
 */
public class SimulationPresenter {

  private final SimulationRenderer myRenderer;
  private final SimulationDriver myDriver;

  /**
   * Creates a presenter for the given renderer.
   *
   * <p>Purpose: sets up a driver and its on-step callback.</p>
   *
   * <p>Assumptions: {@code renderer} is non-null.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param renderer render-only view to update
   */
  public SimulationPresenter(SimulationRenderer renderer) {
    myRenderer = renderer;
    myDriver = new SimulationDriver();

    myDriver.setOnStepCallback(() -> Platform.runLater(this::refreshFromSimulation));
  }

  /**
   * Installs a simulation and renders it immediately.
   *
   * <p>Purpose: initialize the renderer with the simulation's grid and state.</p>
   *
   * <p>Assumptions: {@code sim} is non-null and {@code rows}/{@code cols} match its grid.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * <p>Return value: none.</p>
   *
   * @param sim simulation to display
   * @param rows number of grid rows
   * @param cols number of grid columns
   */
  public void setSimulation(Simulation sim, int rows, int cols) {
    myDriver.setSimulation(sim);

    myRenderer.setGrid(rows, cols);
    myRenderer.render(sim.getReadOnlyGrid(), sim.getRule().getStateColors());
    myRenderer.setGeneration(sim.getGeneration());
  }

  /**
   * Re-renders after a step. Safe to call repeatedly; no-op if no simulation.
   */
  private void refreshFromSimulation() {
    if (!myDriver.hasSimulation()) return;

    Simulation sim = myDriver.getSimulation();
    myRenderer.render(sim.getReadOnlyGrid(), sim.getRule().getStateColors());
    myRenderer.setGeneration(sim.getGeneration());
  }

  /**
   * Returns the underlying simulation driver for this presenter.
   *
   * <p>Assumptions: none.</p>
   *
   * <p>Exceptions: none.</p>
   *
   * @return simulation driver
   */
  public SimulationDriver getDriver() {
    return myDriver;
  }
}
