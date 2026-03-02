package cellsociety.Model.Grid;

/**
 * Minimal grid data for the view: dimensions and per-cell state as an int.
 * The view should depend only on this interface (not {@link ReadOnlyGrid} or {@link CellState}).
 *
 * @author Michael Lei
 */
public interface GridViewData {

  int getRows();

  int getCols();

  /**
   * Integer code for the cell at (row, col), used for color lookup and serialization.
   */
  int getCellStateInt(int row, int col);

  /**
   * Produces an int matrix from this grid for config serialization (save).
   * Each element is the cell's state code at that position.
   */
  static int[][] produceIntArray(GridViewData grid) {
    int rows = grid.getRows();
    int cols = grid.getCols();
    int[][] out = new int[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        out[r][c] = grid.getCellStateInt(r, c);
      }
    }
    return out;
  }
}
