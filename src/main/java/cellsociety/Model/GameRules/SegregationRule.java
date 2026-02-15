package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SegregationState;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.List;
import java.util.*;

/**
 * Schelling Segregation model:
 * Agents (GROUP_A / GROUP_B) are "happy" if
 * (same-type occupied neighbors)/(occupied neighbors) >= satisfactionThreshold.
 * Unhappy agents move to random EMPTY cells.
 *
 * Params:
 * - satisfactionThreshold (default 0.5) in [0, 1]
 */
public class SegregationRule extends GameRule {

  private static final double DEFAULT_THRESHOLD = 0.3;
  private static final String PARAM_SATISFACTION_THRESHOLD = "satisfactionThreshold";
  private static final double THRESHOLD_MIN = 0.0;
  private static final double THRESHOLD_MAX = 1.0;
  private static final int NO_OCCUPIED_NEIGHBORS = 0;

  public SegregationRule() {
    super(new MooreNeighborStrategy());
  }

  public SegregationRule(Map<String, Double> parameters) {
    super(new MooreNeighborStrategy(), parameters);
    if (parameters.containsKey(PARAM_SATISFACTION_THRESHOLD) && (parameters.get(PARAM_SATISFACTION_THRESHOLD) < THRESHOLD_MIN || parameters.get(PARAM_SATISFACTION_THRESHOLD) > THRESHOLD_MAX)) {
      throw new IllegalArgumentException("satisfactionThreshold must be between 0 and 1");
    }
  }


  @Override
  public CellState[][] getNextStates(CellState[][] currentStates) {
    int rows = currentStates.length;
    int cols = currentStates[0].length;

    double threshold = parameters == null || !parameters.containsKey(PARAM_SATISFACTION_THRESHOLD)
        ? DEFAULT_THRESHOLD
        : parameters.get(PARAM_SATISFACTION_THRESHOLD);

    List<int[]> emptyCells = new ArrayList<>();
    List<Mover> movers = new ArrayList<>();

    CellState[][] nextStates = new CellState[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState s = currentStates[r][c];
        nextStates[r][c] = s;

        if (s == SegregationState.EMPTY) {
          emptyCells.add(new int[] {r, c});
        } else {
          if (!isSatisfied(currentStates, r, c, s, threshold)) {
            movers.add(new Mover(r, c, s));
          }
        }
      }
    }

    // clear movers' original positions
    for (Mover m : movers) {
      nextStates[m.r][m.c] = SegregationState.EMPTY;
      emptyCells.add(new int[] {m.r, m.c});
    }

    Collections.shuffle(emptyCells);

    // relocate movers to available empty cells
    int moves = Math.min(movers.size(), emptyCells.size());

    for (int i = 0; i < moves; i++) {
      Mover m = movers.get(i);
      int[] dest = emptyCells.get(i);
      nextStates[dest[0]][dest[1]] = m.state;
    }

    // if movers > emptyCells, the extras should remain in their original location instead of disappearing
    // put them back where they came from
    for (int i = moves; i < movers.size(); i++) {
      Mover m = movers.get(i);
      nextStates[m.r][m.c] = m.state;
    }

    return nextStates;
  }

  private boolean isSatisfied(CellState[][] grid, int r, int c, CellState self, double threshold) {
    List<CellState> neighbors = getNeighbors(grid, r, c);

    int occupied = 0;
    int same = 0;

    for (CellState n : neighbors) {
      if (n == SegregationState.EMPTY) continue;
      occupied++;
      if (n == self) same++;
    }

    if (occupied == NO_OCCUPIED_NEIGHBORS) return true;

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
  public Map<CellState, String> getStateColors() {
    return Map.of(
        SegregationState.EMPTY, SegregationState.EMPTY.getColor(),
        SegregationState.GROUP_A, SegregationState.GROUP_A.getColor(),
        SegregationState.GROUP_B, SegregationState.GROUP_B.getColor());
  }

  @Override
  public CellState fromInt(int value) {
    return SegregationState.fromInt(value);
  }

  @Override
  public CellState getNextStateInCycle(CellState current) {
    return SegregationState.getNextStateInCycle(current);
  }
}
