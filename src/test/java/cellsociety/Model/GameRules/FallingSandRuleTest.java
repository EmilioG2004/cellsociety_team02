package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;

import static cellsociety.Model.CellState.FallingSandState.EMPTY;
import static cellsociety.Model.CellState.FallingSandState.METAL;
import static cellsociety.Model.CellState.FallingSandState.SAND;
import static cellsociety.Model.CellState.FallingSandState.WATER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FallingSandRuleTest {

  private FallingSandRule rule;

  @BeforeEach
  void setUp() {
    rule = new FallingSandRule();
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
  void getNextGrid_allEmpty_remainsAllEmpty() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(rule, grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(EMPTY, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_metal_staysInPlace() {
    CellState[][] grid = {
        {EMPTY, METAL, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(METAL, n[0][1]);
  }

  @Test
  void getNextGrid_sandFallsDown() {
    CellState[][] grid = {
        {SAND, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(EMPTY, n[0][0]);
    assertEquals(SAND, n[1][0]);
  }

  @Test
  void getNextGrid_sandStacksOnBottom() {
    CellState[][] grid = {
        {EMPTY, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY},
        {SAND, SAND, SAND}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(SAND, n[2][0]);
    assertEquals(SAND, n[2][1]);
    assertEquals(SAND, n[2][2]);
  }

  @Test
  void getNextGrid_sandSinksThroughWater() {
    CellState[][] grid = {
        {SAND, EMPTY, EMPTY},
        {WATER, EMPTY, EMPTY},
        {EMPTY, EMPTY, EMPTY}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(WATER, n[0][0], "Water moves up where sand was");
    assertEquals(SAND, n[1][0], "Sand sinks into water");
    assertEquals(EMPTY, n[2][0]);
  }

  @Test
  void fromInt_zeroOneTwoThree_returnsCorrectStates() {
    assertEquals(EMPTY, rule.fromInt(0));
    assertEquals(METAL, rule.fromInt(1));
    assertEquals(SAND, rule.fromInt(2));
    assertEquals(WATER, rule.fromInt(3));
  }

  @Test
  void getDefaultNeighborStrategy_returnsNonNull() {
    assertNotNull(rule.getDefaultNeighborStrategy());
  }

  @Test
  void fromInt_invalidValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(5));
  }

  @Test
  void constructor_withParameters_acceptsEmptyMap() {
    FallingSandRule r = new FallingSandRule(Map.of());
    assertNotNull(r.getDefaultNeighborStrategy());
  }
}
