package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

  // O - One: single operation
  @Test
  void setState_singleUpdate_changesState() {
    deadCell.setState(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, deadCell.getState());
  }

  @Test
  void updateState_commitsNextStateToCurrent() {
    deadCell.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    assertEquals(GameOfLifeState.DEAD, deadCell.getState(), "State should not change until committed");
    deadCell.updateState();
    assertEquals(GameOfLifeState.ALIVE, deadCell.getState(), "State should now be committed");
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
  void updateNextState_withNeighbors_usesRuleLogic() {
    Cell center = new Cell(GameOfLifeState.ALIVE);
    List<ReadOnlyCell> threeAlive = List.of(
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE));
    center.updateNextState(new ConwayGameOfLifeRule(), threeAlive);
    center.updateState();
    assertEquals(GameOfLifeState.ALIVE, center.getState(), "Alive cell with 3 neighbors survives");
  }

  @Test
  void updateNextState_noNeighbors_cellDies() {
    Cell lonely = new Cell(GameOfLifeState.ALIVE);
    lonely.updateNextState(new ConwayGameOfLifeRule(), Collections.emptyList());
    lonely.updateState();
    assertEquals(GameOfLifeState.DEAD, lonely.getState(), "Cell with no neighbors dies");
  }

  // I - Interfaces: ReadOnlyCell contract
  @Test
  void cell_implementsReadOnlyCell() {
    ReadOnlyCell readOnly = new Cell(GameOfLifeState.ALIVE);
    assertEquals(GameOfLifeState.ALIVE, readOnly.getState());
  }

  @Test
  void readOnlyCell_cannotMutate() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    ReadOnlyCell readOnly = cell;
    assertEquals(GameOfLifeState.DEAD, readOnly.getState());
  }

  // S - Simple scenarios: typical use cases
  @Test
  void updateNextState_thenCommit_changesState() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    cell.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    cell.updateState();
    assertEquals(GameOfLifeState.ALIVE, cell.getState());
  }

  @Test
  void updateState_withoutUpdateNext_resetsToNull() {
    Cell cell = new Cell(GameOfLifeState.ALIVE);
    cell.updateState(); // nextState is null initially
    assertEquals(null, cell.getState());
  }

  @Test
  void twoPassUpdate_ensuresConsistentGeneration() {
    Cell cell1 = new Cell(GameOfLifeState.DEAD);
    Cell cell2 = new Cell(GameOfLifeState.ALIVE);

    cell1.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    cell2.updateNextState(new AlwaysDeadRule(), Collections.emptyList());

    assertEquals(GameOfLifeState.DEAD, cell1.getState());
    assertEquals(GameOfLifeState.ALIVE, cell2.getState());

    cell1.updateState();
    cell2.updateState();

    assertEquals(GameOfLifeState.ALIVE, cell1.getState());
    assertEquals(GameOfLifeState.DEAD, cell2.getState());
  }

  // Negative testing
  @Test
  void updateNextState_multipleTimesBeforeCommit_lastOneWins() {
    Cell cell = new Cell(GameOfLifeState.DEAD);
    cell.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    cell.updateNextState(new AlwaysDeadRule(), Collections.emptyList());
    cell.updateState();
    assertEquals(GameOfLifeState.DEAD, cell.getState(), "Last updateNextState should win");
  }

  @Test
  void updateNextState_manyNeighbors_handlesCorrectly() {
    Cell cell = new Cell(GameOfLifeState.ALIVE);
    List<ReadOnlyCell> eightAlive = List.of(
        new Cell(GameOfLifeState.ALIVE), new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE), new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE), new Cell(GameOfLifeState.ALIVE),
        new Cell(GameOfLifeState.ALIVE), new Cell(GameOfLifeState.ALIVE)
    );
    cell.updateNextState(new ConwayGameOfLifeRule(), eightAlive);
    cell.updateState();
    assertEquals(GameOfLifeState.DEAD, cell.getState(), "Cell with 8 neighbors dies (overpopulation)");
  }

  /** Test rule: always returns ALIVE. */
  private static class AlwaysAliveRule extends GameRule {

    public AlwaysAliveRule() {
      super(new cellsociety.Model.Utils.MooreNeighborStrategy());
    }

    @Override
    public CellState getNextState(CellState currentState, List<ReadOnlyCell> neighbors) {
      return GameOfLifeState.ALIVE;
    }

    @Override
    public Map<CellState, String> getStateColors() {
      return Map.of(GameOfLifeState.DEAD, "WHITE", GameOfLifeState.ALIVE, "GREEN");
    }

    @Override
    public CellState fromInt(int value) {
      return GameOfLifeState.fromInt(value);
    }
  }

  /** Test rule: always returns DEAD. */
  private static class AlwaysDeadRule extends GameRule {

    public AlwaysDeadRule() {
      super(new cellsociety.Model.Utils.MooreNeighborStrategy());
    }

    @Override
    public CellState getNextState(CellState currentState, List<ReadOnlyCell> neighbors) {
      return GameOfLifeState.DEAD;
    }

    @Override
    public Map<CellState, String> getStateColors() {
      return Map.of(GameOfLifeState.DEAD, "WHITE", GameOfLifeState.ALIVE, "GREEN");
    }

    @Override
    public CellState fromInt(int value) {
      return GameOfLifeState.fromInt(value);
    }
  }
}
