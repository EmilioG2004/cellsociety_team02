package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import java.util.Collection;
public interface ReadOnlyGrid {

  int getRows();

  int getCols();

  CellState getCellState(int row, int col);

  CellState[][] getCellStates();
}
