# cell society
## TEAM NUMBER
## NAMES


This project implements a cellular automata simulator.

### Timeline

 * Start Date: 

 * Finish Date: 

 * Hours Spent:



### Attributions

 * Resources used for learning (including AI assistance)
 
 * Resources used directly (including AI assistance)


### Running the Program

 * Main class:

 * Data files needed: 

 * Interesting data files:

 * Key/Mouse inputs:



### Notes/Assumptions

 * Assumptions or Simplifications
 * We support changing a cell state by clicking while the sim is running, but we assume that the full sequence (user clicks a cell in the view, the presenter calls the driver to update the cell state in the model, and the presenter triggers a re-render, for example the renderer re-rendering the grid) completes between two step-driven renders, that is between successive ticks of the sim driver’s scheduler. We do not explicitly synchronize click handling with the driver’s tick. In practice we rely on this happening within one step interval.
 * Click to toggle cell state is implemented by cycling the cell to the next state in that simulation’s cycle (for example dead, alive, dead). As a simplification, we do not preserve other cell data when toggling. Any rule-specific or cell-specific data (for example agent, age, sugar) is lost or reset. Only the state in the cycle changes.

 * Known Bugs:

 * Features implemented:

 * Features unimplemented:

 * Noteworthy Features
 * Full support for advancing a simulation in both directions, forward and backward, either by single stepping or by driving.
 * Dynamic cell state update by clicking. Users can click a cell to cycle it to the next state in that simulation’s cycle (dead to alive to dead). They can effectively modify the starting state of any simulation by clicking cells to change their state before pressing start. It can be done both when the sim is paused and when it is running. (See Assumptions for the simplification that cell data beyond the cycle state is lost when toggling.)

### Design Decisions

**Grid interfaces**
We introduce separate interfaces so each package receives only what it needs, reducing coupling and keeping boundaries clear.
 * **ViewGrid**. View depends only on ViewGrid (dimensions and int per cell), not on ReadOnlyGrid or CellState, so rendering stays independent of rule-specific types.
 * **ControllerGrid**. Extends ViewGrid with neighbor strategy get/set so the controller can change neighborhood at runtime without touching rule code.
 * **RuleGrid**. Rules take RuleGrid (read-only plus emptyCopy/deepCopy) and return Grid. They never see mutation or strategy-setting.

**NeighborStrategy and GridEdge**
 * The grid owns a NeighborStrategy (neighborhood shape), and each NeighborStrategy owns a GridEdge (boundary behavior). The strategy uses the GridEdge interface to resolve out-of-bounds neighbor positions. Separate interfaces let shape and edge be chosen and combined independently.

**GameRule abstraction**
 * Initially GameRule did too much. It handled how neighbors were found, how boundaries worked, how the grid was laid out, what the “next” state was when the user clicked a cell, how states became integers for the view, which neighbor strategy was in use, and the colors for each cell. That made rules hard to test in isolation, tied every rule to one way of defining neighbors and edges, and mixed display, neighbor, and grid concerns into the place that should only define transition logic.
 * We split responsibilities so that the rule only defines the simulation’s transition law. Neighborhood shape and boundary behavior live in the strategy and grid edge. The grid owns structure and exposes neighbor queries. Cell states define their own integer encoding and their place in the click-to-cycle order. The rule receives a read-only view of the grid plus the ability to create a copy for the next generation, and returns that next grid. It never holds or chooses the neighbor strategy (the grid carries it), so we can change neighborhood or boundary without touching any rule. That keeps rules focused and reusable across different grid setups.
 * In the end, GameRule is responsible only for simulation parameters, mapping config state codes to cell states when the grid is built, providing a default neighbor strategy when the config does not specify one, and computing the next generation from the current grid using the grid’s own neighbor and copy operations. Transition logic stays in one place and does not reimplement how neighbors or edges work.

**Simulation driver, model, and factory**
 * **Simulation driver**. Owns the run loop (start/pause, speed, direction, step/stepBack, and the current simulation instance). The view/controller talk to the driver to control playback. We introduce an interface so this boundary can be swapped or tested in isolation.
 * **Simulation model**. Represents a single simulation (grid, rule, generation count, and operations like step, stepBack, toggleCellState, setState). The driver calls the model to advance, and the controller gets grid views from it. We introduce an interface so this boundary can be swapped or tested in isolation.
 * **Simulation factory**. Builds a simulation from a config (creates the rule and grid and uses them to construct a SimulationModel). Callers use it only to create a sim from config. We do not introduce an interface. SimulationFactory is a plain class with one static method, createSimulation(SimulationConfig). The factory is an implementation detail, so we avoided an extra abstraction layer.

**Colors outside model**
 * All color maps were removed from the model. Cell states only define an int they map to. The view (and preferences/config) map ints to colors. This keeps the model free of display concerns and lets users change colors without touching rule or state code.

**Simulation logic in model, view separate**
 * All simulation-driving logic (stepping, undo, applying the rule, grid updates) lives in the model package. The sim controller gets a remote-control-style interface to the model. It triggers actions but does not hold or execute the logic. The view only observes the current grid and renders it.

**UI and multi-simulation**
 * Multi-simulation dashboard is intentionally capped at three simultaneous simulations for UI simplicity, legibility, and performance on typical laptop screens. This keeps each panel large enough to be useful and reduces visual clutter.
 * Multi-simulation controls are routed through the existing top control bar to avoid duplicating UI controls in each panel and to maintain a consistent interaction model.
 * The multi-simulation view uses render-only panels (no per-panel controls beyond loading) to separate visualization from control logic and minimize UI complexity.



### Assignment Impressions

