package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SugarScape.Agent;
import cellsociety.Model.CellState.SugarScape.Patch;
import cellsociety.Model.CellState.SugarScape.SugarScapeCellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sugarscape: agents on a grid of sugar patches. Implements preset rules:
 * <ul>
 *   <li>Patch G: grow back sugarGrowBackRate units every sugarGrowBackInterval ticks, up to capacity.</li>
 *   <li>Agent M: look in 4 directions up to vision; move to best vacant patch (most sugar, tie-break closest);
 *       take sugar, add to agent; subtract metabolism; die if sugar ≤ 0.</li>
 * </ul>
 * Preset defaults: initial sugar 5–25, vision 1–6, metabolism 1–4, sugarGrowBackRate 1, sugarGrowBackInterval 1.
 *
 * @author Michael Lei
 */
public class SugarScapeRule extends GameRule {

  private static final String PARAM_SUGAR_GROW_BACK_RATE = "sugarGrowBackRate";
  private static final String PARAM_SUGAR_GROW_BACK_INTERVAL = "sugarGrowBackInterval";
  private static final String PARAM_PATCH_CAPACITY = "patchCapacity";
  private static final String PARAM_AGENT_METABOLISM = "agentMetabolism";
  private static final String PARAM_AGENT_SUGAR_INIT_MIN = "agentInitialMinimumSugar";
  private static final String PARAM_AGENT_SUGAR_INIT_MAX = "agentInitialMaximumSugar";
  private static final String PARAM_AGENT_MIN_METABOLISM = "agentMinimumMetabolism";
  private static final String PARAM_AGENT_MAX_METABOLISM = "agentMaximumMetabolism";
  private static final String PARAM_AGENT_VISION = "agentVision";
  private static final String ERROR_INVALID_STATE = "Invalid SugarScape state: ";

  private static final Map<String, Double> DEFAULT_PARAMETERS = Map.ofEntries(
      Map.entry(PARAM_SUGAR_GROW_BACK_RATE, (double) Patch.DEFAULT_GROW_BACK_RATE),
      Map.entry(PARAM_SUGAR_GROW_BACK_INTERVAL, (double) Patch.DEFAULT_GROW_BACK_INTERVAL),
      Map.entry(PARAM_PATCH_CAPACITY, (double) Patch.DEFAULT_SUGAR_CAPACITY),
      Map.entry(PARAM_AGENT_SUGAR_INIT_MIN, (double) Agent.DEFAULT_INIT_SUGAR_MIN),
      Map.entry(PARAM_AGENT_SUGAR_INIT_MAX, (double) Agent.DEFAULT_INIT_SUGAR_MAX),
      Map.entry(PARAM_AGENT_MIN_METABOLISM, (double) Agent.DEFAULT_SUGAR_METABOLISM_MIN),
      Map.entry(PARAM_AGENT_MAX_METABOLISM, (double) Agent.DEFAULT_SUGAR_METABOLISM_MAX),
      Map.entry(PARAM_AGENT_VISION, (double) Agent.DEFAULT_VISION_MAX),
      Map.entry(PARAM_AGENT_METABOLISM, (double) Agent.DEFAULT_SUGAR_METABOLISM_MAX));

  /** Current tick; used to apply grow-back every sugarGrowBackInterval ticks. */
  private int tick = 0;


  public SugarScapeRule() {
    this(DEFAULT_PARAMETERS);
  }

  public SugarScapeRule(Map<String, Double> parameters) {
    super(parameters != null ? parameters : DEFAULT_PARAMETERS);
  }

  @Override
  public NeighborStrategy getDefaultNeighborStrategy() {
    return new VonNeumannNeighborStrategy();
  }

