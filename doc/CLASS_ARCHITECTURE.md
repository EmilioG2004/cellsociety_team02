# Class Architecture Analysis

Based on the design requirements, here are the classes/abstractions needed and how they interact.

## Package Structure

```
cellsociety/
├── model/          # Simulation logic (NO JavaFX imports)
│   ├── cell/
│   ├── grid/
│   ├── rules/
│   ├── state/
│   └── simulation/
├── view/           # UI components (JavaFX)
│   ├── components/
│   └── controllers/
├── config/         # XML parsing and configuration
└── Main.java       # Application entry point
```

## Core Abstractions & Classes

### 1. **Model Package (`cellsociety.model`)**

#### 1.1 State Management
**`State` (Interface/Enum/Record)**
- Represents a cell's state (e.g., ALIVE, DEAD, TREE, BURNING, EMPTY, FISH, SHARK)
- Should be immutable (DESIGN-17)
- Different simulations define different state sets
- **Interactions**: Used by `Cell`, `GameRules`, `SimulationConfig`

**`StateType` (Enum - Optional)**
- Could define common state categories if needed across simulations

#### 1.2 Cell Abstraction
**`Cell` (Interface)**
```java
public interface Cell {
    void updateNextState(List<Cell> neighbors, GameRules rules);
    void commitState();
    State getState();
    State getNextState(); // for debugging/testing
}
```
- **Purpose**: Represents a single cell in the grid
- **Interactions**: 
  - Receives neighbor information from `CellGrid`
  - Uses `GameRules` to determine next state
  - Stores current and next state (two-pass update)

**`BasicCell` (Concrete Implementation)**
- Implements `Cell` interface
- Stores `currentState` and `nextState`
- **Interactions**: Used by `CellGrid`

#### 1.3 Grid Management
**`CellGrid` (Class)**
```java
public class CellGrid {
    private List<List<Cell>> grid;
    private int rows;
    private int cols;
    // No stored NeighborStrategy—obtained from rules during updateAllCells(rules)
    
    // Read-only access for view
    public State getCellState(int row, int col);
    public int getRows();
    public int getCols();
    
    // Internal: uses rules.getNeighborStrategy() when computing neighbors
    private List<Cell> getCellNeighbors(int row, int col, GameRules rules);
    public void updateAllCells(GameRules rules);
    public void initializeGrid(List<List<State>> initialState);
}
```
- **Purpose**: Manages 2D grid of cells
- **Key Design**: Does NOT expose internal data structure (DESIGN-10). Does NOT store NeighborStrategy; gets it from `rules.getNeighborStrategy()` when stepping.
- **Interactions**: 
  - Used by `Simulation` to update cells
  - Provides read-only access to `GridView`
  - Uses `GameRules.getNeighborStrategy()` to determine neighbors during `updateAllCells(rules)`

**`NeighborStrategy` (Interface)**
```java
public interface NeighborStrategy {
    List<int[]> getNeighborOffsets();
    // Could also handle edge wrapping, toroidal, etc.
}
```
- **Purpose**: Defines which cells count as neighbors (Moore, Von Neumann, custom)
- **Implementations**: 
  - `MooreNeighborStrategy` (8-directional)
  - `VonNeumannNeighborStrategy` (4-directional)
  - `ToroidalNeighborStrategy` (wraparound edges)
- **Interactions**: Owned by `GameRules`; exposed via `GameRules.getNeighborStrategy()`. `CellGrid` uses it by calling `rules.getNeighborStrategy()` when computing neighbors inside `updateAllCells(rules)`.

#### 1.4 Rules Engine
**`GameRules` (Abstract Class)**
```java
public abstract class GameRules {
    protected Map<String, Double> parameters;
    protected NeighborStrategy neighborStrategy;  // owned by this rules type
    
    public abstract State getNextState(State currentState, List<Cell> neighbors);
    public abstract List<State> getPossibleStates();
    public abstract Map<String, String> getStateColors(); // for view
    public NeighborStrategy getNeighborStrategy();       // grid uses this when stepping
    
    // Common helper methods
    protected int countNeighborsWithState(List<Cell> neighbors, State targetState);
}
```
- **Purpose**: Defines state transition rules and **neighbor semantics** for a simulation type. Owns the NeighborStrategy for that type (e.g., Life = Moore, Fire = Von Neumann).
- **Interactions**: Used by `Cell` to determine next state; used by `CellGrid` for both transition logic and for `getNeighborStrategy()` when computing neighbor lists.
- **Implementations**:
  - `ConwayGameOfLifeRules`
  - `FireRules`
  - `PercolationRules`
  - `SegregationRules`
  - `WaTorRules`

