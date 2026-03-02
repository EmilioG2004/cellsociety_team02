package cellsociety.Model.Grid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

/**
 * Minimal grid data for the view: dimensions and per-cell state as an int.
 * The view should depend only on this interface (not {@link ReadOnlyGrid} or {@link CellState}).
 *
 * @author Michael Lei
 */
public interface ViewGrid {

  int getRows();

  int getCols();

  /** for the view to get the neighborstrategy attached to the grid */
  NeighborStrategy getNeighborStrategy();
  /** for the view to dynamically change the neighborstrategy used */
  void setNeighborStrategy(NeighborStrategy strategy);
  /**
   *
   * Integer code for the cell at (row, col), used for color lookup and serialization.
   */
  int getCellStateInt(int row, int col);

  /**
   * Produces an int matrix from this grid for config serialization (save).
   * Each element is the cell's state code at that position.
   */
  static int[][] produceIntArray(ViewGrid grid) {
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