  @Override
  public Grid getNextGrid(Grid currentGrid) {
    int rows = currentGrid.getRows();
    int cols = currentGrid.getCols();
    // Build next grid with new state instances (patch copied, no agent) so we don't mutate current.
    CellState[][] copiedStates = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        copiedStates[r][c] = ((SugarScapeCellState) currentGrid.getCellState(r, c)).copyWithoutAgent();
      }
    }
    Grid next = new ArrayGrid(copiedStates, currentGrid.getNeighborStrategy());

    List<int[]> agentPositions = getAgentPositions(currentGrid, rows, cols);
    Collections.shuffle(agentPositions);

    Set<String> claimed = new HashSet<>();
    NeighborStrategy strategy = currentGrid.getNeighborStrategy();

    for (int[] pos : agentPositions) {
      int r = pos[0], c = pos[1];
      SugarScapeCellState currentState = (SugarScapeCellState) currentGrid.getCellState(r, c);
      Agent agent = currentState.getAgent();
      List<int[]> neighbors = strategy.getNeighborPositions(rows, cols, r, c, agent.getVision());
      int[] best = getBestVacantSpot(neighbors, currentGrid, claimed, r, c);

      SugarScapeCellState destState = (SugarScapeCellState) next.getCellState(best[0], best[1]);
      destState.setAgent(agent);
      agent.gainSugar(destState.getPatch().giveSugar());
      if (!agent.metabolize()) {
        destState.setAgent(null);
      }
      claimed.add(key(best[0], best[1]));

      // Source cell in next is already vacant (copyWithoutAgent); only clear if we moved
      if (best[0] != r || best[1] != c) {
        ((SugarScapeCellState) next.getCellState(r, c)).setAgent(null);
      }
    }

    int interval = getSugarGrowBackInterval();
    if (interval > 0 && tick % interval == 0) {
      int rate = getSugarGrowBackRate();
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          CellState state = next.getCellState(row, col);
          if (state instanceof SugarScapeCellState ss) {
            ss.getPatch().growBack(rate);
          }
        }
      }
    }
    tick++;
    return next;
  }

  private static String key(int r, int c) {
    return r + "," + c;
  }

  /**
   * Returns the best vacant, unclaimed neighbor (most sugar; tie-break by distance), or (agentRow, agentCol) if none.
   */
  private int[] getBestVacantSpot(List<int[]> neighbors, Grid grid, Set<String> claimed,
      int agentRow, int agentCol) {
    int[] best = new int[]{agentRow, agentCol};
    int bestSugar = grid.getCellState(agentRow, agentCol) instanceof SugarScapeCellState ss
        ? ss.getPatchSugar() : 0;
    int bestDist = 0;

    for (int[] n : neighbors) {
      String k = key(n[0], n[1]);
      if (claimed.contains(k)) continue;
      if (!(grid.getCellState(n[0], n[1]) instanceof SugarScapeCellState ns) || !ns.isVacant()) continue;

      int sugar = ns.getPatchSugar();
      int dist = Math.abs(n[0] - agentRow) + Math.abs(n[1] - agentCol);
      boolean better = sugar > bestSugar
          || (sugar == bestSugar && bestDist > 0 && dist < bestDist);
      if (better) {
        best = n;
        bestSugar = sugar;
        bestDist = dist;
      }
    }
    return best;
  }
  /** Returns positions of all occupied cells in the grid. */
  protected List<int[]> getAgentPositions(Grid grid, int rows, int cols) {
    List<int[]> out = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState state = grid.getCellState(r, c);
        if (state instanceof SugarScapeCellState ss && !ss.isVacant()) {
          out.add(new int[]{r, c});
        }
      }
    }
    return out;
  }
  @Override
  public CellState fromInt(int value) {
    if (value >= 100) {
      return new SugarScapeCellState(new Patch(), createAgent());
    }
    if (value >= 0) {
      return new SugarScapeCellState(new Patch(value), null);
    }
    throw new IllegalArgumentException(ERROR_INVALID_STATE + value);
  }

  private Agent createAgent() {
    Integer sugarMin = getOptionalIntParam(PARAM_AGENT_SUGAR_INIT_MIN);
    Integer sugarMax = getOptionalIntParam(PARAM_AGENT_SUGAR_INIT_MAX);
    Integer metabMin = getOptionalIntParam(PARAM_AGENT_MIN_METABOLISM);
    Integer metabMax = getOptionalIntParam(PARAM_AGENT_MAX_METABOLISM);
    Integer visionParam = getOptionalIntParam(PARAM_AGENT_VISION);
    return new Agent(sugarMin, sugarMax, metabMin, metabMax, visionParam, visionParam);
  }

  /** Returns the integer value of the parameter with the given key, or null if absent. */
  protected Integer getOptionalIntParam(String key) {
    return parameters.containsKey(key) ? parameters.get(key).intValue() : null;
  }

  private int getSugarGrowBackRate() {
    return parameters.containsKey(PARAM_SUGAR_GROW_BACK_RATE)
        ? parameters.get(PARAM_SUGAR_GROW_BACK_RATE).intValue()
        : Patch.DEFAULT_GROW_BACK_RATE;
  }

  private int getSugarGrowBackInterval() {
    return parameters.containsKey(PARAM_SUGAR_GROW_BACK_INTERVAL)
        ? parameters.get(PARAM_SUGAR_GROW_BACK_INTERVAL).intValue()
        : Patch.DEFAULT_GROW_BACK_INTERVAL;
  }
}
