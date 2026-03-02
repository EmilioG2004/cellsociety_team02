package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;

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
 */
class PercolationRuleTest {

  private PercolationRule rule;
  private PercolationRule ruleThresholdTwo;

  @BeforeEach
  void setUp() {
    rule = new PercolationRule();
    ruleThresholdTwo = new PercolationRule(Map.of("threshold", 2.0));
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
  void getNextGrid_allClosed_remainsAllClosed() {
    CellState[][] grid = {
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(rule, grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(CLOSED, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_1x1Closed_remainsClosed() {
    CellState[][] grid = {{CLOSED}};
    assertEquals(CLOSED, next(rule, grid)[0][0]);
  }

  @Test
  void getNextGrid_1x1Open_noFullNeighbors_staysOpen() {
    CellState[][] grid = {{OPEN}};
    assertEquals(OPEN, next(rule, grid)[0][0]);
  }

  @Test
  void getNextGrid_1x1Full_remainsFull() {
    CellState[][] grid = {{FULL}};
    assertEquals(FULL, next(rule, grid)[0][0]);
  }

  // ---------- O (One): single element / one neighbor ----------
  @Test
  void getNextGrid_openWithOneFullNeighbor_defaultThreshold_becomesFull() {
    CellState[][] grid = {
        {FULL, OPEN, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(FULL, n[0][0]);
    assertEquals(FULL, n[0][1], "OPEN next to FULL becomes FULL (threshold 1)");
  }

  @Test
  void getNextGrid_openWithNoFullNeighbors_staysOpen() {
    CellState[][] grid = {
        {OPEN, OPEN, OPEN},
        {OPEN, OPEN, OPEN},
        {OPEN, OPEN, OPEN}
    };
    CellState[][] n = next(rule, grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(OPEN, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_closedWithFullNeighbor_staysClosed() {
    CellState[][] grid = {
        {FULL, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(FULL, n[0][0]);
    assertEquals(CLOSED, n[0][1]);
  }

  // ---------- M (Many): threshold, multiple full neighbors ----------
  @Test
  void getNextGrid_openWithOneFullNeighbor_thresholdTwo_staysOpen() {
    CellState[][] grid = {
        {FULL, OPEN, CLOSED},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(ruleThresholdTwo, grid);
    assertEquals(FULL, n[0][0]);
    assertEquals(OPEN, n[0][1], "OPEN needs 2 FULL neighbors when threshold=2");
  }

  @Test
  void getNextGrid_openWithTwoFullNeighbors_thresholdTwo_becomesFull() {
    CellState[][] grid = {
        {FULL, OPEN, FULL},
        {CLOSED, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(ruleThresholdTwo, grid);
    assertEquals(FULL, n[0][0]);
    assertEquals(FULL, n[0][1]);
    assertEquals(FULL, n[0][2]);
  }

  @Test
  void getNextGrid_allFull_remainsAllFull() {
    CellState[][] grid = {
        {FULL, FULL, FULL},
        {FULL, FULL, FULL},
        {FULL, FULL, FULL}
    };
    CellState[][] n = next(rule, grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(FULL, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_centerOpenSurroundedByFull_becomesFull() {
    CellState[][] grid = {
        {FULL, FULL, FULL},
        {FULL, OPEN, FULL},
        {FULL, FULL, FULL}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(FULL, n[1][1]);
  }

  // ---------- B (Boundary): edges, corners ----------
  @Test
  void getNextGrid_cornerOpenWithFullNeighbor_becomesFull() {
    CellState[][] grid = {
        {OPEN, FULL, CLOSED},
        {FULL, CLOSED, CLOSED},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(FULL, n[0][0]);
  }

  @Test
  void getNextGrid_edgeOpenWithFullNeighbor_becomesFull() {
    CellState[][] grid = {
        {CLOSED, FULL, CLOSED},
        {OPEN, OPEN, OPEN},
        {CLOSED, CLOSED, CLOSED}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(FULL, n[1][0]);
    assertEquals(FULL, n[1][1]);
    assertEquals(FULL, n[1][2]);
  }

  @Test
  void getNextGrid_singleRow_openBetweenTwoFull_becomesFull() {
    CellState[][] grid = {{FULL, OPEN, FULL}};
    CellState[][] n = next(rule, grid);
    assertEquals(FULL, n[0][1]);
  }

  // ---------- I (Interface): fromInt, getDefaultNeighborStrategy ----------
  @Test
  void fromInt_zeroOneTwo_returnsClosedOpenFull() {
    assertEquals(CLOSED, rule.fromInt(0));
    assertEquals(OPEN, rule.fromInt(1));
    assertEquals(FULL, rule.fromInt(2));
  }

  @Test
  void getDefaultNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getDefaultNeighborStrategy());
  }

  @Test
  void getNextGrid_doesNotMutateInput() {
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
    next(rule, input);
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
    CellState[][] n = next(ruleEmpty, grid);
    assertEquals(FULL, n[0][1], "Default threshold 1: OPEN with 1 FULL neighbor becomes FULL");
  }
}
