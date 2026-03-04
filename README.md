# cell society
## TEAM NUMBER
## NAMES


This project implements a cellular automata simulator.

### Timeline

 * Start Date: 02/03

 * Finish Date: 03/04

 * Hours Spent: Combined 100+



### Attributions

 * Resources used for learning (including AI assistance)
   * ChatGPT and LLMs were used to explain difficult concepts, as well as to help explain basics and syntax for new knowledge points, specifically TestFX and OpenJFX syntax and functions and possibilities.
 
 * Resources used directly (including AI assistance)
   * LLMs were used to help write comments and documentation. Also used where useful to apply simple, repetitive boilerplate (for example, in resourceBundle files to provide translations)


### Running the Program

 * Main class: properties files to create ResourceBundles, as main must run with a resourcebundle.

 * Data files needed: ConfigXML files, Resource property files

 * Interesting data files: 

 * Key/Mouse inputs: Only clicks. Click to do everything in this simulation, start it, set simulation, and control simulations. All interaction is handled through buttons for ease of use.



### Notes/Assumptions

 * Assumptions or Simplifications
 * We support changing a cell state by clicking while the sim is running, but we assume that the full sequence (user clicks a cell in the view, the presenter calls the driver to update the cell state in the model, and the presenter triggers a re-render, for example the renderer re-rendering the grid) completes between two step-driven renders, that is between successive ticks of the sim driver’s scheduler. We do not explicitly synchronize click handling with the driver’s tick. In practice we rely on this happening within one step interval.
 * Click to toggle cell state is implemented by cycling the cell to the next state in that simulation’s cycle (for example dead, alive, dead). As a simplification, we do not preserve other cell data when toggling. Any rule-specific or cell-specific data (for example agent, age, sugar) is lost or reset. Only the state in the cycle changes.
 * Controller assumptions: controller services assume all slot-mutating actions execute on the JavaFX application thread, slot indexes are validated before service calls, and global controls (speed, direction, strategy, edge) apply uniformly to currently loaded simulations.
 * View assumptions: view classes assume required `ResourceBundle` keys and stylesheet resources exist, a JavaFX `Scene`/`Window` is available before file dialogs are shown, and grid renderers receive dimensions/state matrices that match their initialized size.

 * Known Bugs: When uploading a simulation to a slot, and then changing some value like speed or rotations, or neighborhood strategy, despite being global controls adding a new simulation into a slot will not carry over all of those properties. In some cases, like with speed, the speed will not change at all unless brought back in the opposite direction of whatever the original change was (ie speeding up simulations then adding a new one will not change simulation speed unless slowing down). Also not all labels and buttons and ndoes have proper resourcebundle strings for dynamic updates.

 * Features implemented: all core features except those unimplemented. Grid Resetting, Loading Full SimulationState, StepSimulation, Grid Outline, Flip Grid, GridStaes, GraphView of Pop Changes, Dynamic Cell Neighborhood Updates, Dynamic GridEdge Updates, Dynamic Language/Theme Updates (Cell-51X Custom). Extended moore and VonNeumann neighbors, as well as cylindrical, mirrored, and toroidal edge types. All simulations except foraging ants, generalizedgameoflife, rock paper scissors. All extensions implemented except those expressly stated in features implemented.

 * Features unimplemented: Cell Shape Tilings (variation 2) and missing two cellviews (variation2).

 * Noteworthy Features
 * Full support for advancing a simulation in both directions, forward and backward, either by single stepping or by driving.
 * Dynamic cell state update by clicking. Users can click a cell to cycle it to the next state in that simulation’s cycle (dead to alive to dead). They can effectively modify the starting state of any simulation by clicking cells to change their state before pressing start. It can be done both when the sim is paused and when it is running. (See Assumptions for the simplification that cell data beyond the cycle state is lost when toggling.)
 * Simulations can be fully saved and restored using session files that store both user preferences and all active simulations.

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
 * Multi-simulation dashboard is intentionally capped at nine simultaneous simulations for UI simplicity, legibility, and performance on typical laptop screens. This keeps each panel large enough to be useful and reduces visual clutter.
 * Multi-simulation controls are routed through the existing top control bar to avoid duplicating UI controls in each panel and to maintain a consistent interaction model.
 * The multi-simulation view uses render-only panels (no per-panel controls beyond loading) to separate visualization from control logic and minimize UI complexity.

**Controller design**
 * The controller is split into a thin orchestrator (`SimulationController`) and focused services (`SimulationLoadingService`, `SimulationSavingService`, `SimulationDialogService`, strategy/edge factories, and slot manager) so loading, persistence, dialog flows, and runtime updates can evolve independently.
 * Slot lifecycle and per-slot wiring are centralized in `SlotManager`, while global controls are wired once through `WiringManager`, which keeps multi-slot behavior consistent and avoids duplicated callback setup.
 * Error presentation is centralized through `ErrorHandler`, so service exceptions map to user-facing alerts in one place instead of being handled ad hoc across controller methods.

**View design**
 * `SimulationView` is a composition root for layout only; control creation/text localization is delegated to `ButtonManager`, and styling/language mapping is delegated to `ThemeApplier`/`LanguageApplier`.
 * Grid rendering is abstracted behind `GridView`; `GridPaneView` is one implementation, which keeps renderer choice decoupled from controllers and panel composition.
 * Multi-sim rendering uses lightweight `SimulationPanel` components implementing `SimulationRenderer`, so panels stay focused on display/callback hooks while controllers own behavior and simulation state transitions.



### Assignment Impressions
