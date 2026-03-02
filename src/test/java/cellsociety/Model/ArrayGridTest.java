package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrayGridTest {

  private MooreNeighborStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new MooreNeighborStrategy();
  }

  // Z - Zero: empty/minimal state
  @Test
  void getRows_getCols_returnsDimensions() {
    CellState[][] states = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(states, strategy);
    assertEquals(3, grid.getRows());
    assertEquals(3, grid.getCols());
  }

  @Test
  void allDeadGrid_returnsAllDead() {
    CellState[][] allDead = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(allDead, strategy);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, grid.getCellState(r, c));
      }
    }
  }

  // O - One: single element
  @Test
  void singleCellGrid_storesAndRetrievesState() {
    ArrayGrid singleGrid = new ArrayGrid(new CellState[][]{{ALIVE}}, strategy);
    assertEquals(ALIVE, singleGrid.getCellState(0, 0));
  }

  // M - Many: multiple elements
  @Test
  void constructor_populatesCells() {
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(initial, strategy);
    assertEquals(DEAD, grid.getCellState(0, 0));
    assertEquals(ALIVE, grid.getCellState(0, 1));
    assertEquals(DEAD, grid.getCellState(0, 2));
    assertEquals(DEAD, grid.getCellState(1, 0));
    assertEquals(ALIVE, grid.getCellState(1, 1));
    assertEquals(DEAD, grid.getCellState(1, 2));
    assertEquals(DEAD, grid.getCellState(2, 0));
    assertEquals(ALIVE, grid.getCellState(2, 1));
    assertEquals(DEAD, grid.getCellState(2, 2));
  }

  @Test
  void setCellState_updatesAndReflects() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(initial, strategy);
    grid.setCellState(0, 0, ALIVE);
    grid.setCellState(1, 1, ALIVE);
    assertEquals(ALIVE, grid.getCellState(0, 0));
    assertEquals(ALIVE, grid.getCellState(1, 1));
    assertEquals(DEAD, grid.getCellState(0, 1));
  }

  @Test
  void mixedStateGrid_preservesPattern() {
    CellState[][] checkerboard = {
        {ALIVE, DEAD, ALIVE},
        {DEAD, ALIVE, DEAD},
        {ALIVE, DEAD, ALIVE}
    };
    ArrayGrid grid = new ArrayGrid(checkerboard, strategy);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(checkerboard[r][c], grid.getCellState(r, c));
      }
    }
  }

  // B - Boundary: wrong dimensions, large grid
  @Test
  void constructor_wrongColumnCount_throws() {
    CellState[][] wrongCols = {{DEAD, ALIVE}, {ALIVE}}; // jagged
    assertThrows(IllegalArgumentException.class, () -> new ArrayGrid(wrongCols, strategy));
  }

  @Test
  void constructor_nullStates_throws() {
    assertThrows(IllegalArgumentException.class, () -> new ArrayGrid(null, strategy));
  }

  @Test
  void constructor_emptyRows_throws() {
    assertThrows(IllegalArgumentException.class, () -> new ArrayGrid(new CellState[0][0], strategy));
  }

  @Test
  void constructor_nullStrategy_throws() {
    CellState[][] states = {{DEAD}};
    assertThrows(IllegalArgumentException.class, () -> new ArrayGrid(states, null));
  }

  @Test
  void largeGrid_storesAndRetrievesCorrectly() {
    int size = 50;
    CellState[][] states = new CellState[size][size];
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        states[r][c] = (r + c) % 2 == 0 ? ALIVE : DEAD;
      }
    }
    ArrayGrid largeGrid = new ArrayGrid(states, strategy);
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        assertEquals(states[r][c], largeGrid.getCellState(r, c));
      }
    }
  }

  // I - Interface: copyToArray returns a copy
  @Test
  void copyToArray_returnsCopy_modifyingDoesNotAffectGrid() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(initial, strategy);
    CellState[][] copy = ReadOnlyGrid.copyToArray(grid);
    copy[0][0] = ALIVE;
    assertEquals(DEAD, grid.getCellState(0, 0),
        "Modifying copy should not affect grid");
  }

  @Test
  void copyToArray_dimensionsMatchGrid() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(initial, strategy);
    CellState[][] result = ReadOnlyGrid.copyToArray(grid);
    assertEquals(3, result.length);
    assertEquals(3, result[0].length);
  }

  // E - Exceptional: out of bounds
  @Test
  void getCellState_outOfBounds_throws() {
    CellState[][] states = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    ArrayGrid grid = new ArrayGrid(states, strategy);
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(0, -1));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(3, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(0, 3));
  }

  @Test
  void setCellState_outOfBounds_throws() {
    CellState[][] states = {{DEAD, DEAD}, {DEAD, DEAD}};
    ArrayGrid grid = new ArrayGrid(states, strategy);
    assertThrows(IndexOutOfBoundsException.class, () -> grid.setCellState(-1, 0, ALIVE));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.setCellState(0, 2, ALIVE));
  }
}
