package cellsociety.Model.GameRules;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.GameOfLifeState;
import cellsociety.Model.Utils.MooreNeighborStrategy;
import java.util.List;
import java.util.Map;

/**
 * Defines the transition rules for Conway’s Game of Life.
 *
 * Purpose:
 * This class implements the classic Conway’s Game of Life cellular automaton.
 * It determines how each cell evolves from one generation to the next based
 * on its current state and the number of live neighboring cells. The rule
 * encapsulates both the state-transition logic and the neighbor semantics
 * specific to this simulation type.
 *
 * Assumptions:
 * - Cell states are represented as integers, where DEAD and ALIVE are the only
 *   valid states.
 * - The list of neighbors provided to getNextState corresponds to the Moore
 *   neighborhood (up to eight surrounding cells).
 * - The grid and cell update mechanism ensure that all state transitions are
 *   computed before any state is committed.
 *
 * Dependencies:
 * - GameRule, which provides shared rule functionality and helper methods.
 * - MooreNeighborStrategy to define the neighborhood used by this simulation.
 *
 * Example usage:
 * GameRule rules = new ConwayGameOfLifeRule();
 * int next = rules.getNextState(currentState, neighbors);
 *
 * @author Emilio Garcia
 * @author Michael Lei
 */
public class ConwayGameOfLifeRule extends GameRule {

  /** Integer value representing a dead cell. */
  public static final int DEAD = 0;

  /** Integer value representing a living cell. */
  public static final int ALIVE = 1;

  /**
   * Constructs a ConwayGameOfLifeRule with no additional parameters.
   *
   * Purpose:
   * Initializes the rule using a Moore neighborhood, which considers all
   * surrounding cells when computing the next state.
   *
   * Assumptions:
   * - No simulation-specific parameters are required for standard Game of Life
   *   behavior.
   */
  public ConwayGameOfLifeRule() {
    super(new MooreNeighborStrategy());
  }

  /**
   * Constructs a ConwayGameOfLifeRule with a parameter map.
   *
   * Purpose:
   * Allows the rule to be created with externally supplied parameters, enabling
   * consistency with other rule types that may require configuration values.
   *
   * Assumptions:
   * - The parameters map may be empty, as Conway’s Game of Life does not require
   *   additional parameters.
   *
   * @param parameters map of simulation parameters parsed from configuration
   */
  public ConwayGameOfLifeRule(Map<String, String> parameters) {
    super(new MooreNeighborStrategy(), parameters);
  }

  /**
   * Computes the next state of a cell based on its current state and neighbors.
   *
   * Purpose:
   * Encapsulates the core rules of Conway’s Game of Life:
   * - A living cell survives if it has two or three living neighbors.
   * - A dead cell becomes alive if it has exactly three living neighbors.
   * - All other cells become or remain dead.
   *
   * Assumptions:
   * - currentState is either DEAD or ALIVE.
   * - neighbors contains only valid ReadOnlyCell objects.
   *
   * @param currentStates the cell’s current state
   * @return the computed next state for the cell
   */
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

  /**
   * Returns the mapping from cell states to display colors.
   *
   * Purpose:
   * Provides the view layer with a consistent mapping from simulation states
   * to visual representations without hard-coding colors elsewhere.
   *
   * Assumptions:
   * - The view interprets color strings using JavaFX-compatible color values.
   *
   * @return a map from state values to color names
   */
  @Override
  public CellState fromInt(int value) {
    return GameOfLifeState.fromInt(value);
  }
}
