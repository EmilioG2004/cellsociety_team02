package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
/**
 * Mutable grid of cell states.
 */
public interface MutableGrid {

  int getRows();

  int getCols();

  void setCellState(int row, int col, CellState state);
}