**Concrete Rule Classes**:
- `ConwayGameOfLifeRules` - Implements Conway's Game of Life
- `FireRules` - Implements forest fire simulation (uses `probCatch` parameter)
- `PercolationRules` - Implements percolation simulation
- `SegregationRules` - Implements Schelling's segregation model
- `WaTorRules` - Implements Wa-Tor predator-prey simulation

#### 1.5 Simulation Management
**`Simulation` (Class)**
```java
public class Simulation {
    private CellGrid grid;
    private GameRules rules;
    private SimulationConfig config;
    
    public void step(); // Advance one generation
    public CellGrid getGrid(); // Returns read-only view
    public SimulationConfig getConfig();
}
```
- **Purpose**: Encapsulates a running simulation
- **Interactions**: 
  - Managed by `SimulationDriver`
  - Provides grid to `GridView` for rendering
  - Uses `GameRules` to update cells

**`SimulationDriver` (Class)**
```java
public class SimulationDriver {
    private Simulation simulation;
    private double currentSpeed; // steps per second
    private boolean isRunning;
    private boolean isPaused;
    
    public void start();
    public void pause();
    public void setSpeed(double speed);
    public void step(); // Single step when paused
}
```
- **Purpose**: Controls simulation execution (start, pause, speed)
- **Interactions**: 
  - Manages `Simulation` lifecycle
  - Called by view controllers (CELL-11, CELL-12, CELL-13)

### 2. **View Package (`cellsociety.view`)**

#### 2.1 Grid Rendering
**`GridView` (Interface)**
```java
public interface GridView {
    Node getNode(); // Returns JavaFX Node for display
    void render(CellGrid grid); // Updates display
    void setCellSize(double size);
}
```
- **Purpose**: Abstract rendering interface (DESIGN-10)
- **Interactions**: Used by `SimulationView` to display grid

**`CanvasGridView` (Concrete Implementation)**
- Renders grid using JavaFX `Canvas`
- Maps states to colors using `GameRules.getStateColors()`

**`GridPaneGridView` (Alternative Implementation)**
- Renders grid using JavaFX `GridPane` with colored rectangles
- Can be swapped without changing other code

#### 2.2 Main View
**`SimulationView` (Class)**
```java
public class SimulationView {
    private GridView gridView;
    private SimulationInfoPanel infoPanel;
    private ControlPanel controlPanel;
    
    public void updateDisplay(Simulation simulation);
    public void showError(String message);
}
```
- **Purpose**: Main UI container
- **Interactions**: 
  - Contains `GridView` for grid display
  - Contains control panels
  - Receives updates from `SimulationController`

**`SimulationInfoPanel` (Class)**
- Displays simulation metadata (title, author, description, parameters)
- Meets CELL-09 requirement

**`ControlPanel` (Class)**
- Contains buttons: Start, Pause, Reset, Load, Save
- Contains speed slider
- Meets CELL-10, CELL-11, CELL-12, CELL-13, CELL-14, CELL-16

#### 2.3 Controllers
**`SimulationController` (Class)**
```java
public class SimulationController {
    private SimulationDriver driver;
    private SimulationView view;
    private ConfigLoader configLoader;
    
    public void handleStart();
    public void handlePause();
    public void handleLoadFile(File file);
    public void handleSaveFile(File file);
    public void handleSpeedChange(double newSpeed);
}
```
- **Purpose**: Handles user interactions (simplified MVC - no separate controller package needed)
- **Interactions**: 
  - Connects view events to model actions
  - Uses `ConfigLoader` to load/save simulations

### 3. **Configuration Package (`cellsociety.config`)**

#### 3.1 Configuration Loading
**`SimulationConfig` (Class/Record)**
```java
public class SimulationConfig {
    private String simulationType;
    private String title;
    private String author;
    private String description;
    private int rows;
    private int cols;
    private List<List<State>> initialState;
    private Map<String, Double> parameters;
    private Map<String, String> stateColors;
}
```
- **Purpose**: Immutable configuration data (DESIGN-17)
- **Interactions**: Created by `ConfigLoader`, used by `SimulationFactory`

**`ConfigLoader` (Class)**
```java
public class ConfigLoader {
    public SimulationConfig loadConfig(File xmlFile) throws ConfigException;
    public void saveConfig(SimulationConfig config, File xmlFile) throws ConfigException;
}
```
- **Purpose**: Handles XML parsing (DESIGN-10 - only class that knows about XML)
- **Interactions**: 
  - Used by `SimulationController` to load/save
  - Creates `SimulationConfig` from XML
  - Throws custom exceptions for error handling (DESIGN-14)

