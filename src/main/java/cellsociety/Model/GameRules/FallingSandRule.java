package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.FallingSandState;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.Map;
import java.util.Random;

/** Falling Sand: metal blocks, sand falls (swaps with water), water flows down/left/right. */
public class FallingSandRule extends GameRule {

  public static final int EMPTY = 0;
  public static final int METAL = 1;
  public static final int SAND = 2;
  public static final int WATER = 3;

  private final Random rng = new Random();

  public FallingSandRule() {
    super(new MooreNeighborStrategy());
  }

  public FallingSandRule(Map<String, Double> parameters) {
    super(new MooreNeighborStrategy(), parameters);
  }

  public FallingSandRule(NeighborStrategy strategy, Map<String, Double> parameters) {
    super(strategy, parameters);
  }
  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    int rows = currentStates.length;
    int cols = currentStates[0].length;

    // clear board
    CellState[][] nextStates = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        nextStates[r][c] = FallingSandState.EMPTY;
      }
    }

    // place metal first
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cast(currentStates[r][c]) == FallingSandState.METAL) {
          nextStates[r][c] = FallingSandState.METAL;
        }
      }
    }

    // place sand
    for (int r = rows - 1; r >= 0; r--) {
      for (int c = 0; c < cols; c++) {
        if (cast(currentStates[r][c]) == FallingSandState.SAND) {
          placeSand(currentStates, nextStates, r, c);
        }
      }
    }

    // place water
    for (int r = 0; r <= rows - 1; r++) {
      for (int c = 0; c < cols; c++) {
        if (cast(currentStates[r][c]) == FallingSandState.WATER) {
          placeWater(currentStates, nextStates, r, c);
        }
      }
    }

    return nextStates;
  }

  private void placeSand(CellState[][] current, CellState[][] next, int r, int c) {
    int rows = current.length;
    int nr = r + 1;

    if (nr >= rows) {
      if (isEmpty(next, r, c)) next[r][c] = FallingSandState.SAND;
      return;
    }

    FallingSandState below = cast(current[nr][c]);

    if (below == FallingSandState.EMPTY && isEmpty(next, nr, c)) {
      next[nr][c] = FallingSandState.SAND;
      return;
    }

    // sink through water: sand goes down, water stays behind if possible
    if (below == FallingSandState.WATER && isEmpty(next, nr, c)) {
      next[nr][c] = FallingSandState.SAND;
      if (isEmpty(next, r, c)) next[r][c] = FallingSandState.WATER;
      return;
    }

    if (isEmpty(next, r, c)) next[r][c] = FallingSandState.SAND;
  }

  private void placeWater(CellState[][] current, CellState[][] next, int r, int c) {
    int rows = current.length;
    int cols = current[0].length;

    if (tryMoveWater(current, next, r, c, r + 1, c, rows, cols)) return;

    // randomize left/right order
    boolean goLeftFirst = rng.nextBoolean();
    if (goLeftFirst) {
      if (tryMoveWater(current, next, r, c, r, c - 1, rows, cols)) return;
      if (tryMoveWater(current, next, r, c, r, c + 1, rows, cols)) return;
    } else {
      if (tryMoveWater(current, next, r, c, r, c + 1, rows, cols)) return;
      if (tryMoveWater(current, next, r, c, r, c - 1, rows, cols)) return;
    }

    if (isEmpty(next, r, c)) next[r][c] = FallingSandState.WATER;
  }

  private boolean tryMoveWater(CellState[][] current, CellState[][] next,
      int r, int c, int nr, int nc, int rows, int cols) {
    if (!inBounds(nr, nc, rows, cols)) return false;
    if (cast(current[nr][nc]) != FallingSandState.EMPTY) return false;
    if (!isEmpty(next, nr, nc)) return false;
    next[nr][nc] = FallingSandState.WATER;
    return true;
  }

  private boolean inBounds(int r, int c, int rows, int cols) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
  }

  private boolean isEmpty(CellState[][] grid, int r, int c) {
    return cast(grid[r][c]) == FallingSandState.EMPTY;
  }

  private FallingSandState cast(CellState state) {
    return (state instanceof FallingSandState fs) ? fs : FallingSandState.EMPTY;
  }

  @Override
  public Map<CellState, String> getStateColors() {
    return stateColorsFor(
        FallingSandState.EMPTY,
        FallingSandState.METAL,
        FallingSandState.SAND,
        FallingSandState.WATER
    );
  }

  @Override
  public CellState fromInt(int value) {
    return FallingSandState.fromInt(value);
  }

  @Override
  public CellState getNextStateInCycle(CellState current) {
    return FallingSandState.getNextStateInCycle(current);
  }

  @Override
  public int fromState(CellState state) {
    return FallingSandState.fromState(state);
  }
}
