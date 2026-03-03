package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.PercolotionState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.NeighborStrategy.MooreNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.Map;

/**
 * Percolation: OPEN cells become FULL when they have enough FULL neighbors (Moore).
 * Optional param: threshold (default 1).
 *
 * @author Michael Lei
 */
public class PercolationRule extends GameRule {

  private static final int DEFAULT_THRESHOLD = 1;
  private static final String PARAM_THRESHOLD = "threshold";

  public PercolationRule() {
    super(Map.of());
  }

  public PercolationRule(Map<String, Double> parameters) {
    super(parameters);
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new MooreNeighborStrategy();
  }

  @Override
  public Grid getNextGrid(Grid currentGrid) {
    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();
    int threshold = parameters.containsKey(PARAM_THRESHOLD)
        ? parameters.get(PARAM_THRESHOLD).intValue()
        : DEFAULT_THRESHOLD;
    Grid next = currentGrid.emptyCopy();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState currentState = currentGrid.getCellState(r, c);
        if (currentState == PercolotionState.CLOSED) {
          next.setCellState(r, c, PercolotionState.CLOSED);
        } else if (currentState == PercolotionState.OPEN) {
          int fullNeighbors = currentGrid.countNeighborsWithState(r, c, PercolotionState.FULL);
          next.setCellState(r, c, fullNeighbors >= threshold ? PercolotionState.FULL : PercolotionState.OPEN);
        } else {
          next.setCellState(r, c, PercolotionState.FULL);
        }
      }
    }
    return next;
  }

  @Override
  public CellState fromInt(int value) {
    return PercolotionState.fromInt(value);
  }
}
