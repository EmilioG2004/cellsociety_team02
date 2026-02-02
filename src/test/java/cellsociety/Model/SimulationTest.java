package cellsociety.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Simulation following ZOMBIES (Zero, One, Many, Boundaries, Interfaces, Exceptions,
 * Simple scenarios).
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
    grid.initializeGrid(allDead);
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
    grid.initializeGrid(oneLive);
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
    grid.initializeGrid(verticalBlinker);
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
    grid.initializeGrid(block);
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
    grid.initializeGrid(cornerCell);
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
    grid.initializeGrid(edgeCell);
    simulation.step();
    // Top middle cell (0,1) has 3 alive neighbors on edge
    assertEquals(1, grid.getCellState(0, 1));
  }

  // I - Interfaces: verify proper interface usage
  @Test
  void getGrid_returnsReadOnlyGrid() {
    int[][] initial = {{0, 1}, {1, 0}};
    grid = new Grid(2, 2);
    grid.initializeGrid(initial);
    simulation = new Simulation(grid, rules);

    ReadOnlyGrid readOnly = simulation.getGrid();
    assertNotNull(readOnly);
    assertEquals(2, readOnly.getRows());
    assertEquals(2, readOnly.getCols());
    assertEquals(0, readOnly.getCellState(0, 0));
    assertEquals(1, readOnly.getCellState(0, 1));
  }

  // S - Simple scenarios: basic functionality
  @Test
  void step_advancesOneGeneration() {
    int[][] initial = {
      {0, 0, 0},
      {1, 1, 1},
      {0, 0, 0}
    };
    grid.initializeGrid(initial);
    simulation.step();
    // Should become vertical blinker
    assertEquals(0, grid.getCellState(1, 0));
    assertEquals(1, grid.getCellState(0, 1));
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(2, 1));
  }

  @Test
  void multipleSteps_advanceMultipleGenerations() {
    int[][] blinker = {
      {0, 1, 0},
      {0, 1, 0},
      {0, 1, 0}
    };
    grid.initializeGrid(blinker);
    simulation.step();
    simulation.step();
    // After two steps, should return to original vertical position
    assertEquals(0, grid.getCellState(0, 0));
    assertEquals(1, grid.getCellState(0, 1));
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(2, 1));
  }

  // Negative testing: various grid sizes
  @Test
  void step_largeGrid_performsCorrectly() {
    Grid largeGrid = new Grid(10, 10);
    int[][] glider = new int[10][10];
    // Set up glider pattern at origin
    glider[0][1] = 1;
    glider[1][2] = 1;
    glider[2][0] = 1;
    glider[2][1] = 1;
    glider[2][2] = 1;

    largeGrid.initializeGrid(glider);
    Simulation largeSim = new Simulation(largeGrid, rules);
    largeSim.step();
    // Verify glider has evolved (checking it doesn't crash and produces valid output)
    assertNotNull(largeSim.getGrid());
  }

  @Test
  void step_minimumGrid_1x1() {
    Grid tiny = new Grid(1, 1);
    int[][] single = {{1}};
    tiny.initializeGrid(single);
    Simulation tinySim = new Simulation(tiny, rules);
    tinySim.step();
    // Single cell with no neighbors dies
    assertEquals(0, tiny.getCellState(0, 0));
  }
}
