package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;

import static cellsociety.Model.CellState.SpreadingOfFireState.BURNING;
import static cellsociety.Model.CellState.SpreadingOfFireState.EMPTY;
import static cellsociety.Model.CellState.SpreadingOfFireState.TREE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for SpreadingOfFireRule. Organized by ZOMBIES acronym.
 */
class SpreadingOfFireRuleTest {

  private SpreadingOfFireRule rule;
  /** probCatch=1, probTree=0 */
  private SpreadingOfFireRule ruleCatchOneNoGrow;
  /** probCatch=0, probTree=1 */
  private SpreadingOfFireRule ruleNoCatchGrowAll;
  /** probCatch=0, probTree=0 */
  private SpreadingOfFireRule ruleNoCatchNoGrow;
  private SpreadingOfFireRule ruleEmptyParams;

  @BeforeEach
  void setUp() {
    rule = new SpreadingOfFireRule();
    ruleCatchOneNoGrow = new SpreadingOfFireRule(Map.of("probCatch", 1.0, "probTree", 0.0));
    ruleNoCatchGrowAll = new SpreadingOfFireRule(Map.of("probCatch", 0.0, "probTree", 1.0));
    ruleNoCatchNoGrow = new SpreadingOfFireRule(Map.of("probCatch", 0.0, "probTree", 0.0));
    ruleEmptyParams = new SpreadingOfFireRule(Map.<String, Double>of());
  }

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

