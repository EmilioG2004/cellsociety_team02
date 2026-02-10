package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;

import static cellsociety.Model.CellState.GameOfLifeState.ALIVE;
import static cellsociety.Model.CellState.GameOfLifeState.DEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
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

  // Z - Zero: all dead grid
  @Test
  void getNextStates_allDead_remainsAllDead() {
    CellState[][] allDead = {
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] next = rule.getNextStates(allDead);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(DEAD, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_1x1DeadGrid_remainsDead() {
    CellState[][] single = {{DEAD}};
    CellState[][] next = rule.getNextStates(single);
    assertEquals(DEAD, next[0][0]);
  }

  // O - One: single alive cell
  @Test
  void getNextStates_singleAliveCell_dies() {
    CellState[][] single = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] next = rule.getNextStates(single);
    assertEquals(DEAD, next[1][1], "Single alive cell dies from underpopulation");
  }

  @Test
  void getNextStates_1x1AliveCell_dies() {
    CellState[][] tiny = {{ALIVE}};
    CellState[][] next = rule.getNextStates(tiny);
    assertEquals(DEAD, next[0][0], "Alone cell with no neighbors dies");
  }

  // M - Many: classic patterns
  @Test
  void getNextStates_blinker_rotates() {
    CellState[][] vertical = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD}
    };
    CellState[][] next = rule.getNextStates(vertical);
    // Becomes horizontal
    assertEquals(DEAD, next[0][0]);
    assertEquals(DEAD, next[0][1]);
    assertEquals(DEAD, next[0][2]);
    assertEquals(ALIVE, next[1][0]);
    assertEquals(ALIVE, next[1][1]);
    assertEquals(ALIVE, next[1][2]);
    assertEquals(DEAD, next[2][0]);
    assertEquals(DEAD, next[2][1]);
    assertEquals(DEAD, next[2][2]);
  }

  @Test
  void getNextStates_block_remainsStable() {
    CellState[][] block = {
        {DEAD, DEAD, DEAD, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, DEAD, DEAD}
    };
    CellState[][] next = rule.getNextStates(block);
    assertEquals(ALIVE, next[1][1]);
    assertEquals(ALIVE, next[1][2]);
    assertEquals(ALIVE, next[2][1]);
    assertEquals(ALIVE, next[2][2]);
    assertEquals(DEAD, next[0][0]);
    assertEquals(DEAD, next[3][3]);
  }

  @Test
  void getNextStates_allAlive3x3_overpopulationKillsCenterAndEdges() {
    CellState[][] allAlive = {
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE},
        {ALIVE, ALIVE, ALIVE}
    };
    CellState[][] next = rule.getNextStates(allAlive);
    // Corners have 3 neighbors -> survive
    assertEquals(ALIVE, next[0][0]);
    assertEquals(ALIVE, next[0][2]);
    assertEquals(ALIVE, next[2][0]);
    assertEquals(ALIVE, next[2][2]);
    // Edges have 5 neighbors -> die
    assertEquals(DEAD, next[0][1]);
    assertEquals(DEAD, next[1][0]);
    assertEquals(DEAD, next[1][2]);
    assertEquals(DEAD, next[2][1]);
    // Center has 8 neighbors -> dies
    assertEquals(DEAD, next[1][1]);
  }

  @Test
  void getNextStates_twoAliveNeighbors_aliveSurvives() {
    CellState[][] twoNeighbors = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] next = rule.getNextStates(twoNeighbors);
    // (0,0) has 2 alive neighbors -> survives
    assertEquals(ALIVE, next[0][0]);
    // (0,1) has 2 alive neighbors (0,0) and (1,0) -> survives
    assertEquals(ALIVE, next[0][1]);
  }

  @Test
  void getNextStates_exactlyThreeNeighbors_deadBecomesAlive() {
    CellState[][] lShape = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] next = rule.getNextStates(lShape);
    // (1,1) is dead with exactly 3 alive neighbors -> becomes alive
    assertEquals(ALIVE, next[1][1]);
  }

  // B - Boundary: edge and corner neighbor counts
  @Test
  void getNextStates_cornerCellWithThreeNeighbors_survives() {
    CellState[][] corner = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    CellState[][] next = rule.getNextStates(corner);
    // (0,0) corner has 3 alive neighbors -> survives
    assertEquals(ALIVE, next[0][0]);
  }

  @Test
  void getNextStates_singleRow_cellsHaveLimitedNeighbors() {
    CellState[][] row = {{ALIVE, ALIVE, ALIVE}};
    CellState[][] next = rule.getNextStates(row);
    // Each cell has at most 1-2 neighbors (single row)
    // (0,0) has 1 neighbor -> dies
    assertEquals(DEAD, next[0][0]);
    // (0,1) has 2 neighbors -> survives
    assertEquals(ALIVE, next[0][1]);
    // (0,2) has 1 neighbor -> dies
    assertEquals(DEAD, next[0][2]);
  }

  @Test
  void getNextStates_singleColumn_cellsHaveLimitedNeighbors() {
    CellState[][] col = {{ALIVE}, {ALIVE}, {ALIVE}};
    CellState[][] next = rule.getNextStates(col);
    assertEquals(DEAD, next[0][0]);
    assertEquals(ALIVE, next[1][0]);
    assertEquals(DEAD, next[2][0]);
  }

  // I - Interface: getStateColors, fromInt, neighborStrategy
  @Test
  void getStateColors_containsBothStates() {
    Map<CellState, String> colors = rule.getStateColors();
    assertNotNull(colors);
    assertEquals(2, colors.size());
    assertNotNull(colors.get(DEAD));
    assertNotNull(colors.get(ALIVE));
  }

  @Test
  void fromInt_zero_returnsDead() {
    assertEquals(DEAD, rule.fromInt(0));
  }

  @Test
  void fromInt_one_returnsAlive() {
    assertEquals(ALIVE, rule.fromInt(1));
  }

  @Test
  void getNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getNeighborStrategy());
  }

  @Test
  void getNextStates_doesNotMutateInput() {
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
    rule.getNextStates(input);
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

  // S - Simple: basic Conway rules
  @Test
  void aliveCellWith0Neighbors_dies() {
    CellState[][] grid = {
        {DEAD, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    assertEquals(DEAD, rule.getNextStates(grid)[1][1]);
  }

  @Test
  void aliveCellWith1Neighbor_dies() {
    CellState[][] grid = {
        {DEAD, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, DEAD}
    };
    // (1,1) has 1 alive neighbor -> dies
    assertEquals(DEAD, rule.getNextStates(grid)[1][1]);
  }

  @Test
  void aliveCellWith2Neighbors_survives() {
    CellState[][] grid = {
        {ALIVE, DEAD, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, ALIVE}
    };
    // (1,1) has 2 alive neighbors -> survives
    assertEquals(ALIVE, rule.getNextStates(grid)[1][1]);
  }

  @Test
  void aliveCellWith3Neighbors_survives() {
    CellState[][] grid = {
        {ALIVE, ALIVE, DEAD},
        {DEAD, ALIVE, DEAD},
        {DEAD, DEAD, ALIVE}
    };
    // (1,1) has 3 alive neighbors -> survives
    assertEquals(ALIVE, rule.getNextStates(grid)[1][1]);
  }

  @Test
  void aliveCellWith4Neighbors_dies() {
    CellState[][] grid = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, ALIVE, DEAD},
        {DEAD, DEAD, ALIVE}
    };
    // (1,1) has 4 alive neighbors -> dies
    assertEquals(DEAD, rule.getNextStates(grid)[1][1]);
  }

  @Test
  void deadCellWith3Neighbors_becomesAlive() {
    CellState[][] grid = {
        {ALIVE, ALIVE, DEAD},
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD}
    };
    // (1,1) is dead with 3 alive neighbors -> becomes alive
    assertEquals(ALIVE, rule.getNextStates(grid)[1][1]);
  }

  @Test
  void deadCellWith2Neighbors_staysDead() {
    CellState[][] grid = {
        {ALIVE, DEAD, DEAD},
        {DEAD, DEAD, DEAD},
        {DEAD, DEAD, ALIVE}
    };
    // (1,1) is dead with 2 alive neighbors -> stays dead
    assertEquals(DEAD, rule.getNextStates(grid)[1][1]);
  }
}
