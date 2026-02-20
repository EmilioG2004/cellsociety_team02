package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
/**
 * Mutable grid of cell states. Extends ReadOnlyGrid with methods to update state
 * and obtain a full snapshot. Implementations hide the internal data structure.
 * Uses {@link CellFactory} for cell creation so the grid does not depend on game rules.
 */
public interface MutableGrid extends ReadOnlyGrid {

  /** Sets the state of the cell at (row, col). Creates a new cell if needed using the factory. */
  void setCellState(int row, int col, CellState state, CellFactory cellFactory);

  /** Replaces the entire grid state; dimensions must match. Creates cells using the factory. */
  void setCellsState(CellState[][] state, CellFactory cellFactory);

  /** Returns a new copy of the current grid state (row-major). */
  CellState[][] getCellStates();
}
