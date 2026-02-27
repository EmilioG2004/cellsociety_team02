package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
/**
 * Mutable grid of cell states. Extends ReadOnlyGrid with methods to update state
 * and obtain a full snapshot.
 */
public interface MutableGrid extends ReadOnlyGrid {

  /** Sets the state of the cell at (row, col). */
  void setCellState(int row, int col, CellState state);

  /** Replaces the entire grid state; dimensions must match. */
  void setCellsState(CellState[][] state);

  /** Returns a new copy of the current grid state (row-major). */
  CellState[][] getCellStates();
}
