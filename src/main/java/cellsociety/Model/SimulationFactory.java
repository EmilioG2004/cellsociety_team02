package cellsociety.Model;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.config.SimulationConfig;

public class SimulationFactory {

  public static Grid createInitialGrid(SimulationConfig config) {
    int rows = config.getRows();
    int cols = config.getCols();
    Grid grid = new Grid(rows, cols);
    GameRule rule = createGameRule(config);
    int[][] intStates = config.getInitialStates();
    CellState[][] cellStates = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cellStates[r][c] = rule.fromInt(intStates[r][c]);
      }
    }
    grid.setCellsState(cellStates);
    return grid;
  }

  public static Simulation createSimulation(SimulationConfig config) {
    GameRule rule = createGameRule(config);
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
  }

  public static GameRule createGameRule(SimulationConfig config) {
    String simulationType = config.getSimulationType();
    if (simulationType.equals("GameOfLife")) {
      return new ConwayGameOfLifeRule(config.getParameters());
    }
    throw new IllegalArgumentException("Invalid simulation type: " + simulationType);
  }
}
