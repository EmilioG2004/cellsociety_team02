package cellsociety.Model.GameRules;
import cellsociety.Model.Cell.BasicCell;
import cellsociety.Model.Cell.Cell;
import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SpreadingOfFireState;
import cellsociety.Model.NeighborStrategy.CardinalNeighborStrategy;

import java.util.List;
import java.util.Map;

/** Spreading of Fire: TREE catches from BURNING (probCatch), BURNING→EMPTY, EMPTY→TREE (probTree). Cardinal neighbors. Params: probCatch (0.15), probTree (0.05). */
public class SpreadingOfFireRule extends GameRule {

  private static final double DEFAULT_PROB_CATCH = 0.15;
  private static final double DEFAULT_PROB_TREE = 0.05;
  private static final String PARAM_PROB_CATCH = "probCatch";
  private static final String PARAM_PROB_TREE = "probTree";
  private static final double PROBABILITY_MIN = 0.0;
  private static final double PROBABILITY_MAX = 1.0;
  private static final int MIN_BURNING_NEIGHBORS_TO_CATCH = 1;

  public SpreadingOfFireRule() {
    super(new CardinalNeighborStrategy());
  }

  public SpreadingOfFireRule(Map<String, Double> parameters) {
    super(new CardinalNeighborStrategy(), parameters);
    if (parameters.containsKey(PARAM_PROB_CATCH) && (parameters.get(PARAM_PROB_CATCH) < PROBABILITY_MIN || parameters.get(PARAM_PROB_CATCH) > PROBABILITY_MAX)) {
      throw new IllegalArgumentException("probCatch must be between 0 and 1");
    }
    if (parameters.containsKey(PARAM_PROB_TREE) && (parameters.get(PARAM_PROB_TREE) < PROBABILITY_MIN || parameters.get(PARAM_PROB_TREE) > PROBABILITY_MAX)) {
      throw new IllegalArgumentException("probTree must be between 0 and 1");
    }
  }


  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    int rows = currentStates.length;
    int cols = currentStates[0].length;
    double probCatch = parameters == null || !parameters.containsKey(PARAM_PROB_CATCH)
        ? DEFAULT_PROB_CATCH
        : parameters.get(PARAM_PROB_CATCH);
    double probTree = parameters == null || !parameters.containsKey(PARAM_PROB_TREE)
        ? DEFAULT_PROB_TREE
        : parameters.get(PARAM_PROB_TREE);
    CellState[][] nextStates = new CellState[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState currentState = currentStates[r][c];
        if (currentState == SpreadingOfFireState.BURNING) {
          nextStates[r][c] = SpreadingOfFireState.EMPTY;
        } else if (currentState == SpreadingOfFireState.TREE) {
          List<CellState> neighbors = getNeighbors(currentStates, r, c);
          int burningNeighbors = countNeighborsWithState(neighbors, SpreadingOfFireState.BURNING);
          if (burningNeighbors >= MIN_BURNING_NEIGHBORS_TO_CATCH && Math.random() < probCatch) {
            nextStates[r][c] = SpreadingOfFireState.BURNING;
          } else {
            nextStates[r][c] = SpreadingOfFireState.TREE;
          }
        } else {
          nextStates[r][c] = Math.random() < probTree
              ? SpreadingOfFireState.TREE  
              : SpreadingOfFireState.EMPTY;
        }
      }
    }
    return nextStates;
  }
  @Override
  public Cell createCell(CellState state) {
    return new BasicCell(state);
  }
  @Override
  public Map<CellState, String> getStateColors() {
    return stateColorsFor(
        SpreadingOfFireState.EMPTY,
        SpreadingOfFireState.TREE,
        SpreadingOfFireState.BURNING);
  }

  @Override
  public CellState fromInt(int value) {
    return SpreadingOfFireState.fromInt(value);
  }

  @Override
  public CellState getNextStateInCycle(CellState current) {
    return SpreadingOfFireState.getNextStateInCycle(current);
  }

  @Override
  public int fromState(CellState state){ return SpreadingOfFireState.fromState(state); }
}
