package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SegregationState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Schelling Segregation: unhappy agents move to random EMPTY cells. */
public class SegregationRule extends GameRule {

  private static final double DEFAULT_THRESHOLD = 0.3;
  private static final String PARAM_SATISFACTION_THRESHOLD = "satisfactionThreshold";
  private static final double THRESHOLD_MIN = 0.0;
  private static final double THRESHOLD_MAX = 1.0;

  public SegregationRule(Map<String, Double> parameters) {
    super(parameters);
    validateParameters(this.parameters);
  }

  public SegregationRule() {
    this(Map.of());
  }

  private static void validateParameters(Map<String, Double> parameters) {
    if (parameters == null) return;
    if (!parameters.containsKey(PARAM_SATISFACTION_THRESHOLD)) return;

    double t = parameters.get(PARAM_SATISFACTION_THRESHOLD);
    if (t < THRESHOLD_MIN || t > THRESHOLD_MAX) {
      throw new IllegalArgumentException("satisfactionThreshold must be between 0 and 1");
    }
  }

  @Override
  public Grid getNextGrid(Grid currentGrid) {
    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();

    double threshold = parameters.containsKey(PARAM_SATISFACTION_THRESHOLD)
        ? parameters.get(PARAM_SATISFACTION_THRESHOLD)
        : DEFAULT_THRESHOLD;

    Grid nextGrid = currentGrid.deepCopy();

    List<int[]> emptyCells = new ArrayList<>();
    List<Mover> movers = new ArrayList<>();

    // identify empties and unhappy movers
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState s = currentGrid.getCellState(r, c);

        if (s == SegregationState.EMPTY) {
          emptyCells.add(new int[]{r, c});
        } else {
          if (!isSatisfied(currentGrid, r, c, s, threshold)) {
            movers.add(new Mover(r, c, s));
          }
        }
      }
    }

    // clear movers original positions
    for (Mover m : movers) {
      nextGrid.setCellState(m.r, m.c, SegregationState.EMPTY);
      emptyCells.add(new int[]{m.r, m.c});
    }

    // shuffle empties and relocate movers
    Collections.shuffle(emptyCells);
    int moves = Math.min(movers.size(), emptyCells.size());

    for (int i = 0; i < moves; i++) {
      Mover m = movers.get(i);
      int[] dest = emptyCells.get(i);
      nextGrid.setCellState(dest[0], dest[1], m.state);
    }

    // if movers > emptyCells, put the extras back
    for (int i = moves; i < movers.size(); i++) {
      Mover m = movers.get(i);
      nextGrid.setCellState(m.r, m.c, m.state);
    }

    return nextGrid;
  }

  private boolean isSatisfied(Grid grid, int r, int c, CellState self, double threshold) {
    List<CellState> neighbors = grid.getNeighbors(r, c);

    int occupied = 0;
    int same = 0;

    for (CellState n : neighbors) {
      if (n == SegregationState.EMPTY) continue;
      occupied++;
      if (n == self) same++;
    }

    // If no occupied neighbors, treat as satisfied
    if (occupied == 0) return true;

    double fracSame = (double) same / occupied;
    return fracSame >= threshold;
  }

  private static class Mover {
    final int r;
    final int c;
    final CellState state;

    Mover(int r, int c, CellState state) {
      this.r = r;
      this.c = c;
      this.state = state;
    }
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new MooreNeighborStrategy();
  }

  @Override
  public CellState fromInt(int value) {
    return SegregationState.fromInt(value);
  }
}