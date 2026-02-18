package cellsociety.controller;

import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationDriver;
import cellsociety.view.SimulationRenderer;
import javafx.application.Platform;

/**
 * A lightweight presenter/controller for driving a render-only simulation view.
 *
 * Purpose:
 * Enables multiple simultaneous simulation displays by pairing one SimulationDriver
 * with one SimulationRenderer.
 *
 * @author Emilio Garcia
 */
public class SimulationPresenter {

  private final SimulationRenderer myRenderer;
  private final SimulationDriver myDriver;

  public SimulationPresenter(SimulationRenderer renderer) {
    myRenderer = renderer;
    myDriver = new SimulationDriver();

    myDriver.setOnStepCallback(() -> Platform.runLater(this::refreshFromSimulation));
  }

  /**
   * Installs a simulation and renders it immediately.
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

  public SimulationDriver getDriver() {
    return myDriver;
  }
}
