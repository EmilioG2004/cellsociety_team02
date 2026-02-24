package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.ReadOnlyGrid;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that Grid implements ReadOnlyGrid and that ReadOnlyGrid.copyToArray works.
 * View and other consumers depend only on this read-only contract (rows, cols, getCellState).
 */
class ReadOnlyGridTest {

  private Grid grid;
  private ReadOnlyGrid readOnlyGrid;
  private ConwayGameOfLifeRule cellFactory;

  @BeforeEach
  void setUp() {
    grid = new Grid(3, 3);
    cellFactory = new ConwayGameOfLifeRule();
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {ALIVE, DEAD, ALIVE},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(initial, cellFactory);
    readOnlyGrid = grid;
  }

  // Z - Zero: empty grid
  @Test
  void emptyGrid_hasDimensionsZero() {
    Grid emptyGrid = new Grid(0, 0);
    emptyGrid.setCellsState(new CellState[0][0], cellFactory);
    ReadOnlyGrid ro = emptyGrid;
    assertEquals(0, ro.getRows());
    assertEquals(0, ro.getCols());
  }

  // O - One: single cell
  @Test
  void singleCellGrid_returnsCellState() {
    Grid singleGrid = new Grid(1, 1);
    singleGrid.setCellsState(new CellState[][]{{ALIVE}}, cellFactory);
    ReadOnlyGrid ro = singleGrid;
    assertEquals(1, ro.getRows());
    assertEquals(1, ro.getCols());
    assertEquals(ALIVE, ro.getCellState(0, 0));
  }

  // M - Many: multiple cells
  @Test
  void getRows_returnsGridRows() {
    assertEquals(3, readOnlyGrid.getRows());
  }

  @Test
  void getCols_returnsGridCols() {
    assertEquals(3, readOnlyGrid.getCols());
  }

  @Test
  void getCellState_returnsCorrectStatesForAllCells() {
    assertEquals(DEAD, readOnlyGrid.getCellState(0, 0));
    assertEquals(ALIVE, readOnlyGrid.getCellState(0, 1));
    assertEquals(DEAD, readOnlyGrid.getCellState(0, 2));
    assertEquals(ALIVE, readOnlyGrid.getCellState(1, 0));
    assertEquals(DEAD, readOnlyGrid.getCellState(1, 1));
    assertEquals(ALIVE, readOnlyGrid.getCellState(1, 2));
    assertEquals(DEAD, readOnlyGrid.getCellState(2, 0));
    assertEquals(ALIVE, readOnlyGrid.getCellState(2, 1));
    assertEquals(DEAD, readOnlyGrid.getCellState(2, 2));
  }

  // I - Interface: ReadOnlyGrid matches Grid
  @Test
  void readOnlyGrid_dimensionsMatchGrid() {
    assertEquals(grid.getRows(), readOnlyGrid.getRows());
    assertEquals(grid.getCols(), readOnlyGrid.getCols());
  }

  @Test
  void readOnlyGrid_cellStatesMatchGrid() {
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        assertEquals(grid.getCellState(r, c), readOnlyGrid.getCellState(r, c));
      }
    }
  }

  @Test
  void copyToArray_returnsMatchingSnapshot() {
    CellState[][] copy = ReadOnlyGrid.copyToArray(readOnlyGrid);
    assertEquals(3, copy.length);
    assertEquals(3, copy[0].length);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(grid.getCellState(r, c), copy[r][c]);
      }
    }
  }

  // E - Exceptional: large grid
  @Test
  void largeGrid_returnsCorrectStates() {
    int size = 20;
    Grid largeGrid = new Grid(size, size);
    CellState[][] states = new CellState[size][size];
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        states[r][c] = (r + c) % 2 == 0 ? ALIVE : DEAD;
      }
    }
    largeGrid.setCellsState(states, cellFactory);
    ReadOnlyGrid ro = largeGrid;
    assertEquals(size, ro.getRows());
    assertEquals(size, ro.getCols());
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        assertEquals(states[r][c], ro.getCellState(r, c));
      }
    }
  }

  // S - Simple: typical usage
  @Test
  void typicalUsage_createAndRead() {
    Grid g = new Grid(2, 2);
    g.setCellsState(new CellState[][]{{ALIVE, DEAD}, {DEAD, ALIVE}}, cellFactory);
    ReadOnlyGrid ro = g;
    assertEquals(ALIVE, ro.getCellState(0, 0));
    assertEquals(DEAD, ro.getCellState(0, 1));
    assertEquals(DEAD, ro.getCellState(1, 0));
    assertEquals(ALIVE, ro.getCellState(1, 1));
  }
}
