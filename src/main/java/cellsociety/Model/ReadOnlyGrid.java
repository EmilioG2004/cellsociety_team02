package cellsociety.Model;

import cellsociety.Model.CellState.CellState;

public interface ReadOnlyGrid {

  int getRows();

  int getCols();

  CellState getCellState(int row, int col);
}
