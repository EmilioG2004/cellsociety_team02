package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;

/**
 * Full grid interface: read-only view plus mutation and copy operations.
 * Concrete implementations (e.g. ArrayGrid) are created by SimulationFactory and passed
 * to Simulation as this interface, so Simulation stays decoupled from the concrete type.
 *
 * @author Michael Lei
 */
public interface Grid extends ReadOnlyGrid {

  /**
   * Sets the cell at (row, col) to the given state.
   */
  void setCellState(int row, int col, CellState state);

  /**
   * Returns a new, empty grid of the same concrete type, dimensions, and NeighborStrategy
   * as this grid. All cell states are unset (null). Used by rules to create the
   * next-generation grid without knowing the concrete type.
   */
  Grid emptyCopy();

  /**
   * Returns a new grid of the same concrete type, dimensions, and NeighborStrategy
   * with the same cell states copied. Mutating the returned grid does not affect this grid.
   */
  Grid deepCopy();
}
