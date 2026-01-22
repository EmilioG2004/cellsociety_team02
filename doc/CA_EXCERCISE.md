# Cell Society Design Lab Discussion

#### Names and NetIDs
- Michael Lei - mql3
- Ixa Sigler - ivs5

## Simulations

### Commonalities
- Zero player
- Grid of independent cells
- Cells change state based on neighbors

### Variations
- Cell state changing rules
- Number of cell states (e.g., dead/alive, alive/burning/empty, agent1/agent2/none)

## Discussion Questions

* **How does a Cell know what rules to apply for its simulation?**
  * Rules should be defined globally when simulation starts, and during each update cycle the cell should pass its own state and neighbors into the rule function and decide what to do.

* **How does a Cell know about its neighbors?**
  * Cells should not store their own neighbors as that would mean each cell object would store 8 additional cell objects for its neighbors. Instead, we should have the grid get the neighbors for every cell from its x and y coordinates and pass that list of neighbors to the cell.

* **How can a Cell update itself without affecting its neighbors' update?**
  * Its intended update can be 'cached' before actually going into effect, making sure there are no race conditions or execution order errors between cells. We could store the cell's next state in every cell and have the grid update them when we get the next state of every cell.

* **What behaviors does the Grid itself have?**
  * The grid initializes all cells and has a method to return a cell's neighbors. It can also "wipe" all cells when switching simulation mode. The grid should also be able to iterate through every cell to update cells.

* **How can a Grid update all the Cells it contains?**
  * Iterate through a nested for loop: first update the nextState for every cell, then update every cell's current state to their nextState.

* **What information about a simulation needs to be in the configuration file?**
  * Starting state, type of simulation, and initial parameters.

* **How is configuration information used to set up a simulation?**
  * A class reads the XML and instantiates the correct grid, cells, and parameters based on the file.

* **How is the graphical view of the simulation updated after all the cells have been updated?**
  * Initially we thought we should store the imageView of every cell in the cell object, but we think it'd be better to keep the visuals separate from the data. Instead, after every update we should have a renderer class take the grid/cell data and render it on the screen.


## Alternate Designs

### Design Idea #1: Lesser Cell Independence

* Each cell class only stores its current state, next state, and a reference to the update rules. The Grid class contains a 2D array of these cell objects. The grid would iterate through this array, get the neighbors of each position, and call a cell method to update its next state with neighbor count.

* We don't have a cell class at all and instead make Grid just store a 2D grid of tuples of that position's state, next state, etc. So grid would be a 3D array. Cells would be a tuple of {currentState, nextState}. The grid would be responsible for both getting the neighbors for every position and the logic to update that position off its neighbors instead of passing it off to the cell.

### Design Idea #2: Greater Cell Independence

* Instead of relying on the grid for services, each Cell class contains its own view and neighbors.

* The grid is relegated solely to initializing cells.



## High Level Design Goals

We want to utilize OOP and ensure a high level of collaboration. Cells should be able to consult a game logic/"rulebook" class when updating, rely on the grid to access their neighbors, and pass state to the renderer for graphics. We also want to ensure the simulation manager can handle (a) processing a tick-based simulation with pausing and resetting and (b) pass these update ticks into cells without becoming too bloated.

## CRC Card Classes

### CellGrid
**Purpose:** Manages the grid of cells.

| CellGrid                      |      |
|-------------------------------|------|
| Cell[] getCellNeighbors(x, y) |      |
| Cell getCell(x, y)            |      |
| void updateAllStates()        | Cell |

```java
public class CellGrid {
    // get neighbors of cell at position (x, y)
    public Cell[] getCellNeighbors(int x, int y);
    
    // get one cell
    public Cell getCell(int x, int y);
    
    // update every cell's state in nested for loop
    private void updateAllCells();
}
```

### GameRule
**Purpose:** Represents a set of game rules. We'd make it an abstract class that every different set of game rules has to extend (e.g., ForestFire.java, Conway.java).

| GameRule                                     |      |
|----------------------------------------------|------|
| State getNextState(currentState, neighbors)  | Cell |
| Color getColorForState(state)                |      |

```java
public abstract class GameRule {
    private Enum stateEnum;
    
    public GameRule(Enum stateEnum) {
        this.stateEnum = stateEnum;
    }
    
    abstract public State getNextState(State currentState, List<Cell> neighbors);
    
    abstract public Color getColorForState(State state);
}
```

