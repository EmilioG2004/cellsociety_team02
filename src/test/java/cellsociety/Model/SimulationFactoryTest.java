package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.Simulation.model.SimulationModel;
import cellsociety.Model.Simulation.factory.SimulationFactory;
import cellsociety.config.SimulationConfig;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class SimulationFactoryTest {

  private static final String CONWAY_RULE_CLASS = "cellsociety.Model.GameRules.ConwayGameOfLifeRule";

  // Z - Zero: empty/minimal state
  @Test
  void createSimulation_emptyGrid_returnsSimulation() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 0, 0, new int[][]{}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    assertEquals(0, sim.getReadOnlyGrid().getRows());
    assertEquals(0, sim.getReadOnlyGrid().getCols());
  }

  @Test
  void createSimulation_allDeadGrid_returnsAllDead() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 3, 3,
        new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    ReadOnlyGrid grid = sim.getReadOnlyGrid();
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, grid.getCellState(r, c));
      }
    }
  }

  // O - One: single element
  @Test
  void createSimulation_singleAliveCell_returnsAlive() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 1, 1,
        new int[][]{{1}}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    assertEquals(1, sim.getReadOnlyGrid().getRows());
    assertEquals(1, sim.getReadOnlyGrid().getCols());
    assertEquals(ALIVE, sim.getReadOnlyGrid().getCellState(0, 0));
  }

  // M - Many: multiple elements
  @Test
  void createSimulation_blinkerPattern_correctStates() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 3, 3,
        new int[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    ReadOnlyGrid grid = sim.getReadOnlyGrid();
    assertEquals(3, grid.getRows());
    assertEquals(3, grid.getCols());
    assertEquals(DEAD, grid.getCellState(0, 0));
    assertEquals(ALIVE, grid.getCellState(0, 1));
    assertEquals(DEAD, grid.getCellState(0, 2));
    assertEquals(DEAD, grid.getCellState(1, 0));
    assertEquals(ALIVE, grid.getCellState(1, 1));
    assertEquals(DEAD, grid.getCellState(1, 2));
    assertEquals(DEAD, grid.getCellState(2, 0));
    assertEquals(ALIVE, grid.getCellState(2, 1));
    assertEquals(DEAD, grid.getCellState(2, 2));
  }

  @Test
  void createSimulation_allAliveGrid() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 2, 2,
        new int[][]{{1, 1}, {1, 1}}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    ReadOnlyGrid grid = sim.getReadOnlyGrid();
    for (int r = 0; r < 2; r++) {
      for (int c = 0; c < 2; c++) {
        assertEquals(ALIVE, grid.getCellState(r, c));
      }
    }
  }

  // B - Boundary: dimension mismatches
  @Test
  void createSimulation_gridSmallerThanDeclaredDimensions_throws() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 10, 10,
        new int[][]{{0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(config));
  }

  @Test
  void createSimulation_invalidStateValue_throws() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 1, 1,
        new int[][]{{5}}, new HashMap<>());
    assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(config));
  }

  // I - Interface: returns correct types
  @Test
  void createSimulation_returnsSimulationWithReadOnlyGridAndRule() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 3, 3,
        new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    assertNotNull(sim);
    assertNotNull(sim.getReadOnlyGrid());
    assertNotNull(sim.getRule());
    assertEquals(3, sim.getReadOnlyGrid().getRows());
    assertEquals(3, sim.getReadOnlyGrid().getCols());
  }

  @Test
  void createSimulation_gameOfLife_returnsConwayRule() {
    SimulationConfig config = new SimulationConfig(
        "GameOfLife", CONWAY_RULE_CLASS, null, null, "Test", "Author", "Desc", 1, 1,
        new int[][]{{0}}, new HashMap<>());
    SimulationModel sim = SimulationFactory.createSimulation(config);
    assertEquals(ConwayGameOfLifeRule.class, sim.getRule().getClass());
  }

  // E - Exceptional: invalid rule class
  @Test
  void createSimulation_unknownRuleClass_throwsWithClearMessage() {
    SimulationConfig config = new SimulationConfig(
        "InvalidType", "not.a.RealRuleClass", null, null, "Test", "Author", "Desc", 1, 1,
        new int[][]{{0}}, new HashMap<>());
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
        () -> SimulationFactory.createSimulation(config));
    assertTrue(thrown.getMessage().contains("Rule class not found")
        || thrown.getMessage().contains("not.a.RealRuleClass"),
        "Expected message about missing rule class, got: " + thrown.getMessage());
  }

  @Test
  void createSimulation_nullConfig_throws() {
    assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(null));
  }
}
