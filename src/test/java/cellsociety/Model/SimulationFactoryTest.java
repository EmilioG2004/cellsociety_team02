package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.config.SimulationConfig;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class SimulationFactoryTest {

  // Z - Zero: empty/minimal state
  @Test
  void createSimulation_emptyGrid_returnsSimulation() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 0, 0, new int[][]{}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    assertEquals(0, sim.getGridHandler().getRows());
    assertEquals(0, sim.getGridHandler().getCols());
  }

  @Test
  void createSimulation_allDeadGrid_returnsAllDead() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 3, 3,
        new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    GridHandler handler = sim.getGridHandler();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, handler.getCellState(r, c));
      }
    }
  }

  // O - One: single element
  @Test
  void createSimulation_singleAliveCell_returnsAlive() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 1, 1,
        new int[][]{{1}}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    assertEquals(1, sim.getGridHandler().getRows());
    assertEquals(1, sim.getGridHandler().getCols());
    assertEquals(ALIVE, sim.getGridHandler().getCellState(0, 0));
  }

  // M - Many: multiple elements
  @Test
  void createSimulation_blinkerPattern_correctStates() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 3, 3,
        new int[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    GridHandler handler = sim.getGridHandler();
    assertEquals(3, handler.getRows());
    assertEquals(3, handler.getCols());
    assertEquals(DEAD, handler.getCellState(0, 0));
    assertEquals(ALIVE, handler.getCellState(0, 1));
    assertEquals(DEAD, handler.getCellState(0, 2));
    assertEquals(DEAD, handler.getCellState(1, 0));
    assertEquals(ALIVE, handler.getCellState(1, 1));
    assertEquals(DEAD, handler.getCellState(1, 2));
    assertEquals(DEAD, handler.getCellState(2, 0));
    assertEquals(ALIVE, handler.getCellState(2, 1));
    assertEquals(DEAD, handler.getCellState(2, 2));
  }

  @Test
  void createSimulation_allAliveGrid() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 2, 2,
        new int[][]{{1, 1}, {1, 1}}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    GridHandler handler = sim.getGridHandler();
    for (int r = 0; r < 2; r++) {
      for (int c = 0; c < 2; c++) {
        assertEquals(ALIVE, handler.getCellState(r, c));
      }
    }
  }

  // B - Boundary: dimension mismatches
  @Test
  void createSimulation_gridSmallerThanDeclaredDimensions_throws() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 10, 10,
        new int[][]{{0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(config));
  }

  @Test
  void createSimulation_invalidStateValue_throws() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 1, 1,
        new int[][]{{5}}, new HashMap<>());
    assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(config));
  }

  // I - Interface: returns correct types
  @Test
  void createSimulation_returnsSimulationWithGridHandlerAndRule() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 3, 3,
        new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    assertNotNull(sim.getGridHandler());
    assertNotNull(sim.getRule());
    assertEquals(3, sim.getGridHandler().getRows());
    assertEquals(3, sim.getGridHandler().getCols());
  }

  @Test
  void createSimulation_gameOfLife_returnsConwayRule() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", "Test", "Author", "Desc", 1, 1,
        new int[][]{{0}}, new HashMap<>());
    Simulation sim = SimulationFactory.createSimulation(config);
    assertEquals(ConwayGameOfLifeRule.class, sim.getRule().getClass());
  }

  // E - Exceptional: invalid simulation type
  @Test
  void createSimulation_invalidSimulationType_throws() {
    SimulationConfig config = new SimulationConfig(
        "InvalidType", "Test", "Author", "Desc", 1, 1,
        new int[][]{{0}}, new HashMap<>());
    assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(config));
  }
}
