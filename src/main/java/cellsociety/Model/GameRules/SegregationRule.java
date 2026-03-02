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
  private static final int NO_OCCUPIED_NEIGHBORS = 0;

  public SegregationRule() {
    super(Map.of());
  }

  public SegregationRule(Map<String, Double> parameters) {
    super(parameters);
    validateParameters(parameters);
  }

  public SegregationRule(NeighborStrategy strategy, Map<String, Double> parameters) {
    super(parameters);
    validateParameters(parameters);
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new MooreNeighborStrategy();
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
    Grid nextGrid = currentGrid.deepCopy();

    double threshold = !parameters.containsKey(PARAM_SATISFACTION_THRESHOLD)
        ? DEFAULT_THRESHOLD
        : parameters.get(PARAM_SATISFACTION_THRESHOLD);

    List<Position> emptyCells = new ArrayList<>();
    List<Mover> movers = new ArrayList<>();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState state = currentGrid.getCellState(r, c);

        if (state == SegregationState.EMPTY) {
          emptyCells.add(new Position(r, c));
          continue;
        }
        if (!isSatisfied(currentGrid, r, c, state, threshold)) {
          movers.add(new Mover(r, c, state));
        }
      }
    }

    for (Mover m : movers) {
      nextGrid.setCellState(m.r, m.c, SegregationState.EMPTY);
      emptyCells.add(new Position(m.r, m.c));
    }

    // shuffle empties and relocate movers
    Collections.shuffle(emptyCells);

    for (int i = 0; i < movers.size(); i++) {
      Mover m = movers.get(i);
      Position destination = emptyCells.get(i);
      nextGrid.setCellState(destination.row, destination.col, m.state);
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

  private record Position(int row, int col) {}

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
  public CellState fromInt(int value) {
    return SegregationState.fromInt(value);
  }
}