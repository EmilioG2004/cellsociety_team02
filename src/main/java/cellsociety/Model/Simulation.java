package cellsociety.Model;

public class Simulation {
    private Grid myGrid;
    private GameRule myRule;
    //private SimulationConfig myConfig;

    public Simulation(Grid grid, GameRule rule) {
        this.myGrid = grid;
        this.myRule = rule;
        //this.myConfig = config;
    }
    public void step() {
        myGrid.updateAllCells(myRule);
        myGrid.commitAllStates();
    }
    public GameRule getRule() {
        return myRule;
    }
    public ReadOnlyGrid getGrid() {
        return myGrid;
    }
}
