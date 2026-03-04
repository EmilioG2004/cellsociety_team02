package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SugarScape.Agent;
import cellsociety.Model.CellState.SugarScape.Agent2;
import cellsociety.Model.CellState.SugarScape.Patch;
import cellsociety.Model.CellState.SugarScape.SugarScape2CellState;
import cellsociety.Model.CellState.SugarScape.SugarScapeCellState;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.RuleGrid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * SugarScape with reproduction (Preset 3). Extends the base SugarScape rules with:
 * <ul>
 *   <li>Aging: each agent ages one tick per step; dies at maxAge.</li>
 *   <li>Reproduction rule S: fertile agents of opposite sex with an adjacent empty
 *       patch produce a child that inherits parents' genetic traits.</li>
 * </ul>
 *
 * @author Michael Lei
 */
public class SugarScape2Rule extends SugarScapeRule {

  private static final String PARAM_MAX_AGE_MIN = "agentMaxAgeMin";
  private static final String PARAM_MAX_AGE_MAX = "agentMaxAgeMax";
  private static final String PARAM_FERTILE_START = "agentFertileStart";
  private static final String PARAM_FERTILE_END = "agentFertileEnd";
  private static final String PARAM_AGENT_SUGAR_INIT_MIN = "agentInitialMinimumSugar";
  private static final String PARAM_AGENT_SUGAR_INIT_MAX = "agentInitialMaximumSugar";
  private static final String PARAM_AGENT_MIN_METABOLISM = "agentMinimumMetabolism";
  private static final String PARAM_AGENT_MAX_METABOLISM = "agentMaximumMetabolism";
  private static final String PARAM_AGENT_VISION = "agentVision";

  private static final Random RNG = new Random();

  public SugarScape2Rule() {
    super();
  }

  public SugarScape2Rule(Map<String, Double> parameters) {
    super(parameters);
  }

  @Override
  public Grid getNextGrid(RuleGrid currentGrid) {
    Grid next = super.getNextGrid(currentGrid);
    int rows = next.getRows();
    int cols = next.getCols();

    ageAgents(next, rows, cols);
    reproduce(next, rows, cols);

    return next;
  }

  private void ageAgents(Grid grid, int rows, int cols) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        CellState state = grid.getCellState(r, c);
        if (state instanceof SugarScapeCellState ss && !ss.isVacant()) {
          Agent agent = ss.getAgent();
          if (agent instanceof Agent2 a2) {
            a2.incrementAge();
            if (!a2.isAlive()) {
              ss.setAgent(null);
            }
          }
        }
      }
    }
  }

  private void reproduce(Grid grid, int rows, int cols) {
    List<int[]> agentPositions = getAgentPositions(grid, rows, cols);
    Collections.shuffle(agentPositions);
    NeighborStrategy strategy = grid.getNeighborStrategy();

    for (int[] pos : agentPositions) {
      SugarScapeCellState ss = (SugarScapeCellState) grid.getCellState(pos[0], pos[1]);
      if (ss.isVacant()) {
        continue;
      }
      Agent2 agent = (Agent2) ss.getAgent();
      if (!agent.isFertile()) {
        continue;
      }

      List<int[]> neighbors = strategy.getNeighborPositions(rows, cols, pos[0], pos[1], agent.getVision());
      Collections.shuffle(neighbors);

      for (int[] n : neighbors) {
        if (!agent.isFertile()) {
          break;
        }
        tryMate(agent, pos, n, grid, strategy, rows, cols);
      }
    }
  }

  private void tryMate(Agent2 agent, int[] agentPos, int[] neighborPos,
      Grid grid, NeighborStrategy strategy, int rows, int cols) {
    SugarScapeCellState nss = (SugarScapeCellState) grid.getCellState(neighborPos[0], neighborPos[1]);
    if (nss.isVacant() || !(nss.getAgent() instanceof Agent2 mate)) {
      return;
    }
    if (!mate.isFertile() || mate.getGender() == agent.getGender()) {
      return;
    }

    int[] childPos = findEmptyAdjacentPatch(grid, strategy, rows, cols,
        agentPos[0], agentPos[1], neighborPos[0], neighborPos[1]);
    if (childPos == null) {
      return;
    }

    int childSugar = agent.getInitialSugar() / 2 + mate.getInitialSugar() / 2;
    Agent2 child = new Agent2(childSugar,
        RNG.nextBoolean() ? agent.getSugarMetabolism() : mate.getSugarMetabolism(),
        RNG.nextBoolean() ? agent.getVision() : mate.getVision(),
        getOptionalIntParam(PARAM_MAX_AGE_MIN), getOptionalIntParam(PARAM_MAX_AGE_MAX),
        getOptionalIntParam(PARAM_FERTILE_START), getOptionalIntParam(PARAM_FERTILE_END));

    agent.gainSugar(-(agent.getInitialSugar() / 2));
    mate.gainSugar(-(mate.getInitialSugar() / 2));

    ((SugarScapeCellState) grid.getCellState(childPos[0], childPos[1])).setAgent(child);
  }

  private int[] findEmptyAdjacentPatch(Grid grid, NeighborStrategy strategy,
      int rows, int cols, int r1, int c1, int r2, int c2) {
    int[] found = findVacantNeighbor(grid, strategy, rows, cols, r1, c1);
    return found != null ? found : findVacantNeighbor(grid, strategy, rows, cols, r2, c2);
  }

  private int[] findVacantNeighbor(Grid grid, NeighborStrategy strategy,
      int rows, int cols, int r, int c) {
    List<int[]> neighbors = strategy.getNeighborPositions(rows, cols, r, c);
    Collections.shuffle(neighbors);
    for (int[] pos : neighbors) {
      CellState state = grid.getCellState(pos[0], pos[1]);
      if (state instanceof SugarScapeCellState s && s.isVacant()) {
        return pos;
      }
    }
    return null;
  }

  @Override
  public CellState fromInt(int value) {
    if (value >= 100) {
      return new SugarScape2CellState(new Patch(), createAgent2());
    }
    if (value >= 0) {
      return new SugarScape2CellState(new Patch(value), null);
    }
    throw new IllegalArgumentException("Invalid SugarScape2 state: " + value);
  }

  private Agent2 createAgent2() {
    return new Agent2(
        getOptionalIntParam(PARAM_AGENT_SUGAR_INIT_MIN),
        getOptionalIntParam(PARAM_AGENT_SUGAR_INIT_MAX),
        getOptionalIntParam(PARAM_AGENT_MIN_METABOLISM),
        getOptionalIntParam(PARAM_AGENT_MAX_METABOLISM),
        getOptionalIntParam(PARAM_AGENT_VISION),
        getOptionalIntParam(PARAM_AGENT_VISION),
        getOptionalIntParam(PARAM_MAX_AGE_MIN),
        getOptionalIntParam(PARAM_MAX_AGE_MAX),
        getOptionalIntParam(PARAM_FERTILE_START),
        getOptionalIntParam(PARAM_FERTILE_END));
  }
}
