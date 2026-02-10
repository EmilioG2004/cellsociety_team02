package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
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
    int[][] initial = {
      {0, 1, 0},
      {0, 1, 0},
      {0, 1, 0}
    };
    grid.setCellsState(initial);
    assertEquals(0, grid.getCellState(0, 0));
    assertEquals(1, grid.getCellState(0, 1));
    assertEquals(1, grid.getCellState(1, 1));
  }

  @Test
  void initializeGrid_wrongDimensions_throws() {
    int[][] wrongSize = {{0, 1}, {1, 0}};
    assertThrows(IllegalArgumentException.class, () -> grid.setCellsState(wrongSize));
  }

  @Test
  void getCellNeighbors_centerCell_returnsEightNeighbors() {
    int[][] initial = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    grid.setCellsState(initial);
    var neighbors = grid.getCellNeighbors(1, 1, rules);
    assertEquals(8, neighbors.size());
  }

  @Test
  void getCellNeighbors_cornerCell_returnsThreeNeighbors() {
    int[][] initial = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    grid.setCellsState(initial);
    var neighbors = grid.getCellNeighbors(0, 0, rules);
    assertEquals(3, neighbors.size());
  }

  @Test
  void updateAllCellsAndCommit_advancesGeneration() {
    // Blinker: vertical bar
    int[][] initial = {
      {0, 1, 0},
      {0, 1, 0},
      {0, 1, 0}
    };
    grid.setCellsState(initial);
    grid.updateAllCells(rules);
    grid.commitAllStates();
    // After one step, blinker becomes horizontal
    assertEquals(0, grid.getCellState(0, 0));
    assertEquals(1, grid.getCellState(1, 0));
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(1, 2));
  }

  @Test
  void gridImplementsReadOnlyGrid() {
    int[][] initial = {{0, 1}, {1, 0}};
    grid = new Grid(2, 2);
    grid.setCellsState(initial);
    ReadOnlyGrid readOnly = grid;
    assertEquals(2, readOnly.getRows());
    assertEquals(2, readOnly.getCols());
    assertEquals(0, readOnly.getCellState(0, 0));
    assertEquals(1, readOnly.getCellState(0, 1));
  }
}
