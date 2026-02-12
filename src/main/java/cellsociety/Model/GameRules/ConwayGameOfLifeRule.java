package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.List;
import java.util.Map;

public class ConwayGameOfLifeRule extends GameRule {

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
        if (currentState == GameOfLifeState.ALIVE && (aliveNeighbors == 2 || aliveNeighbors == 3)) {
          nextStates[r][c] = GameOfLifeState.ALIVE;
        } else if (currentState == GameOfLifeState.DEAD && aliveNeighbors == 3) {
          nextStates[r][c] = GameOfLifeState.ALIVE;
        } else {
          nextStates[r][c] = GameOfLifeState.DEAD;
          }
          }
      }
    return nextStates;
    }

  public Map<CellState, String> getStateColors() {
    return Map.of(GameOfLifeState.DEAD, GameOfLifeState.DEAD.getColor(), GameOfLifeState.ALIVE, GameOfLifeState.ALIVE.getColor());
  }

  @Override
  public CellState fromInt(int value) {
    return GameOfLifeState.fromInt(value);
  }
}
