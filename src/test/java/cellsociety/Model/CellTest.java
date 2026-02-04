package cellsociety.Model;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit
.jupiter.api.Test;

/**
 * Test class for Cell
 */
class CellTest {
  private Cell deadCell;
  private Cell aliveCell;

  @BeforeEach
  void setUp() {
    deadCell = new Cell(0);
    aliveCell = new Cell(1);
  }

  // Z - Zero: initial/default state
  @Test
  void getState_returnsInitialState() {
    assertEquals(0, deadCell.getState());
    assertEquals(1, aliveCell.getState());
  }

  @Test
  void cell_initializedWithZero_isDead() {
    Cell cell = new Cell(0);
    assertEquals(0, cell.getState());
  }

  // O - One: single operation
  @Test
  void setState_singleUpdate_changesState() {
    deadCell.setState(1);
    assertEquals(1, deadCell.getState());
  }

  @Test
  void updateState_commitsNextStateToCurrent() {
    deadCell.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    assertEquals(0, deadCell.getState(), "State should not change until committed");
    deadCell.updateState();
    assertEquals(1, deadCell.getState(), "State should now be committed");
  }

  // M - Many: multiple operations
  @Test
  void multipleStateUpdates_retainLatestState() {
    Cell cell = new Cell(0);
    cell.setState(1);
    cell.setState(2);
    cell.setState(0);
    assertEquals(0, cell.getState());
  }

  @Test
  void updateNextState_withNeighbors_usesRuleLogic() {
    Cell center = new Cell(1);
    List<ReadOnlyCell> threeAlive = List.of(new Cell(1), new Cell(1), new Cell(1));
    center.updateNextState(new ConwayGameOfLifeRule(), threeAlive);
    center.updateState();
    assertEquals(1, center.getState(), "Alive cell with 3 neighbors survives");
  }

  @Test
  void updateNextState_noNeighbors_cellDies() {
    Cell lonely = new Cell(1);
    lonely.updateNextState(new ConwayGameOfLifeRule(), Collections.emptyList());
    lonely.updateState();
    assertEquals(0, lonely.getState(), "Cell with no neighbors dies");
  }

  // B - Boundaries: edge/extreme values
  @Test
  void cell_withMaxIntState_storesCorrectly() {
    Cell cell = new Cell(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, cell.getState());
  }

  @Test
  void cell_withNegativeState_storesCorrectly() {
    Cell cell = new Cell(-1);
    assertEquals(-1, cell.getState());
  }

  // I - Interfaces: ReadOnlyCell contract
  @Test
  void cell_implementsReadOnlyCell() {
    ReadOnlyCell readOnly = new Cell(1);
    assertEquals(1, readOnly.getState());
  }

  @Test
  void readOnlyCell_cannotMutate() {
    Cell cell = new Cell(0);
    ReadOnlyCell readOnly = cell;
    // ReadOnlyCell interface only has getState(), no setState
    assertEquals(0, readOnly.getState());
  }

  // S - Simple scenarios: typical use cases
  @Test
  void updateNextState_thenCommit_changesState() {
    Cell cell = new Cell(0);
    cell.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    cell.updateState();
    assertEquals(1, cell.getState());
  }

  @Test
  void updateState_withoutUpdateNext_resetsToDefault() {
    Cell cell = new Cell(1);
    cell.updateState();  // nextState is -1 initially
    assertEquals(-1, cell.getState());
  }

  @Test
  void twoPassUpdate_ensuresConsistentGeneration() {
    Cell cell1 = new Cell(0);
    Cell cell2 = new Cell(1);

    cell1.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    cell2.updateNextState(new AlwaysDeadRule(), Collections.emptyList());

    assertEquals(0, cell1.getState());
    assertEquals(1, cell2.getState());

    // Pass 2: commit
    cell1.updateState();
    cell2.updateState();

    assertEquals(1, cell1.getState());
    assertEquals(0, cell2.getState());
  }

  // Negative testing: unusual but valid scenarios
  @Test
  void updateNextState_multipleTimesBeforeCommit_lastOneWins() {
    Cell cell = new Cell(0);
    cell.updateNextState(new AlwaysAliveRule(), Collections.emptyList());
    cell.updateNextState(new AlwaysDeadRule(), Collections.emptyList());
    cell.updateState();
    assertEquals(0, cell.getState(), "Last updateNextState should win");
  }

  @Test
  void updateNextState_manyNeighbors_handlesCorrectly() {
    Cell cell = new Cell(1);
    List<ReadOnlyCell> eightAlive = List.of(
        new Cell(1), new Cell(1), new Cell(1), new Cell(1),
        new Cell(1), new Cell(1), new Cell(1), new Cell(1)
    );
    cell.updateNextState(new ConwayGameOfLifeRule(), eightAlive);
    cell.updateState();
    assertEquals(0, cell.getState(), "Cell with 8 neighbors dies (overpopulation)");
  }

  /** Test rule: always returns 1 (ALIVE). */
  private static class AlwaysAliveRule extends GameRule {
    public AlwaysAliveRule() {
      super(new cellsociety.Model.Utils.MooreNeighborStrategy());
    }

    @Override
    public int getNextState(int currentState, List<ReadOnlyCell> neighbors) {
      return 1;
    }

    @Override
    public java.util.Map<Integer, String> getStateColors() {
      return java.util.Map.of(0, "WHITE", 1, "GREEN");
    }
  }

  /** Test rule: always returns 0 (DEAD). */
  private static class AlwaysDeadRule extends GameRule {
    public AlwaysDeadRule() {
      super(new cellsociety.Model.Utils.MooreNeighborStrategy());
    }

    @Override
    public int getNextState(int currentState, List<ReadOnlyCell> neighbors) {
      return 0;
    }

    @Override
    public java.util.Map<Integer, String> getStateColors() {
      return java.util.Map.of(0, "WHITE", 1, "GREEN");
    }
  }
}
