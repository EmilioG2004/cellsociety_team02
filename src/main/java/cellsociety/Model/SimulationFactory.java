package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.GameRules.SegregationRule;
import cellsociety.Model.GameRules.SpreadingOfFireRule;
import cellsociety.config.SimulationConfig;
import java.util.Map;
import java.util.HashMap;

public class SimulationFactory {
  public static final Map<String, GameRule> GAME_RULES = Map.of(
      "GameOfLife", new ConwayGameOfLifeRule(),
      "SpreadingOfFire", new SpreadingOfFireRule(),
      "Segregation", new SegregationRule());
  public static Simulation createSimulation(SimulationConfig config) throws IllegalArgumentException {
    try {
      String simulationType = config.getSimulationType();
      GameRule rule = GAME_RULES.get(simulationType);
      if (config.getParameters().size() > 0) {
        rule.setParameters(config.getParameters());
      }
      int rows = config.getRows();
      int cols = config.getCols();
      Grid grid = new Grid(rows, cols);
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
      throw new IllegalArgumentException("Invalid simulation configuration: " + e.getMessage());
    }
  }
}
