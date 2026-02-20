package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GridTest {

  private Grid grid;
  private ConwayGameOfLifeRule cellFactory;

  @BeforeEach
  void setUp() {
    grid = new Grid(3, 3);
    cellFactory = new ConwayGameOfLifeRule();
  }

  // Z - Zero: empty/minimal state
  @Test
  void getRows_getCols_returnsDimensions() {
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
    grid.setCellsState(allDead, cellFactory);
    CellState[][] result = grid.getCellStates();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, result[r][c]);
      }
    }
  }

  // O - One: single element
  @Test
  void singleCellGrid_storesAndRetrievesState() {
    Grid singleGrid = new Grid(1, 1);
    singleGrid.setCellsState(new CellState[][]{{ALIVE}}, cellFactory);
    assertEquals(ALIVE, singleGrid.getCellStates()[0][0]);
    singleGrid.setCellsState(new CellState[][]{{DEAD}}, cellFactory);
    assertEquals(DEAD, singleGrid.getCellStates()[0][0]);
  }

  // M - Many: multiple elements
  @Test
  void setCellsState_populatesCells() {
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(initial, cellFactory);
    CellState[][] result = grid.getCellStates();
    assertEquals(DEAD, result[0][0]);
    assertEquals(ALIVE, result[0][1]);
    assertEquals(DEAD, result[0][2]);
    assertEquals(DEAD, result[1][0]);
    assertEquals(ALIVE, result[1][1]);
    assertEquals(DEAD, result[1][2]);
    assertEquals(DEAD, result[2][0]);
    assertEquals(ALIVE, result[2][1]);
    assertEquals(DEAD, result[2][2]);
  }

  @Test
  void setCellsState_calledTwice_reflectsLatestState() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(initial, cellFactory);

    CellState[][] updated = {
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE}
    };
    grid.setCellsState(updated, cellFactory);

    CellState[][] result = grid.getCellStates();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(ALIVE, result[r][c]);
      }
    }
  }

  @Test
  void mixedStateGrid_preservesPattern() {
    CellState[][] checkerboard = {
        {ALIVE, DEAD, ALIVE},
        {DEAD, ALIVE, DEAD},
        {ALIVE, DEAD, ALIVE}
    };
    grid.setCellsState(checkerboard, cellFactory);
    CellState[][] result = grid.getCellStates();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(checkerboard[r][c], result[r][c]);
      }
    }
  }

  // B - Boundary: wrong dimensions, large grid
  @Test
  void setCellsState_wrongColumnCount_throws() {
    CellState[][] wrongCols = {{DEAD, ALIVE}, {ALIVE, DEAD}};
    assertThrows(IllegalArgumentException.class, () -> grid.setCellsState(wrongCols, cellFactory));
  }

  @Test
  void setCellsState_wrongRowCount_throws() {
    CellState[][] wrongRows = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    assertThrows(IllegalArgumentException.class, () -> grid.setCellsState(wrongRows, cellFactory));
  }

  @Test
  void setCellsState_tooManyRows_throws() {
    CellState[][] tooMany = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    assertThrows(IllegalArgumentException.class, () -> grid.setCellsState(tooMany, cellFactory));
  }

  @Test
  void largeGrid_storesAndRetrievesCorrectly() {
    int size = 50;
    Grid largeGrid = new Grid(size, size);
    CellState[][] states = new CellState[size][size];
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        states[r][c] = (r + c) % 2 == 0 ? ALIVE : DEAD;
      }
    }
    largeGrid.setCellsState(states, cellFactory);
    CellState[][] result = largeGrid.getCellStates();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        assertEquals(states[r][c], result[r][c]);
      }
    }
  }

  // I - Interface: getCellStates returns a copy
  @Test
  void getCellStates_returnsCopy_modifyingDoesNotAffectGrid() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(initial, cellFactory);
    CellState[][] copy = grid.getCellStates();
    copy[0][0] = ALIVE;
    assertEquals(DEAD, grid.getCellStates()[0][0],
        "Modifying returned array should not affect grid");
  }

  @Test
  void getCellStates_dimensionsMatchGrid() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(initial, cellFactory);
    CellState[][] result = grid.getCellStates();
    assertEquals(3, result.length);
    assertEquals(3, result[0].length);
  }

  // E - Exceptional: empty grid, uninitialized access
  @Test
  void zeroSizeGrid_hasZeroDimensionsAndEmptyGetCellStates() {
    Grid emptyGrid = new Grid(0, 0);
    assertEquals(0, emptyGrid.getRows());
    assertEquals(0, emptyGrid.getCols());
    emptyGrid.setCellsState(new CellState[0][0], cellFactory);
    CellState[][] result = emptyGrid.getCellStates();
    assertEquals(0, result.length);
  }

  @Test
  void getCellState_beforeSetCellsState_throws() {
    Grid uninit = new Grid(2, 2);
    assertThrows(IllegalStateException.class, () -> uninit.getCellState(0, 0));
  }

  @Test
  void getCellStates_beforeSetCellsState_throws() {
    Grid uninit = new Grid(2, 2);
    assertThrows(IllegalStateException.class, () -> uninit.getCellStates());
  }

  @Test
  void getCellState_outOfBounds_throws() {
    grid.setCellsState(new CellState[][]{{DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}}, cellFactory);
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(0, -1));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(3, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> grid.getCellState(0, 3));
  }
}
