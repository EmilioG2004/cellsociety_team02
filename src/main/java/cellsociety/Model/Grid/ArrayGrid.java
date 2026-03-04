package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete grid backed by a 2D array internally, but it never exposes that array.
 * Implements Grid (which extends ReadOnlyGrid).
 *
 * @author Ixa Sigler
 */
public class ArrayGrid implements Grid {

  private final CellState[][] cells;
  private NeighborStrategy neighborStrategy;

  /**
   * Creates a grid from a 2D array of cell states. The array is copied defensively.
   * Must be non-empty and rectangular (all rows same length).
   */
  public ArrayGrid(CellState[][] states, NeighborStrategy strategy) {
    if (states == null || states.length == 0) {
      throw new IllegalArgumentException("states cannot be null or empty");
    }
    if (strategy == null) {
      throw new IllegalArgumentException("NeighborStrategy cannot be null");
    }
    int cols = states[0].length;
    if (cols == 0) {
      throw new IllegalArgumentException("states must have at least one column");
    }
    for (int r = 0; r < states.length; r++) {
      if (states[r] == null || states[r].length != cols) {
        throw new IllegalArgumentException("states must be rectangular (same column count per row)");
      }
    }
    this.cells = new CellState[states.length][cols];
    for (int r = 0; r < states.length; r++) {
      System.arraycopy(states[r], 0, this.cells[r], 0, cols);
    }
    this.neighborStrategy = strategy;
  }

  /** Used by emptyCopy() to create an empty grid of the same dimensions. */
  private ArrayGrid(int rows, int cols, NeighborStrategy strategy) {
    this.cells = new CellState[rows][cols];
    this.neighborStrategy = strategy;
  }
  @Override
  public void setNeighborStrategy(NeighborStrategy strategy) {
    if (strategy == null) {
      throw new IllegalArgumentException("NeighborStrategy cannot be null");
    }
    this.neighborStrategy = strategy;
  }

  @Override
  public int getRows() {
    return cells.length;
  }

  @Override
  public int getCols() {
    return cells.length == 0 ? 0 : cells[0].length;
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
  public List<CellState> getNeighbors(int row, int col) {
    List<CellState> neighbors = new ArrayList<>();
    for (int[] pos : neighborStrategy.getNeighborPositions(getRows(), getCols(), row, col)) {
      neighbors.add(cells[pos[0]][pos[1]]);
    }
    return neighbors;
  }

  @Override
  public List<CellState> getNeighbors(int row, int col, int vision) {
    List<CellState> neighbors = new ArrayList<>();
    for (int[] pos : neighborStrategy.getNeighborPositions(getRows(), getCols(), row, col, vision)) {
      neighbors.add(cells[pos[0]][pos[1]]);
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

  @Override
  public Grid emptyCopy() {
    return new ArrayGrid(getRows(), getCols(), neighborStrategy);
  }

  @Override
  public Grid deepCopy() {
    CellState[][] copy = new CellState[getRows()][getCols()];
    for (int r = 0; r < getRows(); r++) {
      System.arraycopy(cells[r], 0, copy[r], 0, getCols());
    }
    return new ArrayGrid(copy, neighborStrategy);
  }
}