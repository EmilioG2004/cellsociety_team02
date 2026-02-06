package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
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
    CellState[][] allDead = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(allDead);
    simulation.step();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, grid.getCellState(r, c), "All cells should remain dead");
      }
    }
  }

  // O - One: single element
  @Test
  void step_singleAliveCell_dies() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(oneLive);
    simulation.step();
    assertEquals(DEAD, grid.getCellState(1, 1), "Single cell should die (underpopulation)");
  }

  // M - Many: multiple elements
  @Test
  void step_blinkerPattern_oscillates() {
    CellState[][] verticalBlinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(verticalBlinker);
    simulation.step();
    // Should become horizontal
    assertEquals(DEAD, grid.getCellState(0, 0));
    assertEquals(ALIVE, grid.getCellState(1, 0));
    assertEquals(ALIVE, grid.getCellState(1, 1));
    assertEquals(ALIVE, grid.getCellState(1, 2));
    assertEquals(DEAD, grid.getCellState(2, 2));
  }

  @Test
  void step_block_remainsStable() {
    CellState[][] block = {
        {DEAD, DEAD, DEAD, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, DEAD, DEAD}
    };
    grid = new Grid(4, 4);
    grid.setCellsState(block);
    simulation = new Simulation(grid, rules);
    simulation.step();
    // Block should remain unchanged
    assertEquals(ALIVE, grid.getCellState(1, 1));
    assertEquals(ALIVE, grid.getCellState(1, 2));
    assertEquals(ALIVE, grid.getCellState(2, 1));
    assertEquals(ALIVE, grid.getCellState(2, 2));
  }

  // B - Boundaries: edge cases
  @Test
  void step_aliveCellAtCorner_properNeighborCount() {
    CellState[][] cornerCell = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(cornerCell);
    simulation.step();
    // Corner cell (0,0) has 2 alive neighbors, should survive
    assertEquals(ALIVE, grid.getCellState(0, 0));
  }

  @Test
  void step_aliveCellAtEdge_properNeighborCount() {
    CellState[][] edgeCell = {
        {DEAD, ALIVE, DEAD},
        {ALIVE, ALIVE, ALIVE},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(edgeCell);
    simulation.step();
    // Top middle cell (0,1) has 3 alive neighbors on edge
    assertEquals(ALIVE, grid.getCellState(0, 1));
  }

  // I - Interfaces: verify proper interface usage
  @Test
  void getGrid_returnsReadOnlyGrid() {
    CellState[][] initial = {{DEAD, ALIVE}, {ALIVE, DEAD}};
    grid = new Grid(2, 2);
    grid.setCellsState(initial);
    simulation = new Simulation(grid, rules);

    ReadOnlyGrid readOnly = simulation.getGrid();
    assertNotNull(readOnly);
    assertEquals(2, readOnly.getRows());
    assertEquals(2, readOnly.getCols());
    assertEquals(DEAD, readOnly.getCellState(0, 0));
    assertEquals(ALIVE, readOnly.getCellState(0, 1));
  }
}
