package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;

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
 * All rule variants (default, deterministic params) are created in setUp.
 */
class SpreadingOfFireRuleTest {

  /** Default rule (probCatch/probTree from defaults). */
  private SpreadingOfFireRule rule;
  /** probCatch=1, probTree=0: tree next to fire always catches; empty never grows. */
  private SpreadingOfFireRule ruleCatchOneNoGrow;
  /** probCatch=0, probTree=1: tree never catches; empty always becomes tree. */
  private SpreadingOfFireRule ruleNoCatchGrowAll;
  /** probCatch=0, probTree=0: fully deterministic (burning→empty, tree→tree, empty→empty). */
  private SpreadingOfFireRule ruleNoCatchNoGrow;
  /** Empty parameters map (uses defaults). */
  private SpreadingOfFireRule ruleEmptyParams;

  @BeforeEach
  void setUp() {
    rule = new SpreadingOfFireRule();
    ruleCatchOneNoGrow = new SpreadingOfFireRule(Map.of("probCatch", 1.0, "probTree", 0.0));
    ruleNoCatchGrowAll = new SpreadingOfFireRule(Map.of("probCatch", 0.0, "probTree", 1.0));
    ruleNoCatchNoGrow = new SpreadingOfFireRule(Map.of("probCatch", 0.0, "probTree", 0.0));
    ruleEmptyParams = new SpreadingOfFireRule(Map.<String, Double>of());
  }

