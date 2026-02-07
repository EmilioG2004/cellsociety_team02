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
  void step_allDeadGrid_remainsAllDead() {
    CellState[][] allDead = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(allDead);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, result[r][c], "All cells should remain dead");
      }
    }
  }

  @Test
  void step_allDeadGrid_multipleSteps_remainsAllDead() {
    CellState[][] allDead = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(allDead);
    simulation.step();
    simulation.step();
    simulation.step();
    CellState[][] result = grid.getCellStates();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, result[r][c]);
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
    assertEquals(DEAD, grid.getCellStates()[1][1], "Single cell should die (underpopulation)");
  }

  @Test
  void step_singleAliveCornerCell_dies() {
    CellState[][] cornerLive = {
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(cornerLive);
    simulation.step();
    assertEquals(DEAD, grid.getCellStates()[0][0], "Corner cell with no neighbors should die");
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
    CellState[][] result = grid.getCellStates();
    // Should become horizontal
    assertEquals(DEAD, result[0][0]);
    assertEquals(DEAD, result[0][1]);
    assertEquals(DEAD, result[0][2]);
    assertEquals(ALIVE, result[1][0]);
    assertEquals(ALIVE, result[1][1]);
    assertEquals(ALIVE, result[1][2]);
    assertEquals(DEAD, result[2][0]);
    assertEquals(DEAD, result[2][1]);
    assertEquals(DEAD, result[2][2]);
  }

  @Test
  void step_blinkerPattern_twoSteps_returnsToOriginal() {
    CellState[][] verticalBlinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(verticalBlinker);
    simulation.step();
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // Should return to vertical
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
  void step_blockPattern_remainsStable() {
    Grid blockGrid = new Grid(4, 4);
    CellState[][] block = {
        {DEAD, DEAD, DEAD, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, DEAD, DEAD}
    };
    blockGrid.setCellsState(block);
    Simulation blockSim = new Simulation(blockGrid, rules);
    blockSim.step();
    CellState[][] result = blockGrid.getCellStates();
    assertEquals(ALIVE, result[1][1]);
    assertEquals(ALIVE, result[1][2]);
    assertEquals(ALIVE, result[2][1]);
    assertEquals(ALIVE, result[2][2]);
    assertEquals(DEAD, result[0][0]);
    assertEquals(DEAD, result[3][3]);
  }

  @Test
  void step_allAliveGrid_cornersAndEdgesDie() {
    CellState[][] allAlive = {
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE}
    };
    grid.setCellsState(allAlive);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // Corners have 3 neighbors -> survive
    assertEquals(ALIVE, result[0][0]);
    assertEquals(ALIVE, result[0][2]);
    assertEquals(ALIVE, result[2][0]);
    assertEquals(ALIVE, result[2][2]);
    // Edges have 5 neighbors -> die (overpopulation)
    assertEquals(DEAD, result[0][1]);
    assertEquals(DEAD, result[1][0]);
    assertEquals(DEAD, result[1][2]);
    assertEquals(DEAD, result[2][1]);
    // Center has 8 neighbors -> dies (overpopulation)
    assertEquals(DEAD, result[1][1]);
  }

  // B - Boundary: corner and edge cell behavior
  @Test
  void step_aliveCellsAtCorner_survivesWithTwoNeighbors() {
    CellState[][] cornerCell = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(cornerCell);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // Corner (0,0) has 2 alive neighbors -> survives
    assertEquals(ALIVE, result[0][0]);
  }

  @Test
  void step_edgeCellWithThreeNeighbors_survives() {
    CellState[][] edgeCell = {
        {DEAD, ALIVE, DEAD},
        {ALIVE, ALIVE, ALIVE},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(edgeCell);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // (0,1) has 3 alive neighbors -> survives
    assertEquals(ALIVE, result[0][1]);
  }

  @Test
  void step_1x1Grid_aliveCellDies() {
    Grid tinyGrid = new Grid(1, 1);
    CellState[][] single = {{ALIVE}};
    tinyGrid.setCellsState(single);
    Simulation tinySim = new Simulation(tinyGrid, rules);
    tinySim.step();
    assertEquals(DEAD, tinyGrid.getCellStates()[0][0], "Alone cell should die");
  }

  // I - Interface: getGridHandler and getRule contracts
  @Test
  void getGridHandler_returnsNonNull() {
    CellState[][] initial = {{DEAD, ALIVE}, {ALIVE, DEAD}};
    Grid g = new Grid(2, 2);
    g.setCellsState(initial);
    Simulation sim = new Simulation(g, rules);
    GridHandler handler = sim.getGridHandler();
    assertNotNull(handler);
    assertEquals(2, handler.getRows());
    assertEquals(2, handler.getCols());
  }

  @Test
  void getRule_returnsOriginalRule() {
    assertEquals(rules, simulation.getRule());
  }

  @Test
  void getRule_isConwayGameOfLifeRule() {
    assertEquals(ConwayGameOfLifeRule.class, simulation.getRule().getClass());
  }

  // E - Exceptional: two cells with exactly 3 neighbors gives birth
  @Test
  void step_deadCellWithExactlyThreeNeighbors_becomesAlive() {
    CellState[][] lShape = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    grid.setCellsState(lShape);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // (1,1) is dead but has exactly 3 alive neighbors -> becomes alive
    assertEquals(ALIVE, result[1][1]);
  }

  // S - Simple: typical use case
  @Test
  void simpleFlow_createStepVerify() {
    CellState[][] initial = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(initial);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // Verify blinker rotated
    assertEquals(ALIVE, result[1][0]);
    assertEquals(ALIVE, result[1][1]);
    assertEquals(ALIVE, result[1][2]);
  }
}