```java
public class ConwayRules extends GameRule {
    
    public ConwayRules(Enum stateEnum) {
        super(stateEnum);
    }
    
    public State getNextState(State currentState, List<Cell> neighbors) {
        int aliveCount = countAliveNeighbors(neighbors);
        if (currentState == StateEnum.DEAD && aliveCount == 3) {
            return StateEnum.ALIVE;
        }
        if (currentState == StateEnum.ALIVE && (aliveCount < 2 || aliveCount > 3)) {
            return StateEnum.DEAD;
        }
        return currentState;
    }
    
    public Color getColorForState(State state) {
        if (state == StateEnum.ALIVE) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
    }
}
```




### Cell
**Purpose:** Represents a cell in the simulation.

| Cell                              |            |
|-----------------------------------|------------|
| void updateNextState(neighbors)   | Grid       |
| void setCurrentStateToNextState() | Game Rules |
| State getState()                  |            |

```java
public class Cell {
    private State currentState;
    private State nextState;
    private GameRule gameRules;
    
    public Cell(State initialState, GameRule rules) {
        this.currentState = initialState;
        this.nextState = initialState;
        this.gameRules = rules;
    }
    
    // updates the cell's next state based on its neighbors and current state
    public void updateNextState(List<Cell> neighbors) {
        nextState = gameRules.getNextState(currentState, neighbors);
    }
    
    // sets the current state to be next state, called by the grid after every cell gets its nextState
    public void updateState() {
        currentState = nextState;
    }
    
    // returns current state of cell, to be used by renderer class to know how to render this cell
    public State getState() {
        return this.currentState;
    }
}
```

### Renderer
**Purpose:** Manages the rendering of the screen.

| Renderer                |          |
|-------------------------|----------|
| Scene getScene(Grid)    | Grid     |
|                         | GameRule |

```java
public class Renderer {
    // returns scene of rendered grid and cells, we'd get the cells from the grid and ask GameRules for the color of the cells
    public Scene getScene(CellGrid grid) {
        // calls game rules to get color of cells
        // renders grid visualization
        return scene;
    }
}
```

## Use Cases

* **Apply the rules to a middle cell:** Set the next state of a cell to dead by counting its number of neighbors using the Game of Life rules for a cell in the middle (i.e., with all its neighbors).

```java
List<Cell> neighbors = grid.getCellNeighbors(x, y);
State next = rules.getNextState(cell.getState(), neighbors);
cell.updateNextState(neighbors);
```

* **Apply the rules to an edge cell:** Set the next state of a cell to live by counting its number of neighbors using the Game of Life rules for a cell on the edge (i.e., with some of its neighbors missing).

```java
// returns < 8 cells, edge detection handled in getCellNeighbors
List<Cell> neighbors = grid.getCellNeighbors(x, y);
State next = rules.getNextState(cell.getState(), neighbors);
cell.updateNextState(neighbors);
```

* **Move to the next generation:** Update all cells in a simulation from their current state to their next state and display the result graphically.

```java
// First, calculate next state for every cell
for (int x = 0; x < grid.getWidth(); x++) {
    for (int y = 0; y < grid.getHeight(); y++) {
        Cell cell = grid.getCell(x, y);
        List<Cell> neighbors = grid.getCellNeighbors(x, y);
        cell.updateNextState(neighbors);
    }
}

// Then, commit all state changes after everyone has decided
for (int x = 0; x < grid.getWidth(); x++) {
    for (int y = 0; y < grid.getHeight(); y++) {
        grid.getCell(x, y).updateState();
    }
}

renderer.render(grid);
```

* **Set a simulation parameter:** Set the value of a parameter, probCatch, for a simulation, Fire, based on the value given in a data file.

```java
SimulationConfig config = configLoader.readConfig("fire.xml");

FireRules rules = new FireRules();
rules.setProbCatch(config.getDouble("probCatch"));

Simulation sim = new Simulation(new CellGrid(config), rules);
```

* **Switch simulations:** Load a new simulation from a data file, replacing the current running simulation with the newly loaded one.

```java
grid.clear();
currentSimulation = simulationFactory.fromConfig(xmlFile);
renderer.render(currentSimulation.getGrid());
```