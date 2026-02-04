package cellsociety.Model;

import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Simulation 
 */
class SimulationTest {
  private Grid grid;
  private GameRule rules;
  private Simulation simulation;

  @BeforeEach
  void setUp() {
    grid = new Grid(3, 3);
    rules = new ConwayGameOfLifeRule();
    simulation = new Simulation(grid, rules);
  }

  // Z - Zero: empty/minimal state
  @Test
  void step_emptyGrid_remainsEmpty() {
    int[][] allDead = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    grid.setCellsState(allDead);
    simulation.step();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(0, grid.getCellState(r, c), "All cells should remain dead");
      }
    }
  }

  // O - One: single element
  @Test
  void step_singleAliveCell_dies() {
    int[][] oneLive = {
      {0, 0, 0},
      {0, 1, 0},
      {0, 0, 0}
    };
    grid.setCellsState(oneLive);
    simulation.step();
    assertEquals(0, grid.getCellState(1, 1), "Single cell should die (underpopulation)");
  }

  // M - Many: multiple elements
  @Test
  void step_blinkerPattern_oscillates() {
    int[][] verticalBlinker = {
      {0, 1, 0},
      {0, 1, 0},
      {0, 1, 0}
    };
    grid.setCellsState(verticalBlinker);
    simulation.step();
    // Should become horizontal
    assertEquals(0, grid.getCellState(0, 0));
    assertEquals(1, grid.getCellState(1, 0));
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(1, 2));
    assertEquals(0, grid.getCellState(2, 2));
  }

  @Test
  void step_block_remainsStable() {
    int[][] block = {
      {0, 0, 0, 0},
      {0, 1, 1, 0},
      {0, 1, 1, 0},
      {0, 0, 0, 0}
    };
    grid = new Grid(4, 4);
    grid.setCellsState(block);
    simulation = new Simulation(grid, rules);
    simulation.step();
    // Block should remain unchanged
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(1, 2));
    assertEquals(1, grid.getCellState(2, 1));
    assertEquals(1, grid.getCellState(2, 2));
  }

  // B - Boundaries: edge cases
  @Test
  void step_aliveCellAtCorner_properNeighborCount() {
    int[][] cornerCell = {
      {1, 1, 0},
      {1, 0, 0},
      {0, 0, 0}
    };
    grid.setCellsState(cornerCell);
    simulation.step();
    // Corner cell (0,0) has 2 alive neighbors, should survive
    assertEquals(1, grid.getCellState(0, 0));
  }

  @Test
  void step_aliveCellAtEdge_properNeighborCount() {
    int[][] edgeCell = {
      {0, 1, 0},
      {1, 1, 1},
      {0, 0, 0}
    };
    grid.setCellsState(edgeCell);
    simulation.step();
    // Top middle cell (0,1) has 3 alive neighbors on edge
    assertEquals(1, grid.getCellState(0, 1));
  }

  // I - Interfaces: verify proper interface usage
  @Test
  void getGrid_returnsReadOnlyGrid() {
    int[][] initial = {{0, 1}, {1, 0}};
    grid = new Grid(2, 2);
    grid.setCellsState(initial);
    simulation = new Simulation(grid, rules);

    ReadOnlyGrid readOnly = simulation.getGrid();
    assertNotNull(readOnly);
    assertEquals(2, readOnly.getRows());
    assertEquals(2, readOnly.getCols());
    assertEquals(0, readOnly.getCellState(0, 0));
    assertEquals(1, readOnly.getCellState(0, 1));
  }
}
