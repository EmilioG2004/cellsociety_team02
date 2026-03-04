package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;

import static cellsociety.Model.CellState.Langton1984State.S0;
import static cellsociety.Model.CellState.Langton1984State.S1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Langton1984RuleTest {

  private Langton1984Rule rule;

  @BeforeEach
  void setUp() {
    rule = new Langton1984Rule();
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
  void getNextGrid_allS0_remainsAllS0() {
    CellState[][] grid = {
        {S0, S0, S0},
        {S0, S0, S0},
        {S0, S0, S0}
    };
    CellState[][] n = next(rule, grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(S0, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_1x1_unchanged() {
    CellState[][] grid = {{S0}};
    assertEquals(S0, next(rule, grid)[0][0]);
  }

  @Test
  void fromInt_zeroToSeven_returnsS0ThroughS7() {
    assertEquals(S0, rule.fromInt(0));
    assertEquals(S1, rule.fromInt(1));
    assertEquals(S0, rule.fromInt(0));
  }

  @Test
  void getDefaultNeighborStrategy_returnsVonNeumann() {
    assertNotNull(rule.getDefaultNeighborStrategy());
  }

  @Test
  void fromInt_invalidValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(8));
  }

  @Test
  void fromInt_negativeValue_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(-1));
  }

  @Test
  void constructor_withParameters_acceptsEmptyMap() {
    Langton1984Rule r = new Langton1984Rule(Map.of());
    assertNotNull(r.getDefaultNeighborStrategy());
    assertEquals(S0, r.fromInt(0));
  }
}
