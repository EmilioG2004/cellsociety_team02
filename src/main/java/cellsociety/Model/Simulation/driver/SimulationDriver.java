package cellsociety.Model.Simulation.driver;

import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Simulation.model.SimulationModel;

/**
 * API for controlling a simulation run: start/pause, speed, direction, step/stepBack,
 * and delegating to the current {@link SimulationModel}. The view/controller depend on this
 * interface; the default implementation is {@link DefaultSimulationDriver}.
 *
 * @author Michael Lei
 */
public interface SimulationDriver {

  /** Sets the current simulation. If running, the next tick will use this simulation. */
  void setSimulation(SimulationModel simulation);

  /**
   * Sets the simulation's grid to the given state and updates its undo stack.
   * No-op if no simulation set.
   */
  void setState(Grid state);

  /** Toggles the cell at (row, col) to the next state in the simulation's cycle. */
  void toggleCellState(int row, int col);

  /** Returns true if a simulation has been set. */
  boolean hasSimulation();

  /**
   * Returns the current simulation.
   * @throws IllegalStateException if no simulation has been set
   */
  SimulationModel getSimulation();

  /**
   * Registers a callback invoked after each step (e.g. for view refresh).
   * May be called from the driver's thread; use Platform.runLater for UI updates.
   */
  void setOnStepCallback(Runnable onStepCallback);

  /** Returns true if the driver is currently running (stepping periodically). */
  boolean isRunning();

  /** Returns the current stepping rate in steps per second. */
  double getStepsPerSecond();

  /** Starts periodic stepping at the current speed. No-op if already running or no simulation set. */
  void start();

  /** Stops periodic stepping. Single-step via {@link #step()} still works when paused. */
  void pause();

  /** Sets the stepping rate in steps per second. If running, reschedules at the new rate. */
  void setSpeed(double stepsPerSecond);

  /** Advances the current simulation by one generation and notifies the step callback. No-op if no simulation set. */
  void step();

  /** Sets whether stepping goes forward (true) or backward (false). */
  void setDirection(boolean forward);

  /** Returns true if direction is forward, false if backward. */
  boolean isForwardDirection();

  /**
   * Goes back one generation and notifies the step callback.
   * @return true if step back was performed, false if no simulation or already at initial state
   */
  boolean stepBack();
}
