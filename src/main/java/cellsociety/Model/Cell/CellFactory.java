package cellsociety.Model.Cell;

import cellsociety.Model.CellState.CellState;

/**
 * Creates cells for a grid. Used when initializing or updating grid state so the grid
 * does not depend on game rules—only on this abstraction.
 */
@FunctionalInterface
public interface CellFactory {

  /** Returns a new cell with the given state. */
  Cell createCell(CellState state);
}