**`ConfigException` (Custom Exception)**
- Extends `Exception`
- Used for XML parsing errors, invalid configurations
- Meets DESIGN-14 requirement

#### 3.2 Simulation Factory
**`SimulationFactory` (Class)**
```java
public class SimulationFactory {
    public Simulation createSimulation(SimulationConfig config) throws SimulationException;
}
```
- **Purpose**: Creates `Simulation` instances from config
- **Interactions**: 
  - Uses `SimulationConfig` to determine which `GameRules` to create (each concrete rules type is constructed with the appropriate `NeighborStrategy`)
  - Creates `CellGrid` with no strategy—the grid obtains the strategy from the rules during `updateAllCells(rules)` via `rules.getNeighborStrategy()`
  - Returns configured `Simulation`

## Interaction Flow Diagrams

### Loading a Simulation
```
User clicks "Load" 
  → SimulationController.handleLoadFile()
  → ConfigLoader.loadConfig(xmlFile)
  → Returns SimulationConfig
  → SimulationFactory.createSimulation(config)
  → Creates GameRules (each with its NeighborStrategy), CellGrid (no strategy), Simulation
  → SimulationDriver.setSimulation(simulation)
  → SimulationView.updateDisplay(simulation)
  → GridView.render(simulation.getGrid())
```

### Running a Simulation Step
```
SimulationDriver.tick() [called by animation timer]
  → Simulation.step()
  → CellGrid.updateAllCells(rules)
  → For each Cell:
      → CellGrid uses rules.getNeighborStrategy() to get neighbor offsets
      → CellGrid.getCellNeighbors(row, col, rules)  [internal]
      → Cell.updateNextState(neighbors, rules)
      → Rules.getNextState(currentState, neighbors)
  → CellGrid.commitAllStates()
  → SimulationView.updateDisplay()
  → GridView.render(grid)
```

### User Changes Speed
```
User moves speed slider
  → SimulationController.handleSpeedChange(newSpeed)
  → SimulationDriver.setSpeed(newSpeed)
  → Animation timer updates rate
```

## Key Design Patterns Used

1. **Strategy Pattern**: `NeighborStrategy`, `GameRules` - allows swapping behaviors
2. **Factory Pattern**: `SimulationFactory` - creates simulations from config
3. **Observer Pattern**: (Optional) View observes model changes
4. **Template Method**: `GameRules` abstract class with common helper methods
5. **Interface Segregation**: `GridView` interface allows different rendering implementations

## Abstractions Summary (DESIGN-11)

1. **`Cell` Interface** - Abstract cell behavior
2. **`GameRules` Abstract Class** - Abstract rule computation
3. **`NeighborStrategy` Interface** - Abstract neighbor definition
4. **`GridView` Interface** - Abstract rendering
5. **`State` (Interface/Enum)** - Abstract state representation

## Encapsulation Points (DESIGN-10)

1. **Grid Data Structure**: Hidden behind `CellGrid` - no public `getGrid()` returning internal structure
2. **XML Format**: Only `ConfigLoader` knows about XML - model/view are XML-agnostic
3. **Rendering Implementation**: `GridView` interface hides whether using Canvas, GridPane, etc.
4. **JavaFX Dependencies**: Isolated to `cellsociety.view` package only

## Error Handling (DESIGN-13, DESIGN-14)

- `ConfigException` - For XML/config errors
- `SimulationException` - For simulation creation errors
- `InvalidStateException` - For invalid state transitions
- All exceptions provide meaningful messages
- View displays errors to user without crashing

## Testing Considerations (DESIGN-12)

**Model Tests** (JUnit):
- `CellTest` - Test state updates
- `CellGridTest` - Test neighbor finding, grid updates
- `GameRulesTest` - Test each rule implementation
- `SimulationTest` - Test simulation stepping

**End-to-End Tests** (TestFX):
- `SimulationViewTest` - Test UI interactions
- `ConfigLoadTest` - Test loading various XML files
- `SimulationRunTest` - Test full simulation execution

## External Configuration (DESIGN-15)

- **Resource Files**: `src/main/resources/cellsociety/`
  - `Version.properties` - Version info
  - `UIStrings.properties` - All UI text (using ResourceBundle)
  - `styles.css` - UI styling
- **Constants**: Move magic numbers to config files or named constants
