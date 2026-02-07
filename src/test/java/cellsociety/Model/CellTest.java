package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Cell
 */
class CellTest {

  private Cell deadCell;
  private Cell aliveCell;

  @BeforeEach
  void setUp() {
    deadCell = new Cell(GameOfLifeState.DEAD);
    aliveCell = new Cell(GameOfLifeState.ALIVE);
  }

  // Z - Zero: initial/default state
  @Test
  void getState_returnsInitialState() {
    assertEquals(GameOfLifeState.DEAD, deadCell.getState());
    assertEquals(GameOfLifeState.ALIVE, aliveCell.getState());
  }

  @Test
  void cell_initializedWithDead_isDead() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    assertEquals(GameOfLifeState.DEAD, cell.getState());
  }

  @Test
  void cell_initializedWithNull_returnsNull() {
    Cell cell = new Cell(null);
    assertNull(cell.getState());
  }

  // O - One: single operation
  @Test
  void setState_deadToAlive_changesState() {
    deadCell.setState(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, deadCell.getState());
  }

  @Test
  void setState_aliveToDead_changesState() {
    aliveCell.setState(GameOfLifeState.DEAD);
    assertEquals(GameOfLifeState.DEAD, aliveCell.getState());
  }

  // M - Many: multiple operations
  @Test
  void multipleStateUpdates_retainLatestState() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    cell.setState(GameOfLifeState.ALIVE);
    cell.setState(GameOfLifeState.DEAD);
    assertEquals(GameOfLifeState.DEAD, cell.getState());
  }

  @Test
  void multipleRapidStateChanges_retainsLast() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    cell.setState(GameOfLifeState.ALIVE);
    cell.setState(GameOfLifeState.DEAD);
    cell.setState(GameOfLifeState.ALIVE);
    cell.setState(GameOfLifeState.DEAD);
    cell.setState(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, cell.getState());
  }

  // B - Boundary: edge states
  @Test
  void setState_toSameState_doesNotChange() {
    aliveCell.setState(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, aliveCell.getState());
  }

  @Test
  void setState_toNull_setsNull() {
    aliveCell.setState(null);
    assertNull(aliveCell.getState());
  }

  // I - Interface: Cell contract
  @Test
  void getState_afterConstruction_matchesConstructorArg() {
    for (GameOfLifeState state : GameOfLifeState.values()) {
      Cell cell = new Cell(state);
      assertEquals(state, cell.getState());
    }
  }

  // E - Exceptional: state transitions
  @Test
  void setState_fromNullToValid_works() {
    Cell cell = new Cell(null);
    cell.setState(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, cell.getState());
  }

  // S - Simple: typical use case
  @Test
  void typicalUsage_createAndToggle() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    assertEquals(GameOfLifeState.DEAD, cell.getState());
    cell.setState(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, cell.getState());
  }
}
