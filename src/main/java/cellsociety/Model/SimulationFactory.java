package cellsociety.Model;

public class SimulationFactory {
    public static Grid createInitialGrid(Configuration config) {
        int rows = config.getRows();
        int cols = config.getCols();
        Grid grid = new Grid(rows, cols);
        grid.setCellsState(config.getInitialState());
        return grid;
    }
    public static Simulation createSimulation(Configuration config) {
        Grid grid = createInitialGrid(config);
        GameRule rule = createGameRule(config);
        return new Simulation(grid, rule);
    }
    public static GameRule createGameRule(Configuration config) {
        String ruleType = config.getRuleType();
        if (ruleType.equals("GameOfLife")) {
            return new ConwayGameOfLifeRule();
        }
        return null;
    }
    private static class Configuration {
        public int getRows() {
            return 0;
        }
        public int getCols() {
            return 0;
        }
        public int[][] getInitialState() {
            return null;
        }
    }
}