  // ---------- Z (Zero): empty/minimal ----------
  @Test
  void getNextGrid_allEmpty_probTreeZero_remainsAllEmpty() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleNoCatchNoGrow, allEmpty);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(EMPTY, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_allEmpty_probTreeOne_allBecomeTree() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleNoCatchGrowAll, allEmpty);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(TREE, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_1x1Empty_probTreeZero_remainsEmpty() {
    CellState[][] single = {{EMPTY}};
    CellState[][] n = next(ruleNoCatchNoGrow, single);
    assertEquals(EMPTY, n[0][0]);
  }

  // ---------- O (One): single element ----------
  @Test
  void getNextGrid_singleBurning_becomesEmpty() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, BURNING, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[1][1]);
  }

  @Test
  void getNextGrid_1x1Burning_becomesEmpty() {
    CellState[][] grid = {{BURNING}};
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[0][0]);
  }

  @Test
  void getNextGrid_singleTreeNoFire_staysTree() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleNoCatchNoGrow, grid);
    assertEquals(TREE, n[1][1]);
  }

  @Test
  void getNextGrid_singleEmpty_probTreeOne_becomesTree() {
    CellState[][] grid = {{EMPTY}};
    CellState[][] n = next(ruleNoCatchGrowAll, grid);
    assertEquals(TREE, n[0][0]);
  }

  // ---------- M (Many): multiple elements ----------
  @Test
  void getNextGrid_multipleBurning_allBecomeEmpty() {
    CellState[][] grid = {
        {BURNING, TREE, BURNING},
        {TREE, BURNING, TREE},
        {BURNING, TREE, BURNING}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[0][0]);
    assertEquals(EMPTY, n[0][2]);
    assertEquals(EMPTY, n[1][1]);
    assertEquals(EMPTY, n[2][0]);
    assertEquals(EMPTY, n[2][2]);
  }

  @Test
  void getNextGrid_treeBlockWithNoFire_allStayTree() {
    CellState[][] grid = {
        {EMPTY, TREE, EMPTY},
        {TREE, TREE, TREE},
        {EMPTY, TREE, EMPTY}
    };
    CellState[][] n = next(ruleNoCatchNoGrow, grid);
    assertEquals(TREE, n[0][1]);
    assertEquals(TREE, n[1][0]);
    assertEquals(TREE, n[1][1]);
    assertEquals(TREE, n[1][2]);
    assertEquals(TREE, n[2][1]);
  }

  @Test
  void getNextGrid_treeNextToBurning_catchOne_becomesBurning() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {BURNING, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleCatchOneNoGrow, grid);
    assertEquals(EMPTY, n[0][1]);
    assertEquals(EMPTY, n[1][0]);
    assertEquals(BURNING, n[1][1]);
  }

  @Test
  void getNextGrid_treeNextToBurning_noCatch_staysTree() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {BURNING, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleNoCatchNoGrow, grid);
    assertEquals(TREE, n[1][1]);
  }

  @Test
  void getNextGrid_2x2Empty_probTreeOne_allBecomeTree() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY},
        {EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleNoCatchGrowAll, allEmpty);
    assertEquals(TREE, n[0][0]);
    assertEquals(TREE, n[0][1]);
    assertEquals(TREE, n[1][0]);
    assertEquals(TREE, n[1][1]);
  }

  // ---------- B (Boundaries): cardinal vs diagonal ----------
  @Test
  void getNextGrid_cardinalNeighbors_diagonalBurningOnly_centerTreeStaysTree() {
    CellState[][] grid = {
        {BURNING, EMPTY, BURNING},
        {EMPTY, TREE, EMPTY},
        {BURNING, EMPTY, BURNING}
    };
    CellState[][] n = next(ruleCatchOneNoGrow, grid);
    assertEquals(EMPTY, n[0][0]);
    assertEquals(EMPTY, n[0][2]);
    assertEquals(EMPTY, n[2][0]);
    assertEquals(EMPTY, n[2][2]);
    assertEquals(TREE, n[1][1]);
  }

  @Test
  void getNextGrid_treeWithOneVonNeumannBurning_catchOne_becomesBurning() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleCatchOneNoGrow, grid);
    assertEquals(BURNING, n[1][1]);
  }

  @Test
  void getNextGrid_singleRow_burningBecomesEmpty() {
    CellState[][] grid = {{BURNING, TREE, BURNING}};
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[0][0]);
    assertEquals(EMPTY, n[0][2]);
  }

  @Test
  void getNextGrid_singleColumn_centerBurningBecomesEmpty() {
    CellState[][] grid = {{TREE}, {BURNING}, {TREE}};
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[1][0]);
  }

  @Test
  void getNextGrid_cornerBurning_becomesEmpty() {
    CellState[][] grid = {
        {BURNING, TREE, EMPTY},
        {TREE, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[0][0]);
  }

  @Test
  void getNextGrid_edgeTreeWithBurningNeighbor_catchOne_catches() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleCatchOneNoGrow, grid);
    assertEquals(BURNING, n[1][1]);
  }

  // ---------- I (Interface): fromInt, getDefaultNeighborStrategy ----------
  @Test
  void fromInt_zero_returnsEmpty() {
    assertEquals(EMPTY, rule.fromInt(0));
  }

  @Test
  void fromInt_one_returnsTree() {
    assertEquals(TREE, rule.fromInt(1));
  }

  @Test
  void fromInt_two_returnsBurning() {
    assertEquals(BURNING, rule.fromInt(2));
  }

  @Test
  void getDefaultNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getDefaultNeighborStrategy());
  }

  @Test
  void getNextGrid_doesNotMutateInput() {
    CellState[][] input = {
        {EMPTY, BURNING, EMPTY},
        {TREE, TREE, TREE},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] copy = {
        {EMPTY, BURNING, EMPTY},
        {TREE, TREE, TREE},
        {EMPTY, EMPTY, EMPTY}
    };
    next(rule, input);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(copy[r][c], input[r][c]);
      }
    }
  }

  // ---------- E (Exceptional): invalid params, invalid fromInt ----------
  @Test
  void constructor_probCatchAboveOne_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new SpreadingOfFireRule(Map.of("probCatch", 1.5)));
  }

  @Test
  void constructor_probCatchNegative_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new SpreadingOfFireRule(Map.of("probCatch", -0.1)));
  }

  @Test
  void constructor_probTreeAboveOne_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new SpreadingOfFireRule(Map.of("probTree", 2.0)));
  }

  @Test
  void constructor_probTreeNegative_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new SpreadingOfFireRule(Map.of("probTree", -0.1)));
  }

  @Test
  void fromInt_invalidValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(5));
  }

  @Test
  void fromInt_negativeValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(-1));
  }

  @Test
  void constructor_emptyParameters_accepts() {
    assertNotNull(ruleEmptyParams);
    CellState[][] grid = {{BURNING}};
    assertEquals(EMPTY, next(ruleEmptyParams, grid)[0][0]);
  }
}