  // ---------- Z (Zero): empty/minimal ----------
  @Test
  void getNextStates_allEmpty_probTreeZero_remainsAllEmpty() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleNoCatchNoGrow.getNextStates(allEmpty);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(EMPTY, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_allEmpty_probTreeOne_allBecomeTree() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleNoCatchGrowAll.getNextStates(allEmpty);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(TREE, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_1x1Empty_probTreeZero_remainsEmpty() {
    CellState[][] single = {{EMPTY}};
    CellState[][] next = ruleNoCatchNoGrow.getNextStates(single);
    assertEquals(EMPTY, next[0][0]);
  }

  // ---------- O (One): single element ----------
  @Test
  void getNextStates_singleBurning_becomesEmpty() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, BURNING, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(EMPTY, next[1][1]);
  }

  @Test
  void getNextStates_1x1Burning_becomesEmpty() {
    CellState[][] grid = {{BURNING}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(EMPTY, next[0][0]);
  }

  @Test
  void getNextStates_singleTreeNoFire_staysTree() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleNoCatchNoGrow.getNextStates(grid);
    assertEquals(TREE, next[1][1]);
  }

  @Test
  void getNextStates_singleEmpty_probTreeOne_becomesTree() {
    CellState[][] grid = {{EMPTY}};
    CellState[][] next = ruleNoCatchGrowAll.getNextStates(grid);
    assertEquals(TREE, next[0][0]);
  }

  // ---------- M (Many): multiple elements ----------
  @Test
  void getNextStates_multipleBurning_allBecomeEmpty() {
    CellState[][] grid = {
        {BURNING, TREE, BURNING},
        {TREE, BURNING, TREE},
        {BURNING, TREE, BURNING}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(EMPTY, next[0][0]);
    assertEquals(EMPTY, next[0][2]);
    assertEquals(EMPTY, next[1][1]);
    assertEquals(EMPTY, next[2][0]);
    assertEquals(EMPTY, next[2][2]);
  }

  @Test
  void getNextStates_treeBlockWithNoFire_allStayTree() {
    CellState[][] grid = {
        {EMPTY, TREE, EMPTY},
        {TREE, TREE, TREE},
        {EMPTY, TREE, EMPTY}
    };
    CellState[][] next = ruleNoCatchNoGrow.getNextStates(grid);
    assertEquals(TREE, next[0][1]);
    assertEquals(TREE, next[1][0]);
    assertEquals(TREE, next[1][1]);
    assertEquals(TREE, next[1][2]);
    assertEquals(TREE, next[2][1]);
  }

  @Test
  void getNextStates_treeNextToBurning_catchOne_becomesBurning() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {BURNING, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleCatchOneNoGrow.getNextStates(grid);
    assertEquals(EMPTY, next[0][1]);
    assertEquals(EMPTY, next[1][0]);
    assertEquals(BURNING, next[1][1]);
  }

  @Test
  void getNextStates_treeNextToBurning_noCatch_staysTree() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {BURNING, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleNoCatchNoGrow.getNextStates(grid);
    assertEquals(TREE, next[1][1]);
  }

  @Test
  void getNextStates_2x2Empty_probTreeOne_allBecomeTree() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY},
        {EMPTY, EMPTY}
    };
    CellState[][] next = ruleNoCatchGrowAll.getNextStates(allEmpty);
    assertEquals(TREE, next[0][0]);
    assertEquals(TREE, next[0][1]);
    assertEquals(TREE, next[1][0]);
    assertEquals(TREE, next[1][1]);
  }

  // ---------- B (Boundaries): edges, corners, cardinal vs diagonal ----------
  @Test
  void getNextStates_cardinalNeighbors_diagonalBurningOnly_centerTreeStaysTree() {
    CellState[][] grid = {
        {BURNING, EMPTY, BURNING},
        {EMPTY, TREE, EMPTY},
        {BURNING, EMPTY, BURNING}
    };
    CellState[][] next = ruleCatchOneNoGrow.getNextStates(grid);
    assertEquals(EMPTY, next[0][0]);
    assertEquals(EMPTY, next[0][2]);
    assertEquals(EMPTY, next[2][0]);
    assertEquals(EMPTY, next[2][2]);
    assertEquals(TREE, next[1][1]);
  }

  @Test
  void getNextStates_treeWithOneCardinalBurning_catchOne_becomesBurning() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleCatchOneNoGrow.getNextStates(grid);
    assertEquals(BURNING, next[1][1]);
  }

  @Test
  void getNextStates_singleRow_burningBecomesEmpty() {
    CellState[][] grid = {{BURNING, TREE, BURNING}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(EMPTY, next[0][0]);
    assertEquals(EMPTY, next[0][2]);
  }

  @Test
  void getNextStates_singleColumn_centerBurningBecomesEmpty() {
    CellState[][] grid = {{TREE}, {BURNING}, {TREE}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(EMPTY, next[1][0]);
  }

  @Test
  void getNextStates_cornerBurning_becomesEmpty() {
    CellState[][] grid = {
        {BURNING, TREE, EMPTY},
        {TREE, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(EMPTY, next[0][0]);
  }

  @Test
  void getNextStates_edgeTreeWithBurningNeighbor_catchOne_catches() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] next = ruleCatchOneNoGrow.getNextStates(grid);
    assertEquals(BURNING, next[1][1]);
  }

  // ---------- I (Interface): getStateColors, fromInt, getNeighborStrategy ----------
  @Test
  void getStateColors_containsAllThreeStates() {
    Map<CellState, String> colors = rule.getStateColors();
    assertNotNull(colors);
    assertEquals(3, colors.size());
    assertEquals("WHITE", colors.get(EMPTY));
    assertEquals("GREEN", colors.get(TREE));
    assertEquals("RED", colors.get(BURNING));
  }

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
  void getNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getNeighborStrategy());
  }

  @Test
  void getNextStates_doesNotMutateInput() {
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
    rule.getNextStates(input);
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
    assertEquals(EMPTY, ruleEmptyParams.getNextStates(grid)[0][0]);
  }

  // ---------- S (Simple): typical flows ----------
  @Test
  void burningAlwaysBecomesEmpty() {
    CellState[][] grid = {{BURNING}};
    assertEquals(EMPTY, ruleNoCatchNoGrow.getNextStates(grid)[0][0]);
  }

  @Test
  void treeWithNoBurningNeighbors_staysTree() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    assertEquals(TREE, ruleNoCatchNoGrow.getNextStates(grid)[1][1]);
  }

  @Test
  void treeWithBurningNeighbor_catchOne_catches() {
    CellState[][] grid = {
        {EMPTY, BURNING, EMPTY},
        {EMPTY, TREE, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    assertEquals(BURNING, ruleCatchOneNoGrow.getNextStates(grid)[1][1]);
  }

  @Test
  void emptyWithProbTreeOne_becomesTree() {
    CellState[][] grid = {{EMPTY}};
    assertEquals(TREE, ruleNoCatchGrowAll.getNextStates(grid)[0][0]);
  }

  @Test
  void emptyWithProbTreeZero_staysEmpty() {
    CellState[][] grid = {{EMPTY}};
    assertEquals(EMPTY, ruleNoCatchNoGrow.getNextStates(grid)[0][0]);
  }
}
