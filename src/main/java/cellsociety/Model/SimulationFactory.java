package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.GameRules.SpreadingOfFireRule;
import cellsociety.config.SimulationConfig;
import java.util.Map;
import java.util.HashMap;

/** Builds a Simulation from a SimulationConfig (type, dimensions, initial states, parameters). */
public class SimulationFactory {

  public static final String SIMULATION_TYPE_GAME_OF_LIFE = "GameOfLife";
  public static final String SIMULATION_TYPE_SPREADING_OF_FIRE = "SpreadingOfFire";
  private static final int MIN_PARAMS_TO_APPLY = 1;
  private static final String ERROR_MESSAGE_INVALID_CONFIG = "Invalid simulation configuration: ";
  private static final String ERROR_UNKNOWN_TYPE = "Unknown simulation type: '%s'. Supported: "
      + SIMULATION_TYPE_GAME_OF_LIFE + ", " + SIMULATION_TYPE_SPREADING_OF_FIRE;

  public static final Map<String, GameRule> GAME_RULES = Map.of(
      SIMULATION_TYPE_GAME_OF_LIFE, new ConwayGameOfLifeRule(),
      SIMULATION_TYPE_SPREADING_OF_FIRE, new SpreadingOfFireRule());

  public static Simulation createSimulation(SimulationConfig config) throws IllegalArgumentException {
    try {
      String simulationType = config.getSimulationType();
      GameRule rule = GAME_RULES.get(simulationType);
      if (rule == null) {
        throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_TYPE, simulationType));
      }
      if (config.getParameters().size() >= MIN_PARAMS_TO_APPLY) {
        rule.setParameters(config.getParameters());
      }
      int rows = config.getRows();
      int cols = config.getCols();
      MutableGrid grid = new Grid(rows, cols);
      int[][] intStates = config.getInitialStates();
      CellState[][] cellStates = new CellState[rows][cols];
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          cellStates[r][c] = rule.fromInt(intStates[r][c]);
        }
      }
      grid.setCellsState(cellStates);
      return new Simulation(grid, rule);
    } catch (Exception e) {
      throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_CONFIG + e.getMessage());
    }
  }
}
