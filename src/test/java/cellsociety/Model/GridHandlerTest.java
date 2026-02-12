package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for GridHandler
 */
class GridHandlerTest {

  private Grid grid;
  private GridHandler handler;

  @BeforeEach
  void setUp() {
    grid = new Grid(3, 3);
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {ALIVE, DEAD, ALIVE},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(initial);
    handler = new GridHandler(grid);
  }

  // Z - Zero: empty grid
  @Test
  void emptyGrid_hasDimensionsZero() {
    Grid emptyGrid = new Grid(0, 0);
    emptyGrid.setCellsState(new CellState[0][0]);
    GridHandler emptyHandler = new GridHandler(emptyGrid);
    assertEquals(0, emptyHandler.getRows());
    assertEquals(0, emptyHandler.getCols());
  }

  // O - One: single cell
  @Test
  void singleCellGrid_returnsCellState() {
    Grid singleGrid = new Grid(1, 1);
    singleGrid.setCellsState(new CellState[][]{{ALIVE}});
    GridHandler singleHandler = new GridHandler(singleGrid);
    assertEquals(1, singleHandler.getRows());
    assertEquals(1, singleHandler.getCols());
    assertEquals(ALIVE, singleHandler.getCellState(0, 0));
  }

  // M - Many: multiple cells
  @Test
  void getRows_returnsGridRows() {
    assertEquals(3, handler.getRows());
  }

  @Test
  void getCols_returnsGridCols() {
    assertEquals(3, handler.getCols());
  }

  @Test
  void getCellState_returnsCorrectStatesForAllCells() {
    assertEquals(DEAD, handler.getCellState(0, 0));
    assertEquals(ALIVE, handler.getCellState(0, 1));
    assertEquals(DEAD, handler.getCellState(0, 2));
    assertEquals(ALIVE, handler.getCellState(1, 0));
    assertEquals(DEAD, handler.getCellState(1, 1));
    assertEquals(ALIVE, handler.getCellState(1, 2));
    assertEquals(DEAD, handler.getCellState(2, 0));
    assertEquals(ALIVE, handler.getCellState(2, 1));
    assertEquals(DEAD, handler.getCellState(2, 2));
  }

  // I - Interface: handler delegates to grid
  @Test
  void handler_dimensionsMatchGrid() {
    assertEquals(grid.getRows(), handler.getRows());
    assertEquals(grid.getCols(), handler.getCols());
  }

  @Test
  void handler_cellStatesMatchGrid() {
    CellState[][] gridStates = grid.getCellStates();
    for (int r = 0; r < grid.getRows(); r++) {
      for (int c = 0; c < grid.getCols(); c++) {
        assertEquals(gridStates[r][c], handler.getCellState(r, c));
      }
    }
  }

  // E - Exceptional: large grid
  @Test
  void largeGrid_handlerReturnsCorrectStates() {
    int size = 20;
    Grid largeGrid = new Grid(size, size);
    CellState[][] states = new CellState[size][size];
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        states[r][c] = (r + c) % 2 == 0 ? ALIVE : DEAD;
      }
    }
    largeGrid.setCellsState(states);
    GridHandler largeHandler = new GridHandler(largeGrid);
    assertEquals(size, largeHandler.getRows());
    assertEquals(size, largeHandler.getCols());
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        assertEquals(states[r][c], largeHandler.getCellState(r, c));
      }
    }
  }

  // S - Simple: typical usage
  @Test
  void typicalUsage_createAndRead() {
    Grid g = new Grid(2, 2);
    g.setCellsState(new CellState[][]{{ALIVE, DEAD}, {DEAD, ALIVE}});
    GridHandler h = new GridHandler(g);
    assertEquals(ALIVE, h.getCellState(0, 0));
    assertEquals(DEAD, h.getCellState(0, 1));
    assertEquals(DEAD, h.getCellState(1, 0));
    assertEquals(ALIVE, h.getCellState(1, 1));
  }
}
