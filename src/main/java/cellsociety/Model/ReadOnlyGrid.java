package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;

/**
 * Read-only view of a grid of cell states. Allows consumers (e.g. the view) to read
 * dimensions and per-cell state without depending on the grid's internal representation
 * or receiving a full 2D array.
 */
public interface ReadOnlyGrid {

  int getRows();

  int getCols();

  CellState getCellState(int row, int col);

  /**
   * Copies this grid's state into a new 2D array. Use only when a caller truly needs
   * an array (e.g. for serialization); prefer getRows/getCols/getCellState for display.
   */
  static CellState[][] copyToArray(ReadOnlyGrid grid) {
    int rows = grid.getRows();
    int cols = grid.getCols();
    CellState[][] out = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        out[r][c] = grid.getCellState(r, c);
      }
    }
    return out;
  }

  public static int[][] produceIntArray(CellState[][] states, GameRule rule){
    int rows = states.length;
    int cols = states[0].length;
    int[][] out = new int[rows][cols];
    for (int r = 0; r < rows; r++){
      for (int c = 0; c < cols; c++){
        out[r][c] = rule.fromState(states[r][c]);
      }
    }

    return out;
  }
}
