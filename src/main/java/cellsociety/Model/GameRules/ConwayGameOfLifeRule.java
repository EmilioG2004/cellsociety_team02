package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.List;
import java.util.Map;

/** Conway's Game of Life: Moore neighborhood, birth on 3, survive on 2 or 3. */
public class ConwayGameOfLifeRule extends GameRule {

  /** Integer value representing a dead cell. */
  public static final int DEAD = 0;

  /** Integer value representing a living cell. */
  public static final int ALIVE = 1;

  private static final int SURVIVE_NEIGHBOR_COUNT_LOW = 2;
  private static final int SURVIVE_NEIGHBOR_COUNT_HIGH = 3;
  private static final int BIRTH_NEIGHBOR_COUNT = 3;

  public ConwayGameOfLifeRule() {
    super(new MooreNeighborStrategy());
  }

  public ConwayGameOfLifeRule(Map<String, Double> parameters) {
    super(new MooreNeighborStrategy(), parameters);
  }

  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    CellState[][] nextStates = new CellState[currentStates.length][currentStates[0].length];
    for (int r = 0; r < currentStates.length; r++) {
      for (int c = 0; c < currentStates[0].length; c++) {
        CellState currentState = currentStates[r][c];
        List<CellState> neighbors = getNeighbors(currentStates, r, c);
        int aliveNeighbors = countNeighborsWithState(neighbors, GameOfLifeState.ALIVE);
        if (currentState == GameOfLifeState.ALIVE && (aliveNeighbors == SURVIVE_NEIGHBOR_COUNT_LOW || aliveNeighbors == SURVIVE_NEIGHBOR_COUNT_HIGH)) {
          nextStates[r][c] = GameOfLifeState.ALIVE;
        } else if (currentState == GameOfLifeState.DEAD && aliveNeighbors == BIRTH_NEIGHBOR_COUNT) {
          nextStates[r][c] = GameOfLifeState.ALIVE;
        } else {
          nextStates[r][c] = GameOfLifeState.DEAD;
        }
      }
    }
    return nextStates;
  }

  @Override
  public Map<CellState, String> getStateColors() {
    return stateColorsFor(GameOfLifeState.DEAD, GameOfLifeState.ALIVE);
  }

  @Override
  public CellState fromInt(int value) {
    return GameOfLifeState.fromInt(value);
  }
  @Override
  public CellState getNextStateInCycle(CellState current) {
    return GameOfLifeState.getNextStateInCycle(current);
  }
}
