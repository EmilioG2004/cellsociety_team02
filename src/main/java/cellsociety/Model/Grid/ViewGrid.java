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

  /** Returns the neighbor strategy attached to the grid (e.g. Moore, Von Neumann). */
  NeighborStrategy getNeighborStrategy();

  /** Sets the neighbor strategy so the view can change neighborhood (e.g. Moore vs Von Neumann) dynamically. */
  void setNeighborStrategy(NeighborStrategy strategy);

  /**
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
