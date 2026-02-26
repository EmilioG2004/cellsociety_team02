package cellsociety.Model.Grid;

import cellsociety.Model.CellState.CellState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
/**
 * Stores a rectangular arrangement of cell states.
 */
public class Grid implements MutableGrid {

  private static final int FIRST_ROW_INDEX = 0;
  private static final int FIRST_COL_INDEX = 0;
  private static final String ERROR_GRID_NOT_INITIALIZED = "Grid not initialized; call setCellsState first";
  private static final String ERROR_CELL_NOT_INITIALIZED = "Cell at (%d, %d) not initialized";
  private static final String ERROR_ROW_OUT_OF_BOUNDS = "Row %d out of bounds [0, %d)";
  private static final String ERROR_COL_OUT_OF_BOUNDS = "Column %d out of bounds [0, %d)";

  private final CellState[][] cells;
  private final int rows;
  private final int cols;

  /**
   * Constructs a grid with the given dimensions. Call {@link #setCellsState(CellState[][])} to populate.
   */
  public Grid(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.cells = new CellState[rows][cols];
  }

  @Override
  public void setCellsState(CellState[][] state) {
    if (state.length != rows || (rows > FIRST_ROW_INDEX && state[FIRST_ROW_INDEX].length != cols)) {
      throw new IllegalArgumentException(
          "Initial state dimensions " + state.length + "x"
              + (state.length > FIRST_ROW_INDEX ? state[FIRST_ROW_INDEX].length : FIRST_COL_INDEX)
              + " do not match grid " + rows + "x" + cols);
    }
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c] = state[r][c];
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
   * Returns the state of the cell at the given row and column.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the state of the cell
   * @throws IndexOutOfBoundsException if row or col is out of bounds
   * @throws IllegalStateException if the grid has not been initialized at (row, col)
   */
  public CellState getCellState(int row, int col) {
    checkBounds(row, col);
    CellState state = cells[row][col];
    if (state == null) {
      throw new IllegalStateException(String.format(ERROR_CELL_NOT_INITIALIZED, row, col));
    }
    return state;
  }

  private void checkBounds(int row, int col) {
    if (row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException(String.format(ERROR_ROW_OUT_OF_BOUNDS, row, rows));
    }
    if (col < 0 || col >= cols) {
      throw new IndexOutOfBoundsException(String.format(ERROR_COL_OUT_OF_BOUNDS, col, cols));
    }
  }

  @Override
  public void setCellState(int row, int col, CellState state) {
    checkBounds(row, col);
    cells[row][col] = state;
  }

  @Override
  public CellState[][] getCellStates() {
    CellState[][] states = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c] == null) {
          throw new IllegalStateException(ERROR_GRID_NOT_INITIALIZED);
        }
        states[r][c] = cells[r][c];
      }
    }
    return states;
  }
}
