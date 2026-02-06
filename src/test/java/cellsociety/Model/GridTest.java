package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GridTest {

  private Grid grid;
  private ConwayGameOfLifeRule rules;

  @BeforeEach
  void setUp() {
    grid = new Grid(3, 3);
    rules = new ConwayGameOfLifeRule();
  }

  @Test
  void getRows_getCols_returnsDimensions() {
    assertEquals(3, grid.getRows());
    assertEquals(3, grid.getCols());
  }

  @Test
  void initializeGrid_populatesCells() {
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(initial);
    assertEquals(DEAD, grid.getCellState(0, 0));
    assertEquals(ALIVE, grid.getCellState(0, 1));
    assertEquals(ALIVE, grid.getCellState(1, 1));
  }

  @Test
  void initializeGrid_wrongDimensions_throws() {
    CellState[][] wrongSize = {{DEAD, ALIVE}, {ALIVE, DEAD}};
    assertThrows(IllegalArgumentException.class, () -> grid.setCellsState(wrongSize));
  }

  @Test
  void getCellNeighbors_centerCell_returnsEightNeighbors() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(initial);
    var neighbors = grid.getCellNeighbors(1, 1, rules);
    assertEquals(8, neighbors.size());
  }

  @Test
  void getCellNeighbors_cornerCell_returnsThreeNeighbors() {
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(initial);
    var neighbors = grid.getCellNeighbors(0, 0, rules);
    assertEquals(3, neighbors.size());
  }

  @Test
  void updateAllCellsAndCommit_advancesGeneration() {
    // Blinker: vertical bar
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(initial);
    grid.updateAllCells(rules);
    grid.commitAllStates();
    // After one step, blinker becomes horizontal
    assertEquals(DEAD, grid.getCellState(0, 0));
    assertEquals(ALIVE, grid.getCellState(1, 0));
    assertEquals(ALIVE, grid.getCellState(1, 1));
    assertEquals(ALIVE, grid.getCellState(1, 2));
  }

  @Test
  void gridImplementsReadOnlyGrid() {
    CellState[][] initial = {{DEAD, ALIVE}, {ALIVE, DEAD}};
    grid = new Grid(2, 2);
    grid.setCellsState(initial);
    ReadOnlyGrid readOnly = grid;
    assertEquals(2, readOnly.getRows());
    assertEquals(2, readOnly.getCols());
    assertEquals(DEAD, readOnly.getCellState(0, 0));
    assertEquals(ALIVE, readOnly.getCellState(0, 1));
  }
}
