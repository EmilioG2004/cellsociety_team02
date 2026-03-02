package cellsociety.Model.GameRules;

import static cellsociety.Model.CellState.SegregationState.EMPTY;
import static cellsociety.Model.CellState.SegregationState.GROUP_A;
import static cellsociety.Model.CellState.SegregationState.GROUP_B;
import static org.junit.jupiter.api.Assertions.*;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SegregationRuleTest {

  private SegregationRule ruleDefault;
  private SegregationRule ruleThresholdOne;
  private SegregationRule ruleThresholdZero;
  private SegregationRule ruleEmptyParams;

  @BeforeEach
  void setUp() {
    ruleDefault = new SegregationRule();
    ruleThresholdOne = new SegregationRule(Map.of("satisfactionThreshold", 1.0));
    ruleThresholdZero = new SegregationRule(Map.of("satisfactionThreshold", 0.0));
    ruleEmptyParams = new SegregationRule(Map.<String, Double>of());
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

  @Test
  void getNextGrid_allEmpty_staysAllEmpty() {
    CellState[][] allEmpty = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(ruleDefault, allEmpty);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(EMPTY, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_1x1Empty_staysEmpty() {
    CellState[][] single = {{EMPTY}};
    assertEquals(EMPTY, next(ruleDefault, single)[0][0]);
  }

  @Test
  void getNextGrid_1x1GroupA_noNeighbors_isSatisfied_stays() {
    CellState[][] grid = {{GROUP_A}};
    assertEquals(GROUP_A, next(ruleThresholdOne, grid)[0][0]);
  }

  @Test
  void getNextGrid_thresholdZero_noOneMoves_gridUnchanged() {
    CellState[][] grid = {
        {GROUP_A, GROUP_B, EMPTY},
        {GROUP_A, GROUP_B, EMPTY},
        {EMPTY,   EMPTY,   EMPTY}
    };
    CellState[][] copy = deepCopy(grid);
    CellState[][] n = next(ruleThresholdZero, grid);
    assertGridsEqual(copy, n);
  }

  @Test
  void getNextGrid_singleUnhappyAgent_movesToOnlyEmptySpot_moore() {
    CellState[][] grid = {
        {GROUP_A, GROUP_A, GROUP_A},
        {GROUP_A, GROUP_A, GROUP_A},
        {GROUP_A, GROUP_B, EMPTY}
    };
    CellState[][] n = next(ruleThresholdOne, grid);
    assertEquals(EMPTY, n[1][1]);
    assertEquals(GROUP_A, n[2][2]);
    assertSameCounts(grid, n);
  }

  @Test
  void getNextGrid_doesNotMutateInput() {
    CellState[][] input = {
        {GROUP_A, GROUP_B, EMPTY},
        {GROUP_A, GROUP_A, GROUP_B},
        {EMPTY,   GROUP_B, GROUP_A}
    };
    CellState[][] copy = deepCopy(input);
    next(ruleDefault, input);
    assertGridsEqual(copy, input);
  }

  @Test
  void constructor_thresholdAboveOne_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new SegregationRule(Map.of("satisfactionThreshold", 1.5)));
  }

  @Test
  void constructor_thresholdNegative_throws() {
    assertThrows(IllegalArgumentException.class,
        () -> new SegregationRule(Map.of("satisfactionThreshold", -0.1)));
  }

  @Test
  void constructor_emptyParameters_accepts() {
    assertNotNull(ruleEmptyParams);
  }

  private static CellState[][] deepCopy(CellState[][] grid) {
    CellState[][] copy = new CellState[grid.length][grid[0].length];
    for (int r = 0; r < grid.length; r++) {
      System.arraycopy(grid[r], 0, copy[r], 0, grid[0].length);
    }
    return copy;
  }

  private static void assertGridsEqual(CellState[][] a, CellState[][] b) {
    assertEquals(a.length, b.length);
    assertEquals(a[0].length, b[0].length);
    for (int r = 0; r < a.length; r++) {
      for (int c = 0; c < a[0].length; c++) {
        assertEquals(a[r][c], b[r][c], "Mismatch at (" + r + "," + c + ")");
      }
    }
  }

  private static void assertSameCounts(CellState[][] before, CellState[][] after) {
    assertEquals(count(before, EMPTY), count(after, EMPTY));
    assertEquals(count(before, GROUP_A), count(after, GROUP_A));
    assertEquals(count(before, GROUP_B), count(after, GROUP_B));
  }

  private static int count(CellState[][] grid, CellState s) {
    int total = 0;
    for (CellState[] row : grid) {
      for (CellState cell : row) {
        if (cell == s) total++;
      }
    }
    return total;
  }
}
