package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Simulation.Simulation;
import cellsociety.Model.Simulation.SimulationDriver;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for SimulationDriver
 */
class SimulationDriverTest {

  private SimulationDriver driver;
  private Simulation simulation;
  private Grid grid;
  private GameRule rules;

  @BeforeEach
  void setUp() {
    driver = new SimulationDriver();
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

  @AfterEach
  void tearDown() {
    if (driver.isRunning()) {
      driver.pause();
    }
  }

  // Z - Zero: no simulation, initial state
  @Test
  void step_withNoSimulation_doesNothing() {
    driver.step();
    assertFalse(driver.isRunning());
    assertFalse(driver.hasSimulation());
  }

  @Test
  void start_withNoSimulation_doesNothing() {
    driver.start();
    assertFalse(driver.isRunning());
  }

  @Test
  void getSimulation_withNoSimulationSet_throws() {
    assertFalse(driver.hasSimulation());
    assertThrows(IllegalStateException.class, () -> driver.getSimulation());
  }

  @Test
  void getStepsPerSecond_returnsDefault() {
    assertEquals(1.0, driver.getStepsPerSecond());
  }

  @Test
  void isRunning_initiallyFalse() {
    assertFalse(driver.isRunning());
  }

  // O - One: single operation
  @Test
  void setSimulation_thenGetSimulation_returnsSame() {
    driver.setSimulation(simulation);
    assertTrue(driver.hasSimulation());
    assertEquals(simulation, driver.getSimulation());
  }

  @Test
  void step_withSimulation_advancesOneGeneration() {
    CellState[][] blinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    simulation.setState(blinker);
    driver.setSimulation(simulation);

    driver.step();

    CellState[][] result = grid.getCellStates();
    // Blinker should become horizontal
    assertEquals(ALIVE, result[1][0]);
    assertEquals(ALIVE, result[1][1]);
    assertEquals(ALIVE, result[1][2]);
  }

  @Test
  void step_invokesCallback() {
    simulation.setState(new CellState[][]{{DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}});
    driver.setSimulation(simulation);
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.step();

    assertEquals(1, callCount.get());
  }

  // M - Many: multiple operations
  @Test
  void step_multipleTimes_advancesMultipleGenerations() {
    CellState[][] blinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    simulation.setState(blinker);
    driver.setSimulation(simulation);
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.step(); // vertical -> horizontal
    driver.step(); // horizontal -> vertical

    assertEquals(2, callCount.get());
    CellState[][] result = grid.getCellStates();
    // Should be back to vertical blinker
    assertEquals(ALIVE, result[0][1]);
    assertEquals(ALIVE, result[1][1]);
    assertEquals(ALIVE, result[2][1]);
  }

  @Test
  void start_thenPause_stopsRunning() {
    simulation.setState(new CellState[][]{{DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}});
    driver.setSimulation(simulation);
    driver.start();
    assertTrue(driver.isRunning());

    driver.pause();
    assertFalse(driver.isRunning());
  }

  @Test
  void pause_whenNotRunning_isNoOp() {
    driver.setSimulation(simulation);
    driver.pause();
    assertFalse(driver.isRunning());
  }

  @Test
  void start_whenAlreadyRunning_isNoOp() {
    simulation.setState(new CellState[][]{{DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}});
    driver.setSimulation(simulation);
    driver.start();
    driver.start(); // second start is no-op
    assertTrue(driver.isRunning());
    driver.pause();
  }

  @Test
  void step_whilePaused_stillWorks() {
    CellState[][] blinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    simulation.setState(blinker);
    driver.setSimulation(simulation);
    driver.start();
    driver.pause();

    driver.step();

    CellState[][] result = grid.getCellStates();
    assertEquals(ALIVE, result[1][0]);
  }

  // B - Boundaries: speed, edge values
  @Test
  void setSpeed_withZero_usesDefault() {
    driver.setSpeed(0);
    assertEquals(1.0, driver.getStepsPerSecond());
  }

  @Test
  void setSpeed_withNegative_usesDefault() {
    driver.setSpeed(-5);
    assertEquals(1.0, driver.getStepsPerSecond());
  }

  @Test
  void setSpeed_withPositive_updatesRate() {
    driver.setSpeed(5.0);
    assertEquals(5.0, driver.getStepsPerSecond());
  }

  @Test
  void setSpeed_verySmall_updatesRate() {
    driver.setSpeed(0.001);
    assertEquals(0.001, driver.getStepsPerSecond());
  }

  @Test
  void setSimulation_toNull_clearsSimulation() {
    driver.setSimulation(simulation);
    driver.setSimulation(null);
    assertFalse(driver.hasSimulation());
    assertThrows(IllegalStateException.class, () -> driver.getSimulation());
  }

  @Test
  void step_afterSettingSimulationToNull_isNoOp() {
    driver.setSimulation(simulation);
    driver.setSimulation(null);
    driver.step(); // should not throw
    assertFalse(driver.hasSimulation());
  }

  @Test
  void toggleCellState_withNoSimulation_doesNotThrow() {
    driver.toggleCellState(0, 0); // no-op when simulation is null
    assertFalse(driver.hasSimulation());
  }

  @Test
  void toggleCellState_withSimulation_updatesCell() {
    simulation.setState(new CellState[][]{
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    });
    driver.setSimulation(simulation);
    driver.toggleCellState(1, 1);
    assertEquals(ALIVE, grid.getCellState(1, 1));
  }

  @Test
  void toggleCellState_withSimulation_twice_returnsToOriginal() {
    simulation.setState(new CellState[][]{
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    });
    driver.setSimulation(simulation);
    driver.toggleCellState(1, 1);
    driver.toggleCellState(1, 1);
    assertEquals(ALIVE, grid.getCellState(1, 1));
  }

  // I - Interface: integration with Simulation
  @Test
  void periodicStepping_invokesCallbackMultipleTimes() throws InterruptedException {
    simulation.setState(new CellState[][]{{DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}});
    driver.setSimulation(simulation);
    driver.setSpeed(50); // 50 steps per second
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.start();
    Thread.sleep(150);
    driver.pause();

    assertTrue(callCount.get() >= 1, "Callback should fire at least once");
  }

  @Test
  void setSpeed_whileRunning_reschedulesAtNewRate() throws InterruptedException {
    simulation.setState(new CellState[][]{{DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}});
    driver.setSimulation(simulation);
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.setSpeed(2);
    driver.start();
    driver.setSpeed(50); // speed up while running
    Thread.sleep(150);
    driver.pause();

    assertTrue(callCount.get() >= 1, "Should have stepped after speed change");
  }

  // E - Exceptional: replacing simulation while running
  @Test
  void setSimulation_whileRunning_switchesToNewSimulation() throws InterruptedException {
    simulation.setState(new CellState[][]{{DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}, {DEAD, DEAD, DEAD}});
    driver.setSimulation(simulation);
    driver.start();

    Grid newGrid = new Grid(2, 2);
    newGrid.setCellsState(new CellState[][]{{ALIVE, DEAD}, {DEAD, ALIVE}}, new ConwayGameOfLifeRule());
    Simulation newSim = new Simulation(newGrid, new ConwayGameOfLifeRule());
    driver.setSimulation(newSim);

    assertEquals(newSim, driver.getSimulation());
    driver.pause();
  }

  // S - Simple: typical flows
  @Test
  void fullFlow_setSimulation_step_verifyState() {
    CellState[][] blinker = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    simulation.setState(blinker);
    driver.setSimulation(simulation);

    driver.step();
    CellState[][] afterFirst = grid.getCellStates();
    assertEquals(ALIVE, afterFirst[1][1]);

    driver.step();
    CellState[][] afterSecond = grid.getCellStates();
    assertEquals(ALIVE, afterSecond[0][1]);
    driver.stepBack();
    CellState[][] afterFirstBack = grid.getCellStates();
    assertEquals(ALIVE, afterFirstBack[1][1]);
    driver.stepBack();
    CellState[][] afterSecondBack = grid.getCellStates();
    assertEquals(ALIVE, afterSecondBack[0][1], "Second stepBack restores to initial blinker (setState)");

    assertFalse(driver.isRunning());
  }
}
