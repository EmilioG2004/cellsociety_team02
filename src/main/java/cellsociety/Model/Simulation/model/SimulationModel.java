package cellsociety.Model.Simulation.model;

import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.ControllerGrid;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.Grid.ViewGrid;
import java.util.Map;

/**
 * Interface for a single simulation run: grid, rule, generation count,
 * and mutations (toggle cell, set state). The controller and driver depend on this interface;
 *
 * @author Michael Lei
 */
public interface SimulationModel {

  /**
   * Returns the current grid as a ViewGrid (dimensions, state ints, neighbor strategy).
   * Use for rendering and serialization.
   */
  ViewGrid getViewGrid();

  /**
   * Returns a read-only view of the current grid (cell states, neighbors, bounds).
   */
  ReadOnlyGrid getReadOnlyGrid();

  /**
   * Returns a controller-facing view of the current grid (dimensions, state ints,
   * plus neighbor-strategy get/set).
   */
  ControllerGrid getControllerGrid();

  /**
   * Returns the number of steps taken since the initial state (0 for initial).
   */
  int getGeneration();

  /**
   * Returns the rule that drives this simulation (for config/metadata).
   */
  GameRule getRule();

  /**
   * Toggles the cell at (row, col) to the next state in this simulation's cycle.
   */
  void toggleCellState(int row, int col);

  /**
   * Resets the grid to the given state and clears undo history.
   *
   * @param state the new grid state (will be deep-copied)
   */
  void setState(Grid state);

  /**
   * Advances the simulation one step (next generation). Called by the driver.
   */
  void step();

  /**
   * Reverts to the previous generation. Called by the driver.
   * @throws IllegalStateException if already at the initial state
   */
  void stepBack();

  /**
   * Returns counts of each cell-state integer in the current grid.
   * Keys are state ints and values are populations of that state.
   */
  Map<Integer, Integer> getStateCounts();
}
