package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;

import static cellsociety.Model.CellState.PercolotionState.CLOSED;
import static cellsociety.Model.CellState.PercolotionState.FULL;
import static cellsociety.Model.CellState.PercolotionState.OPEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for PercolationRule. Organized by ZOMBIES acronym.
 * CLOSED stays CLOSED; OPEN becomes FULL when full neighbors >= threshold; FULL stays FULL.
 */
class PercolationRuleTest {

  private PercolationRule rule;
  private PercolationRule ruleThresholdTwo;

  @BeforeEach
  void setUp() {
    rule = new PercolationRule();
    ruleThresholdTwo = new PercolationRule(Map.of("threshold", 2.0));
  }

  // ---------- Z (Zero): empty/minimal ----------
  @Test
  void getNextStates_allClosed_remainsAllClosed() {
    CellState[][] grid = {
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = rule.getNextStates(grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(CLOSED, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_1x1Closed_remainsClosed() {
    CellState[][] grid = {{CLOSED}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(CLOSED, next[0][0]);
  }

  @Test
  void getNextStates_1x1Open_noFullNeighbors_staysOpen() {
    CellState[][] grid = {{OPEN}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(OPEN, next[0][0]);
  }

  @Test
  void getNextStates_1x1Full_remainsFull() {
    CellState[][] grid = {{FULL}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[0][0]);
  }

  // ---------- O (One): single element / one neighbor ----------
  @Test
  void getNextStates_openWithOneFullNeighbor_defaultThreshold_becomesFull() {
    CellState[][] grid = {
        {FULL, OPEN, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[0][0]);
    assertEquals(FULL, next[0][1], "OPEN next to FULL becomes FULL (threshold 1)");
  }

  @Test
  void getNextStates_openWithNoFullNeighbors_staysOpen() {
    CellState[][] grid = {
        {OPEN, OPEN, OPEN},
        {OPEN, OPEN, OPEN},
        {OPEN, OPEN, OPEN}
    };
    CellState[][] next = rule.getNextStates(grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(OPEN, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_closedWithFullNeighbor_staysClosed() {
    CellState[][] grid = {
        {FULL, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[0][0]);
    assertEquals(CLOSED, next[0][1]);
  }

  // ---------- M (Many): threshold, multiple full neighbors ----------
  @Test
  void getNextStates_openWithOneFullNeighbor_thresholdTwo_staysOpen() {
    CellState[][] grid = {
        {FULL, OPEN, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = ruleThresholdTwo.getNextStates(grid);
    assertEquals(FULL, next[0][0]);
    assertEquals(OPEN, next[0][1], "OPEN needs 2 FULL neighbors when threshold=2");
  }

  @Test
  void getNextStates_openWithTwoFullNeighbors_thresholdTwo_becomesFull() {
    CellState[][] grid = {
        {FULL, OPEN, FULL},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = ruleThresholdTwo.getNextStates(grid);
    assertEquals(FULL, next[0][0]);
    assertEquals(FULL, next[0][1]);
    assertEquals(FULL, next[0][2]);
  }

  @Test
  void getNextStates_allFull_remainsAllFull() {
    CellState[][] grid = {
        {FULL, FULL, FULL},
        {FULL, FULL, FULL},
        {FULL, FULL, FULL}
    };
    CellState[][] next = rule.getNextStates(grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(FULL, next[r][c]);
      }
    }
  }

  @Test
  void getNextStates_centerOpenSurroundedByFull_becomesFull() {
    CellState[][] grid = {
        {FULL, FULL, FULL},
        {FULL, OPEN, FULL},
        {FULL, FULL, FULL}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[1][1]);
  }

  // ---------- B (Boundary): edges, corners ----------
  @Test
  void getNextStates_cornerOpenWithFullNeighbor_becomesFull() {
    CellState[][] grid = {
        {OPEN, FULL, CLOSED},
        {FULL, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[0][0]);
  }

  @Test
  void getNextStates_edgeOpenWithFullNeighbor_becomesFull() {
    CellState[][] grid = {
        {CLOSED, FULL, CLOSED},
        {OPEN, OPEN, OPEN},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[1][0]);
    assertEquals(FULL, next[1][1]);
    assertEquals(FULL, next[1][2]);
  }

  @Test
  void getNextStates_singleRow_openBetweenTwoFull_becomesFull() {
    CellState[][] grid = {{FULL, OPEN, FULL}};
    CellState[][] next = rule.getNextStates(grid);
    assertEquals(FULL, next[0][1]);
  }

  // ---------- I (Interface): getStateColors, fromInt, getNextStateInCycle ----------
  @Test
  void getStateColors_containsAllThreeStates() {
    Map<CellState, String> colors = rule.getStateColors();
    assertNotNull(colors);
    assertEquals(3, colors.size());
    assertEquals("BLACK", colors.get(CLOSED));
    assertEquals("WHITE", colors.get(OPEN));
    assertEquals("BLUE", colors.get(FULL));
  }

  @Test
  void fromInt_zeroOneTwo_returnsClosedOpenFull() {
    assertEquals(CLOSED, rule.fromInt(0));
    assertEquals(OPEN, rule.fromInt(1));
    assertEquals(FULL, rule.fromInt(2));
  }

  @Test
  void getNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getNeighborStrategy());
  }

  @Test
  void getNextStateInCycle_closed_returnsOpen() {
    assertEquals(OPEN, rule.getNextStateInCycle(CLOSED));
  }

  @Test
  void getNextStateInCycle_open_returnsFull() {
    assertEquals(FULL, rule.getNextStateInCycle(OPEN));
  }

  @Test
  void getNextStateInCycle_full_returnsClosed() {
    assertEquals(CLOSED, rule.getNextStateInCycle(FULL));
  }

  @Test
  void getNextStates_doesNotMutateInput() {
    CellState[][] input = {
        {CLOSED, OPEN, FULL},
        {OPEN, OPEN, OPEN},
        {FULL, CLOSED, CLOSED}
    };
    CellState[][] copy = {
        {CLOSED, OPEN, FULL},
        {OPEN, OPEN, OPEN},
        {FULL, CLOSED, CLOSED}
    };
    rule.getNextStates(input);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(copy[r][c], input[r][c]);
      }
    }
  }

  // ---------- E (Exceptional): invalid fromInt ----------
  @Test
  void fromInt_invalidValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(5));
  }

  @Test
  void fromInt_negativeValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(-1));
  }

  @Test
  void constructor_emptyParameters_usesDefaultThreshold() {
    PercolationRule ruleEmpty = new PercolationRule(Map.<String, Double>of());
    CellState[][] grid = {
        {FULL, OPEN, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] next = ruleEmpty.getNextStates(grid);
    assertEquals(FULL, next[0][1], "Default threshold 1: OPEN with 1 FULL neighbor becomes FULL");
  }
}
