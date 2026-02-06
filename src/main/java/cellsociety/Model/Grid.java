package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Utils.NeighborStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a rectangular arrangement of cells, applies neighbor logic using the strategy from
 * GameRule, and drives the two-pass update (compute next state for all, then commit).
 * Does not store a NeighborStrategy; obtains it from GameRule during updates.
 */
public class Grid implements ReadOnlyGrid {

  private final Cell[][] cells;
  private final int rows;
  private final int cols;

  /**
   * Constructs a grid with the given dimensions. Call {@link #setCellsState(CellState[][])} to populate.
   */
  public Grid(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.cells = new Cell[rows][cols];
  }

  /**
   * Initializes the grid with cells from the given state array. Dimensions must match rows and cols.
   *
   * @param state 2D array of CellState values, row-major order
   * @throws IllegalArgumentException if dimensions don't match
   */
  public void setCellsState(CellState[][] state) {
    if (state.length != rows || (rows > 0 && state[0].length != cols)) {
      throw new IllegalArgumentException(
          "Initial state dimensions " + state.length + "x"
              + (state.length > 0 ? state[0].length : 0)
              + " do not match grid " + rows + "x" + cols);
    }
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c] != null) {
          cells[r][c].setState(state[r][c]);
        } else {
          cells[r][c] = new Cell(state[r][c]);
        }
      }
    }
  }

  /**
   * Returns the number of rows.
   */
  public int getRows() {
    return rows;
  }

  /**
   * Returns the number of columns.
   */
  public int getCols() {
    return cols;
  }

  /**
   * Returns the state of the cell at (row, col). For read-only access by the view.
   *
   * @throws IndexOutOfBoundsException if coordinates are out of bounds
   */
  public CellState getCellState(int row, int col) {
    return cells[row][col].getState();
  }

  /**
   * Returns the position of the cell.
   */
  public int[] getCellPosition(ReadOnlyCell cell) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c].equals(cell)) {
          return new int[]{r, c};
        }
      }
    }
    throw new IllegalArgumentException("Cell not found in grid");
  }

  /**
   * Returns the list of neighbor cells for the cell at (row, col), using the neighbor strategy
   * from the given rules. Out-of-bounds positions are excluded (finite boundary).
   */
  public List<ReadOnlyCell> getCellNeighbors(int row, int col, GameRule rules) {
    NeighborStrategy strategy = rules.getNeighborStrategy();
    List<int[]> offsets = strategy.getNeighborOffsets();
    List<ReadOnlyCell> neighbors = new ArrayList<>();

    for (int[] offset : offsets) {
      int nr = row + offset[0];
      int nc = col + offset[1];
      if (isInBounds(nr, nc)) {
        neighbors.add(cells[nr][nc]);
      }
    }
    return neighbors;
  }

  /**
   * Computes the next state for every cell using the given rules. Does not commit—call
   * {@link #commitAllStates()} after to advance the generation.
   */
  public void updateAllCells(GameRule rules) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        List<ReadOnlyCell> neighbors = getCellNeighbors(r, c, rules);
        cells[r][c].updateNextState(rules, neighbors);
      }
    }
  }

  /**
   * Commits all cells' next state to current state. Call after {@link #updateAllCells(GameRule)}.
   */
  public void commitAllStates() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c].updateState();
      }
    }
  }

  private boolean isInBounds(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
  }
}
