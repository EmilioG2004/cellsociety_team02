package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SpreadingOfFireState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.RuleGrid;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.Map;

/**
 * Spreading of Fire: TREE catches from BURNING (probCatch), BURNING to EMPTY,
 * EMPTY to TREE (probTree). Von Neumann neighbors.
 *
 * @author Michael Lei
 */
public class SpreadingOfFireRule extends GameRule {

  private static final double DEFAULT_PROB_CATCH = 0.15;
  private static final double DEFAULT_PROB_TREE = 0.05;
  private static final String PARAM_PROB_CATCH = "probCatch";
  private static final String PARAM_PROB_TREE = "probTree";
  private static final double PROBABILITY_MIN = 0.0;
  private static final double PROBABILITY_MAX = 1.0;
  private static final int MIN_BURNING_NEIGHBORS_TO_CATCH = 1;

  public SpreadingOfFireRule() {
    super(Map.of());
  }

  public SpreadingOfFireRule(Map<String, Double> parameters) {
    super(parameters);
    validateParameters(parameters);
  }

  private static void validateParameters(Map<String, Double> parameters) {
    if (parameters.containsKey(PARAM_PROB_CATCH) && (parameters.get(PARAM_PROB_CATCH) < PROBABILITY_MIN || parameters.get(PARAM_PROB_CATCH) > PROBABILITY_MAX)) {
      throw new IllegalArgumentException("probCatch must be between 0 and 1");
    }
    if (parameters.containsKey(PARAM_PROB_TREE) && (parameters.get(PARAM_PROB_TREE) < PROBABILITY_MIN || parameters.get(PARAM_PROB_TREE) > PROBABILITY_MAX)) {
      throw new IllegalArgumentException("probTree must be between 0 and 1");
    }
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new VonNeumannNeighborStrategy();
  }

  @Override
  public Grid getNextGrid(RuleGrid currentGrid) {
    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();
    double probCatch = parameters.containsKey(PARAM_PROB_CATCH) ? parameters.get(PARAM_PROB_CATCH) : DEFAULT_PROB_CATCH;
    double probTree = parameters.containsKey(PARAM_PROB_TREE) ? parameters.get(PARAM_PROB_TREE) : DEFAULT_PROB_TREE;
    Grid next = currentGrid.emptyCopy();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState currentState = currentGrid.getCellState(r, c);
        if (currentState == SpreadingOfFireState.BURNING) {
          next.setCellState(r, c, SpreadingOfFireState.EMPTY);
        } else if (currentState == SpreadingOfFireState.TREE) {
          int burningNeighbors = currentGrid.countNeighborsWithState(r, c, SpreadingOfFireState.BURNING);
          boolean catchFire = burningNeighbors >= MIN_BURNING_NEIGHBORS_TO_CATCH && Math.random() < probCatch;
          next.setCellState(r, c, catchFire ? SpreadingOfFireState.BURNING : SpreadingOfFireState.TREE);
        } else {
          next.setCellState(r, c, Math.random() < probTree ? SpreadingOfFireState.TREE : SpreadingOfFireState.EMPTY);
        }
      }
    }
    return next;
  }

  @Override
  public CellState fromInt(int value) {
    return SpreadingOfFireState.fromInt(value);
  }
}
