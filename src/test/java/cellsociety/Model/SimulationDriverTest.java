package cellsociety.Model;

import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.CellState.CellState;
import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    rules = new ConwayGameOfLifeRule();
    simulation = new Simulation(grid, rules);
  }

  @AfterEach
  void tearDown() {
    driver.pause();
  }

  // Z - Zero: no simulation, initial state
  @Test
  void step_withNoSimulation_doesNothing() {
    driver.step();
    assertFalse(driver.isRunning());
    assertNull(driver.getSimulation());
  }

  @Test
  void start_withNoSimulation_doesNothing() {
    driver.start();
    assertFalse(driver.isRunning());
  }

  @Test
  void getSimulation_initiallyNull() {
    assertNull(driver.getSimulation());
  }

  @Test
  void getStepsPerSecond_returnsDefault() {
    assertEquals(1.0, driver.getStepsPerSecond());
  }

  // O - One: single operation
  @Test
  void setSimulation_thenGetSimulation_returnsSame() {
    driver.setSimulation(simulation);
    assertNotNull(driver.getSimulation());
    assertEquals(simulation, driver.getSimulation());
  }

  @Test
  void step_withSimulation_advancesOneGeneration() {
    int[][] blinker = {
      {0, 1, 0},
      {0, 1, 0},
      {0, 1, 0}
    };
    grid.setCellsState(new CellState[][] {{DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}});
    driver.setSimulation(simulation);

    driver.step();

    assertEquals(1, grid.getCellState(1, 0));
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(1, 2));
  }

  @Test
  void step_invokesCallback() {
    driver.setSimulation(simulation);
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.step();

    assertEquals(1, callCount.get());
  }

  @Test
  void step_withoutCallback_doesNotThrow() {
    driver.setSimulation(simulation);
    driver.step();
    assertEquals(1, grid.getCellState(1, 1)); // blinker middle
  }

  // M - Many: multiple operations
  @Test
  void step_multipleTimes_advancesMultipleGenerations() {
    CellState[][] blinker = {
      {DEAD, ALIVE, DEAD},
      {DEAD, ALIVE, DEAD},
      {DEAD, ALIVE, DEAD}
    };
    grid.setCellsState(blinker);
    driver.setSimulation(simulation);
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.step();
    driver.step();

    assertEquals(2, callCount.get());
    assertEquals(1, grid.getCellState(0, 1));
    assertEquals(1, grid.getCellState(1, 1));
    assertEquals(1, grid.getCellState(2, 1));
  }

  @Test
  void start_thenPause_stopsRunning() {
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
    driver.setSimulation(simulation);
    driver.start();
    driver.start();
    assertTrue(driver.isRunning());
    driver.pause();
  }

  @Test
  void step_whilePaused_stillWorks() {
    grid.setCellsState(new CellState[][] {{DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}});
    driver.setSimulation(simulation);
    driver.start();
    driver.pause();

    driver.step();

    assertEquals(1, grid.getCellState(1, 0));
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
  void setSimulation_toNull_clearsSimulation() {
    driver.setSimulation(simulation);
    driver.setSimulation(null);
    assertNull(driver.getSimulation());
  }

  @Test
  void step_afterSettingSimulationToNull_isNoOp() {
    driver.setSimulation(simulation);
    driver.setSimulation(null);
    driver.step();
    assertNull(driver.getSimulation());
  }

  // I - Interfaces: integration with Simulation
  @Test
  void periodicStepping_invokesCallbackMultipleTimes() throws InterruptedException {
    grid.setCellsState(new CellState[][] {{DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}});
    driver.setSimulation(simulation);
    driver.setSpeed(50); // 50 steps per second
    AtomicInteger callCount = new AtomicInteger(0);
    driver.setOnStepCallback(callCount::incrementAndGet);

    driver.start();
    Thread.sleep(150);
    driver.pause();

    assertTrue(callCount.get() >= 1, "Callback should fire at least once");
  }

  // S - Simple: typical flows
  @Test
  void fullFlow_setSimulation_step_pause() {
    grid.setCellsState(new CellState[][] {{DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}, {DEAD, ALIVE, DEAD}});
    driver.setSimulation(simulation);

    driver.step();
    assertEquals(1, grid.getCellState(1, 1));

    driver.step();
    assertEquals(1, grid.getCellState(0, 1));

    assertFalse(driver.isRunning());
  }
}
