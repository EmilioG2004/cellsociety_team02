package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.GameRules.PercolationRule;
import cellsociety.Model.GameRules.SegregationRule;
import cellsociety.Model.GameRules.SpreadingOfFireRule;
import cellsociety.Model.GameRules.WaTorRule;
import cellsociety.config.SimulationConfig;
import java.util.Map;
import java.util.function.Function;

/** Builds a Simulation from a SimulationConfig (type, dimensions, initial states, parameters). */
public class SimulationFactory {

  public static final String SIMULATION_TYPE_GAME_OF_LIFE = "GameOfLife";
  public static final String SIMULATION_TYPE_SPREADING_OF_FIRE = "SpreadingOfFire";
  public static final String SIMULATION_TYPE_SEGREGATION = "Segregation";
  public static final String SIMULATION_TYPE_WATOR = "WaTor";
  public static final String SIMULATION_TYPE_PERCOLATION = "Percolation";
  private static final String ERROR_MESSAGE_INVALID_CONFIG = "Invalid simulation configuration: ";
  private static final String ERROR_UNKNOWN_TYPE = "Unknown simulation type: '%s'. Supported: "
      + SIMULATION_TYPE_GAME_OF_LIFE + ", " + SIMULATION_TYPE_SPREADING_OF_FIRE + ", "
      + SIMULATION_TYPE_SEGREGATION + ", " + SIMULATION_TYPE_WATOR + ", " + SIMULATION_TYPE_PERCOLATION;

  private static final Map<String, Function<Map<String, Double>, GameRule>> RULE_FACTORIES = Map.of(
      SIMULATION_TYPE_GAME_OF_LIFE, p -> new ConwayGameOfLifeRule(p != null ? p : Map.of()),
      SIMULATION_TYPE_SPREADING_OF_FIRE, p -> new SpreadingOfFireRule(p != null ? p : Map.of()),
      SIMULATION_TYPE_SEGREGATION, p -> new SegregationRule(p != null ? p : Map.of()),
      SIMULATION_TYPE_WATOR, p -> new WaTorRule(p != null ? p : Map.of()),
      SIMULATION_TYPE_PERCOLATION, p -> new PercolationRule(p != null ? p : Map.of()));

  public static Simulation createSimulation(SimulationConfig config) throws IllegalArgumentException {
    try {
      String simulationType = config.getSimulationType();
      Function<Map<String, Double>, GameRule> factory = RULE_FACTORIES.get(simulationType);
      if (factory == null) {
        throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_TYPE, simulationType));
      }
      GameRule rule = factory.apply(config.getParameters());
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
