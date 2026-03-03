package cellsociety.controller;

import cellsociety.Model.Simulation.driver.DefaultSimulationDriver;
import cellsociety.Model.Simulation.driver.SimulationDriver;
import cellsociety.Model.Simulation.model.SimulationModel;
import cellsociety.view.SimulationRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private final List<PopulationSnapshot> myPopulationHistory;

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
    myDriver = new DefaultSimulationDriver();
    myPopulationHistory = new ArrayList<>();

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
  public void setSimulation(SimulationModel sim, int rows, int cols) {
    myDriver.setSimulation(sim);

    myRenderer.setGrid(rows, cols);
    myRenderer.render(sim.getViewGrid());
    myRenderer.setGeneration(sim.getGeneration());
    syncPopulationAtCurrentGeneration();
  }

  /**
   * Toggles the clicked cell and immediately refreshes the slot.
   *
   * @param row clicked row index
   * @param col clicked column index
   */
  public void toggleCellState(int row, int col) {
    if (!myDriver.hasSimulation()) {
      throw new IllegalStateException("No simulation loaded.");
    }
    myDriver.toggleCellState(row, col);
    refreshFromSimulation();
  }

  /**
   * Re-renders after a step. Safe to call repeatedly; no-op if no simulation.
   */
  private void refreshFromSimulation() {
    if (!myDriver.hasSimulation()) return;

    SimulationModel sim = myDriver.getSimulation();
    myRenderer.render(sim.getViewGrid());
    myRenderer.setGeneration(sim.getGeneration());
    syncPopulationAtCurrentGeneration();
  }

  public Map<Integer, Integer> getCurrentStateCounts() {
    if (!myDriver.hasSimulation()) {
      throw new IllegalStateException("No simulation loaded.");
    }
    return Map.copyOf(myDriver.getSimulation().getStateCounts());
  }

  public List<PopulationSnapshot> getPopulationHistory() {
    return List.copyOf(myPopulationHistory);
  }

  public void syncPopulationAtCurrentGeneration() {
    if (!myDriver.hasSimulation()) {
      throw new IllegalStateException("No simulation loaded.");
    }

    SimulationModel sim = myDriver.getSimulation();
    int generation = sim.getGeneration();
    Map<Integer, Integer> counts = sim.getStateCounts();
    PopulationSnapshot snapshot = new PopulationSnapshot(generation, counts);

    if (myPopulationHistory.isEmpty()) {
      myPopulationHistory.add(snapshot);
      return;
    }

    int lastIndex = myPopulationHistory.size() - 1;
    PopulationSnapshot last = myPopulationHistory.get(lastIndex);
    if (last.getGeneration() == generation) {
      myPopulationHistory.set(lastIndex, snapshot);
      return;
    }

    if (last.getGeneration() > generation) {
      while (!myPopulationHistory.isEmpty()
          && myPopulationHistory.getLast().getGeneration() > generation) {
        myPopulationHistory.removeLast();
      }
      if (!myPopulationHistory.isEmpty()
          && myPopulationHistory.getLast().getGeneration() == generation) {
        myPopulationHistory.set(myPopulationHistory.size() - 1, snapshot);
      } else {
        myPopulationHistory.add(snapshot);
      }
      return;
    }

    myPopulationHistory.add(snapshot);
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

  public static final class PopulationSnapshot {
    private final int myGeneration;
    private final Map<Integer, Integer> myCounts;

    public PopulationSnapshot(int generation, Map<Integer, Integer> counts) {
      myGeneration = generation;
      myCounts = Map.copyOf(counts);
    }

    public int getGeneration() {
      return myGeneration;
    }

    public Map<Integer, Integer> getCounts() {
      return myCounts;
    }
  }
}
