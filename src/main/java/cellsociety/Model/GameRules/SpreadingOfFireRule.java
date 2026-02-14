package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SpreadingOfFireState;
import cellsociety.Model.Utils.CardinalNeighborStrategy;
import java.util.List;
import java.util.Map;

/**
 * Spreading of Fire cellular automaton: TREE cells catch fire from BURNING neighbors
 * with probability probCatch; BURNING becomes EMPTY; EMPTY becomes TREE with probability probTree.
 * Uses cardinal (4) neighbors. Parameters: probCatch (default 0.15), probTree (default 0.05).
 */
public class SpreadingOfFireRule extends GameRule {

  private static final double DEFAULT_PROB_CATCH = 0.15;
  private static final double DEFAULT_PROB_TREE = 0.05;

  public SpreadingOfFireRule() {
    super(new CardinalNeighborStrategy());
  }

  public SpreadingOfFireRule(Map<String, Double> parameters) {
    super(new CardinalNeighborStrategy(), parameters);
    if(parameters.containsKey("probCatch") && (parameters.get("probCatch") < 0 || parameters.get("probCatch") > 1)) {
      throw new IllegalArgumentException("probCatch must be between 0 and 1");
    }
    if(parameters.containsKey("probTree") && (parameters.get("probTree") < 0 || parameters.get("probTree") > 1)) {
      throw new IllegalArgumentException("probTree must be between 0 and 1");
    }
  }


  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    int rows = currentStates.length;
    int cols = currentStates[0].length;
    double probCatch = parameters == null || !parameters.containsKey("probCatch")
        ? DEFAULT_PROB_CATCH
        : parameters.get("probCatch");
    double probTree = parameters == null || !parameters.containsKey("probTree")
        ? DEFAULT_PROB_TREE
        : parameters.get("probTree");
    CellState[][] nextStates = new CellState[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState currentState = currentStates[r][c];
        if (currentState == SpreadingOfFireState.BURNING) {
          nextStates[r][c] = SpreadingOfFireState.EMPTY;
        } else if (currentState == SpreadingOfFireState.TREE) {
          List<CellState> neighbors = getNeighbors(currentStates, r, c);
          int burningNeighbors = countNeighborsWithState(neighbors, SpreadingOfFireState.BURNING);
          if (burningNeighbors > 0 && Math.random() < probCatch) {
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
  public Map<CellState, String> getStateColors() {
    return Map.of(
        SpreadingOfFireState.EMPTY, SpreadingOfFireState.EMPTY.getColor(),
        SpreadingOfFireState.TREE, SpreadingOfFireState.TREE.getColor(),
        SpreadingOfFireState.BURNING, SpreadingOfFireState.BURNING.getColor());
  }

  @Override
  public CellState fromInt(int value) {
    return SpreadingOfFireState.fromInt(value);
  }
}
