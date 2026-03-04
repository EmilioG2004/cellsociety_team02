package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.FallingSandState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.ReadOnlyGrid;
import cellsociety.Model.Grid.RuleGrid;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.GridEdge.ToroidalEdge;

import java.util.Map;
import java.util.Random;

/** Falling Sand: metal blocks, sand falls (swaps with water), water flows down/left/right. */
public class FallingSandRule extends GameRule {

  public static final int EMPTY = 0;
  public static final int METAL = 1;
  public static final int SAND = 2;
  public static final int WATER = 3;

  private final Random rng = new Random();
  private final NeighborStrategy defaultStrategy;

  public FallingSandRule() {
    super(Map.of());
    this.defaultStrategy = null;
  }

  public FallingSandRule(Map<String, Double> parameters) {
    super(parameters);
    this.defaultStrategy = null;
  }

  public FallingSandRule(NeighborStrategy strategy, Map<String, Double> parameters) {
    super(parameters);
    this.defaultStrategy = strategy;
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    NeighborStrategy ns = (defaultStrategy != null) ? defaultStrategy : new MooreNeighborStrategy();
    return ns;
  }

  @Override
  public Grid getNextGrid(RuleGrid currentGrid) {
    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();
    Grid nextGrid = currentGrid.emptyCopy();

    // clear board to EMPTY
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        nextGrid.setCellState(r, c, FallingSandState.EMPTY);
      }
    }

    // place metal first
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cast(currentGrid.getCellState(r, c)) == FallingSandState.METAL) {
          nextGrid.setCellState(r, c, FallingSandState.METAL);
        }
      }
    }

    // place sand
    for (int r = rows - 1; r >= 0; r--) {
      for (int c = 0; c < cols; c++) {
        if (cast(currentGrid.getCellState(r, c)) == FallingSandState.SAND) {
          placeSand(currentGrid, nextGrid, r, c);
        }
      }
    }

    // place water
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cast(currentGrid.getCellState(r, c)) == FallingSandState.WATER) {
          placeWater(currentGrid, nextGrid, r, c);
        }
      }
    }

    return nextGrid;
  }

  private void placeSand(ReadOnlyGrid current, Grid next, int r, int c) {
    int rows = current.getRows();
    int nr = r + 1;

    if (nr >= rows) {
      if (isEmpty(next, r, c)) next.setCellState(r, c, FallingSandState.SAND);
      return;
    }

    FallingSandState below = cast(current.getCellState(nr, c));

    if (below == FallingSandState.EMPTY && isEmpty(next, nr, c)) {
      next.setCellState(nr, c, FallingSandState.SAND);
      return;
    }

    // sink through water: sand goes down, water stays behind if possible
    if (below == FallingSandState.WATER && isEmpty(next, nr, c)) {
      next.setCellState(nr, c, FallingSandState.SAND);
      if (isEmpty(next, r, c)) next.setCellState(r, c, FallingSandState.WATER);
      return;
    }

    if (isEmpty(next, r, c)) next.setCellState(r, c, FallingSandState.SAND);
  }

  private void placeWater(ReadOnlyGrid current, Grid next, int r, int c) {
    int rows = current.getRows();
    int cols = current.getCols();

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

    if (isEmpty(next, r, c)) next.setCellState(r, c, FallingSandState.WATER);
  }

  private boolean tryMoveWater(ReadOnlyGrid current, Grid next,
      int r, int c, int nr, int nc, int rows, int cols) {

    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) return false;

    if (cast(current.getCellState(nr, nc)) != FallingSandState.EMPTY) return false;
    if (!isEmpty(next, nr, nc)) return false;

    next.setCellState(nr, nc, FallingSandState.WATER);
    return true;
  }

  private boolean isEmpty(ReadOnlyGrid grid, int r, int c) {
    return cast(grid.getCellState(r, c)) == FallingSandState.EMPTY;
  }

  private FallingSandState cast(CellState state) {
    return (state instanceof FallingSandState fs) ? fs : FallingSandState.EMPTY;
  }

  @Override
  public CellState fromInt(int value) {
    return FallingSandState.fromInt(value);
  }
}