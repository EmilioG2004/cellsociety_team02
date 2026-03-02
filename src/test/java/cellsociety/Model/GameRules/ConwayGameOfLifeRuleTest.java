package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for ConwayGameOfLifeRule
 */
class ConwayGameOfLifeRuleTest {

  private ConwayGameOfLifeRule rule;

  @BeforeEach
  void setUp() {
    rule = new ConwayGameOfLifeRule();
  }

  /** Wraps a CellState[][] in an ArrayGrid, runs one step, and returns the next state as an array. */
  private CellState[][] next(GameRule r, CellState[][] input) {
    Grid g = new ArrayGrid(input, r.getDefaultNeighborStrategy());
    Grid result = r.getNextGrid(g);
    CellState[][] out = new CellState[input.length][input[0].length];
    for (int row = 0; row < input.length; row++) {
      for (int col = 0; col < input[0].length; col++) {
        out[row][col] = result.getCellState(row, col);
      }
    }
    return out;
  }

  // Z - Zero: all dead grid
  @Test
  void getNextGrid_allDead_remainsAllDead() {
    CellState[][] allDead = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] n = next(rule, allDead);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, n[r][c]);
      }
    }
  }

  // O - One: single alive cell
  @Test
  void getNextGrid_singleAliveCell_dies() {
    CellState[][] single = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] n = next(rule, single);
    assertEquals(DEAD, n[1][1], "Single alive cell dies from underpopulation");
  }

  // M - Many: classic patterns
  @Test
  void getNextGrid_blinker_rotates() {
    CellState[][] vertical = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    CellState[][] n = next(rule, vertical);
    assertEquals(DEAD, n[0][0]);
    assertEquals(DEAD, n[0][1]);
    assertEquals(DEAD, n[0][2]);
    assertEquals(ALIVE, n[1][0]);
    assertEquals(ALIVE, n[1][1]);
    assertEquals(ALIVE, n[1][2]);
    assertEquals(DEAD, n[2][0]);
    assertEquals(DEAD, n[2][1]);
    assertEquals(DEAD, n[2][2]);
  }

  @Test
  void getNextGrid_block_remainsStable() {
    CellState[][] block = {
        {DEAD, DEAD, DEAD, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, DEAD, DEAD}
    };
    CellState[][] n = next(rule, block);
    assertEquals(ALIVE, n[1][1]);
    assertEquals(ALIVE, n[1][2]);
    assertEquals(ALIVE, n[2][1]);
    assertEquals(ALIVE, n[2][2]);
    assertEquals(DEAD, n[0][0]);
    assertEquals(DEAD, n[3][3]);
  }

  @Test
  void getNextGrid_allAlive3x3_overpopulationKillsCenterAndEdges() {
    CellState[][] allAlive = {
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE}
    };
    CellState[][] n = next(rule, allAlive);
    assertEquals(ALIVE, n[0][0]);
    assertEquals(ALIVE, n[0][2]);
    assertEquals(ALIVE, n[2][0]);
    assertEquals(ALIVE, n[2][2]);
    assertEquals(DEAD, n[0][1]);
    assertEquals(DEAD, n[1][0]);
    assertEquals(DEAD, n[1][2]);
    assertEquals(DEAD, n[2][1]);
    assertEquals(DEAD, n[1][1]);
  }

  @Test
  void getNextGrid_twoAliveNeighbors_aliveSurvives() {
    CellState[][] twoNeighbors = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] n = next(rule, twoNeighbors);
    assertEquals(ALIVE, n[0][0]);
    assertEquals(ALIVE, n[0][1]);
  }

  @Test
  void getNextGrid_exactlyThreeNeighbors_deadBecomesAlive() {
    CellState[][] lShape = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] n = next(rule, lShape);
    assertEquals(ALIVE, n[1][1]);
  }

  // B - Boundary: edge and corner neighbor counts
  @Test
  void getNextGrid_cornerCellWithThreeNeighbors_survives() {
    CellState[][] corner = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] n = next(rule, corner);
    assertEquals(ALIVE, n[0][0]);
  }

  @Test
  void getNextGrid_singleRow_cellsHaveLimitedNeighbors() {
    CellState[][] row = {{ALIVE, ALIVE, ALIVE}};
    CellState[][] n = next(rule, row);
    assertEquals(DEAD, n[0][0]);
    assertEquals(ALIVE, n[0][1]);
    assertEquals(DEAD, n[0][2]);
  }

  @Test
  void getNextGrid_singleColumn_cellsHaveLimitedNeighbors() {
    CellState[][] col = {{ALIVE}, {ALIVE}, {ALIVE}};
    CellState[][] n = next(rule, col);
    assertEquals(DEAD, n[0][0]);
    assertEquals(ALIVE, n[1][0]);
    assertEquals(DEAD, n[2][0]);
  }

  // I - Interface: fromInt, defaultNeighborStrategy
  @Test
  void fromInt_zeroAndOne_returnDeadAndAlive() {
    assertEquals(DEAD, rule.fromInt(0));
    assertEquals(ALIVE, rule.fromInt(1));
  }

  @Test
  void getDefaultNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getDefaultNeighborStrategy());
  }

  @Test
  void getNextGrid_doesNotMutateInput() {
    CellState[][] input = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    CellState[][] inputCopy = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    next(rule, input);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(inputCopy[r][c], input[r][c], "Input array should not be mutated");
      }
    }
  }

  // E - Exceptional: invalid fromInt
  @Test
  void fromInt_invalidValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(5));
  }

  @Test
  void fromInt_negativeValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(-1));
  }
}
