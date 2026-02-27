package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete grid backed by a 2D array internally, but it never exposes that array.
 * Implements BOTH interfaces:
 *  - ReadOnlyGrid: what rules can read
 *  - WritableGrid: what simulation uses to write next grid
 *
 * @author Ixa Sigler
 */
public class ArrayGrid implements ReadOnlyGrid, MutableGrid {

  private final CellState[][] cells;
  private final NeighborStrategy neighborStrategy;

  /**
   * Create an empty grid of given dimensions. Caller populates using setCellState().
   */
  public ArrayGrid(int rows, int cols, NeighborStrategy strategy) {
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("rows and cols must be positive");
    }
    if (strategy == null) {
      throw new IllegalArgumentException("NeighborStrategy cannot be null");
    }
    this.cells = new CellState[rows][cols];
    this.neighborStrategy = strategy;
  }

  // -------------------- ReadOnlyGrid --------------------

  @Override
  public int getRows() {
    return cells.length;
  }

  @Override
  public int getCols() {
    return cells[0].length;
  }

  @Override
  public CellState getCellState(int row, int col) {
    if (!isInBounds(row, col)) {
      throw new IndexOutOfBoundsException("Out of bounds: (" + row + ", " + col + ")");
    }
    return cells[row][col];
  }

  @Override
  public boolean isInBounds(int row, int col) {
    return row >= 0 && row < getRows() && col >= 0 && col < getCols();
  }

  @Override
  public NeighborStrategy getNeighborStrategy() {
    return neighborStrategy;
  }

  @Override
  public CellState getRelativeNeighbor(int row, int col, int rowOffset, int colOffset) {
    int r = row + rowOffset;
    int c = col + colOffset;
    if (!isInBounds(r, c)) return null;
    return cells[r][c];
  }

  @Override
  public List<CellState> getNeighbors(int row, int col) {
    List<CellState> neighbors = new ArrayList<>();
    for (int[] offset : neighborStrategy.getNeighborOffsets()) {
      CellState n = getRelativeNeighbor(row, col, offset[0], offset[1]);
      if (n != null) neighbors.add(n);
    }
    return neighbors;
  }

  @Override
  public void setCellState(int row, int col, CellState state) {
    if (!isInBounds(row, col)) {
      throw new IndexOutOfBoundsException("Out of bounds: (" + row + ", " + col + ")");
    }
    cells[row][col] = state;
  }
}