package cellsociety.Model;
import cellsociety.config.SimulationConfig;
public class SimulationFactory {
    public static Grid createInitialGrid(SimulationConfig config) {
        int rows = config.getRows();
        int cols = config.getCols();
        Grid grid = new Grid(rows, cols);
        grid.setCellsState(config.getInitialStates());
        return grid;
    }
    public static Simulation createSimulation(SimulationConfig config) {
        Grid grid = createInitialGrid(config);
        GameRule rule = createGameRule(config);
        return new Simulation(grid, rule);
    }
    public static GameRule createGameRule(SimulationConfig config) {
        String simulationType = config.getSimulationType();
        if (simulationType.equals("GameOfLife")) {
            return new ConwayGameOfLifeRule(config.getParameters());
        }
    }
}
