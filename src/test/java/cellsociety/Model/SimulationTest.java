package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.ReadOnlyGrid;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    CellState[][] initial = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    rules = new ConwayGameOfLifeRule();
    grid.setCellsState(initial, rules);
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
    simulation.setState(allDead);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    assertAll(
        () -> assertEquals(DEAD, result[0][0]),
        () -> assertEquals(DEAD, result[0][1]),
        () -> assertEquals(DEAD, result[0][2]),
        () -> assertEquals(DEAD, result[1][0]),
        () -> assertEquals(DEAD, result[1][1]),
        () -> assertEquals(DEAD, result[1][2]),
        () -> assertEquals(DEAD, result[2][0]),
        () -> assertEquals(DEAD, result[2][1]),
        () -> assertEquals(DEAD, result[2][2])
    );
  }

  // O - One: single element
  @Test
  void step_singleAliveCell_dies() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    simulation.setState(oneLive);
    simulation.step();
    assertEquals(DEAD, grid.getCellStates()[1][1], "Single cell should die (underpopulation)");
  }
  @Test
  void step_singleAliveCell_dies_thenBack_returnsToOriginal() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };

    simulation.setState(oneLive);
    simulation.step();
    assertEquals(DEAD, grid.getCellStates()[1][1], "Single cell should die (underpopulation)");
    simulation.stepBack();

    assertEquals(ALIVE, grid.getCellStates()[1][1], "Single cell should return to alive");
  }

  @Test
  void stepBack_fromInitialState_throws() {
    IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> simulation.stepBack());
    assertTrue(thrown.getMessage().contains("Cannot step back from initial state"));
  }

  @Test
  void stepBack_multipleSteps_restoresToInitial() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    simulation.setState(oneLive);
    simulation.step();
    simulation.step();
    simulation.step();
    simulation.stepBack();
    simulation.stepBack();
    simulation.stepBack();
    CellState[][] result = grid.getCellStates();
    assertEquals(ALIVE, result[1][1], "After 3 stepBack should restore single live cell");
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        if (r != 1 || c != 1) {
          assertEquals(DEAD, result[r][c]);
        }
      }
    }
  }

  @Test
  void stepBack_twice_afterBlinkerTwoSteps_restoresVerticalBlinker() {
    CellState[][] verticalBlinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    simulation.setState(verticalBlinker);
    simulation.step(); // -> horizontal
    simulation.step(); // -> vertical again
    simulation.stepBack();
    simulation.stepBack();
    CellState[][] result = grid.getCellStates();
    assertAll(
        () -> assertEquals(ALIVE, result[0][1]),
        () -> assertEquals(ALIVE, result[1][1]),
        () -> assertEquals(ALIVE, result[2][1]),
        () -> assertEquals(DEAD, result[1][0]),
        () -> assertEquals(DEAD, result[1][2])
    );
  }

  // setState and undo history
  @Test
  void setState_clearsUndoHistory_stepBackOnceRestoresToNewState() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    simulation.setState(oneLive);
    simulation.step(); // cell dies
    simulation.stepBack();
    assertEquals(ALIVE, grid.getCellStates()[1][1], "stepBack after setState should restore to setState snapshot");
  }

  @Test
  void setState_afterSteps_replacesHistory_stepBackFromStateOnly() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    simulation.setState(oneLive);
    simulation.step();
    simulation.step();
    CellState[][] other = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, ALIVE}
    };
    simulation.setState(other);
    simulation.step(); // corner cell dies
    simulation.stepBack();
    assertEquals(ALIVE, grid.getCellStates()[2][2], "stepBack should restore to last setState (other), not earlier");
  }

  @Test
  void setState_thenStepBackFromInitial_throws() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    simulation.setState(oneLive);
    assertThrows(IllegalStateException.class, () -> simulation.stepBack(),
        "Cannot step back when only one state on stack (current)");
  }

  // M - Many: multiple elements
  @Test
  void step_blinkerPattern_oscillates() {
    CellState[][] verticalBlinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    simulation.setState(verticalBlinker);
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
    simulation.setState(verticalBlinker);
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
    blockGrid.setCellsState(block, rules);
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
    simulation.setState(allAlive);
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
    simulation.setState(cornerCell);
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
    simulation.setState(edgeCell);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // (0,1) has 3 alive neighbors -> survives
    assertEquals(ALIVE, result[0][1]);
  }

  @Test
  void step_1x1Grid_aliveCellDies() {
    Grid tinyGrid = new Grid(1, 1);
    CellState[][] single = {{ALIVE}};
    tinyGrid.setCellsState(single, rules);
    Simulation tinySim = new Simulation(tinyGrid, rules);
    tinySim.step();
    assertEquals(DEAD, tinyGrid.getCellStates()[0][0], "Alone cell should die");
  }

  @Test
  void step_1x2Grid_bothAlive_bothDie() {
    Grid thinGrid = new Grid(1, 2);
    CellState[][] twoAlive = {{ALIVE, ALIVE}};
    thinGrid.setCellsState(twoAlive, rules);
    Simulation thinSim = new Simulation(thinGrid, rules);
    thinSim.step();
    CellState[][] result = thinGrid.getCellStates();
    assertEquals(DEAD, result[0][0], "Each has 1 neighbor -> underpopulation");
    assertEquals(DEAD, result[0][1]);
  }

  @Test
  void step_2x2Grid_blockStaysStable() {
    Grid smallGrid = new Grid(2, 2);
    CellState[][] block = {{ALIVE, ALIVE}, {ALIVE, ALIVE}};
    smallGrid.setCellsState(block, rules);
    Simulation smallSim = new Simulation(smallGrid, rules);
    smallSim.step();
    CellState[][] result = smallGrid.getCellStates();
    assertAll(
        () -> assertEquals(ALIVE, result[0][0]),
        () -> assertEquals(ALIVE, result[0][1]),
        () -> assertEquals(ALIVE, result[1][0]),
        () -> assertEquals(ALIVE, result[1][1])
    );
  }

  @Test
  void step_thenStepBack_thenStep_consistentWithTwoSteps() {
    CellState[][] oneLive = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    simulation.setState(oneLive);
    simulation.step();       // cell dies
    simulation.stepBack();    // back to one live
    simulation.step();        // cell dies again
    assertEquals(DEAD, grid.getCellStates()[1][1], "Step after stepBack should match normal step from same state");
  }

  // Toggle cell state (click-to-cycle)
  @Test
  void toggleCellState_dead_becomesAlive() {
    simulation.setState(new CellState[][]{
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    });
    simulation.toggleCellState(1, 1);
    assertEquals(ALIVE, grid.getCellState(1, 1));
  }

  @Test
  void toggleCellState_alive_becomesDead() {
    simulation.setState(new CellState[][]{
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    });
    simulation.toggleCellState(1, 1);
    assertEquals(DEAD, grid.getCellState(1, 1));
  }

  @Test
  void toggleCellState_twice_returnsToOriginal() {
    simulation.setState(new CellState[][]{
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    });
    simulation.toggleCellState(1, 1);
    simulation.toggleCellState(1, 1);
    assertEquals(ALIVE, grid.getCellState(1, 1));
  }

  @Test
  void toggleCellState_updatesStackTopSoStepBackRestores() {
    simulation.setState(new CellState[][]{
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    });
    simulation.toggleCellState(1, 1);
    assertEquals(ALIVE, grid.getCellState(1, 1));
    simulation.step(); // now we have history
    simulation.stepBack(); // should show toggled state (one alive at center)
    assertEquals(ALIVE, grid.getCellState(1, 1));
  }

  @Test
  void toggleCellState_otherCellsUnchanged() {
    simulation.setState(new CellState[][]{
        {ALIVE, DEAD, ALIVE},
        {DEAD, DEAD, DEAD},
        {ALIVE, DEAD, ALIVE}
    });
    simulation.toggleCellState(1, 1);
    assertEquals(ALIVE, grid.getCellState(0, 0));
    assertEquals(DEAD, grid.getCellState(0, 1));
    assertEquals(ALIVE, grid.getCellState(2, 2));
    assertEquals(ALIVE, grid.getCellState(1, 1)); // toggled from DEAD
  }

  // I - Interface: getReadOnlyGrid and getRule contracts
  @Test
  void getReadOnlyGrid_returnsNonNullWithCorrectDimensions() {
    CellState[][] initial = {{DEAD, ALIVE}, {ALIVE, DEAD}};
    Grid g = new Grid(2, 2);
    g.setCellsState(initial, rules);
    Simulation sim = new Simulation(g, rules);
    ReadOnlyGrid readOnlyGrid = sim.getReadOnlyGrid();
    assertNotNull(readOnlyGrid);
    assertEquals(2, readOnlyGrid.getRows());
    assertEquals(2, readOnlyGrid.getCols());
  }

  @Test
  void getReadOnlyGrid_dimensionsMatchGrid() {
    ReadOnlyGrid readOnlyGrid = simulation.getReadOnlyGrid();
    assertEquals(grid.getRows(), readOnlyGrid.getRows());
    assertEquals(grid.getCols(), readOnlyGrid.getCols());
  }

  @Test
  void getRule_returnsOriginalRule() {
    assertEquals(rules, simulation.getRule());
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
    simulation.setState(lShape);
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
    simulation.setState(initial);
    simulation.step();
    CellState[][] result = grid.getCellStates();
    // Verify blinker rotated
    assertEquals(ALIVE, result[1][0]);
    assertEquals(ALIVE, result[1][1]);
    assertEquals(ALIVE, result[1][2]);
  }
}
