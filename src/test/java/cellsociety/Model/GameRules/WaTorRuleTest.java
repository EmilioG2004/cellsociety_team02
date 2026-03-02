package cellsociety.Model.GameRules;

import static cellsociety.Model.CellState.WaTor.WaTorCellState.FISH;
import static cellsociety.Model.CellState.WaTor.WaTorCellState.SHARK;
import static cellsociety.Model.CellState.WaTor.WaTorCellState.WATER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WaTorRuleTest {

  private WaTorRule rule;

  @BeforeEach
  void setUp() {
    rule = new WaTorRule();
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

  private static int count(CellState[][] grid, CellState state) {
    int n = 0;
    for (CellState[] row : grid) {
      for (CellState cell : row) {
        if (state.equals(cell)) n++;
      }
    }
    return n;
  }

  @Test
  void fromInt_zero_returnsWater() {
    assertEquals(WATER, rule.fromInt(0));
  }

  @Test
  void fromInt_one_returnsFish() {
    assertEquals(FISH, rule.fromInt(1));
  }

  @Test
  void fromInt_two_returnsShark() {
    assertEquals(SHARK, rule.fromInt(2));
  }

  @Test
  void fromInt_invalid_throws() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> rule.fromInt(5));
    assertTrue(thrown.getMessage().contains("Invalid"));
  }

  @Test
  void getNextGrid_allWater_remainsAllWater() {
    CellState[][] grid = {
        {WATER, WATER, WATER},
        {WATER, WATER, WATER},
        {WATER, WATER, WATER}
    };
    CellState[][] n = next(rule, grid);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals(WATER, n[r][c]);
      }
    }
  }

  @Test
  void getNextGrid_sharkAdjacentToFish_fishGetsEaten() {
    CellState[][] grid = {
        {SHARK, FISH, FISH},
        {FISH, FISH, WATER},
        {FISH, WATER, WATER}
    };
    int fishBefore  = count(grid, FISH);
    int sharkBefore = count(grid, SHARK);
    CellState[][] n = next(rule, grid);
    assertEquals(sharkBefore, count(n, SHARK), "Shark count unchanged");
    assertEquals(fishBefore - 1, count(n, FISH), "One fish was eaten");
    assertEquals(WATER, n[0][0], "Shark left original cell");
  }

  @Test
  void getNextGrid_fishWithOneEmptyNeighbor_movesThere() {
    // 2x1 with wrapping: fish at (0,0) has water at (1,0) as neighbor
    CellState[][] grid = {
        {FISH},
        {WATER}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(1, count(n, FISH), "One fish moved");
    assertEquals(WATER, n[0][0], "Fish left original cell");
    assertEquals(FISH, n[1][0], "Fish moved to only empty neighbor");
  }

  @Test
  void getNextGrid_sharkWithStartEnergyOne_diesAfterOneStep() {
    rule = new WaTorRule(Map.of("sharkStartEnergy", 1.0));
    CellState sharkWithLowEnergy = rule.fromInt(2);
    CellState[][] grid = {
        {WATER, WATER, WATER},
        {WATER, sharkWithLowEnergy, WATER},
        {WATER, WATER, WATER}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(WATER, n[1][1], "Shark at 1 energy loses 1 and dies");
  }

  @Test
  void getNextGrid_fishSurroundedBySharks_getsEaten() {
    CellState[][] grid = {
        {WATER, SHARK, WATER},
        {SHARK, FISH,  SHARK},
        {WATER, SHARK, WATER}
    };
    CellState[][] n = next(rule, grid);
    assertEquals(SHARK, n[1][1], "Fish with no water neighbors does not move");
  }
}
