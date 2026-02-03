package cellsociety.Model.GameRules;

import cellsociety.Model.GameRule;
import cellsociety.Model.ReadOnlyCell;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.List;
import java.util.Map;

public class ConwayGameOfLifeRule extends GameRule {
  public static final int DEAD = 0;
  public static final int ALIVE = 1;

  public ConwayGameOfLifeRule(){
    super(new MooreNeighborStrategy());
  }

  @Override
  public int getNextState(int currentState, List<ReadOnlyCell> neighbors) {
    int aliveNeighbors = countNeighborsWithState(neighbors, ALIVE);

    if (currentState == ALIVE && (aliveNeighbors == 2 || aliveNeighbors == 3)){
      return ALIVE;
    } else if (currentState == DEAD && aliveNeighbors == 3){
      return ALIVE;
    }
    return DEAD;
  }

  @Override
  public Map<String, String> getStateColors() {
    return Map.of(
        "ALIVE", "#90EE90",
        "DEAD", "#FFFFFF"
    );
  }
}
