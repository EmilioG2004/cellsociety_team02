# Cell Society Design Final
### TEAM NUMBER 2
### Emilio Garcia, Michael Lei, Ixa Sigler


## Team Roles and Responsibilities

 * Emilio Garcia
   * Frontend (View) and Orchestration(Controller)

 * Ixa Sigler
   * Configuration, Simulations

 * Michael Lei
   * Backend, Simulations



## Design goals

#### What Features are Easy to Add

The current design is easiest to extend in places where behavior is already behind an interface or factory-like boundary.

- New simulation types are relatively easy to add. A new simulation mainly needs a new `CellState` implementation or enum, a new `GameRule` subclass, XML config files, and a class reference in the config file. Because `SimulationFactory` builds rules and grids from `SimulationConfig`, most of the controller and view code does not need to change.
- New neighborhood and boundary behaviors are also easy to add. `NeighborStrategy` and `GridEdge` are separate abstractions, and the controller already has runtime support for swapping them across loaded simulations.
- New rendering implementations are possible through `GridView`. The current project uses `GridPaneView`, but the panel and presenter layers depend on the `GridView` contract rather than a specific widget.
- New color schemes, themes, and languages are easier to add than in the original plan because visual color maps are stored outside the model and UI text is centralized in resource bundles and theme helpers.
- New persistence behaviors are easier to add because single-simulation config loading/saving is separated from full program session loading/saving.


## High-level Design

#### Core Classes

The implemented architecture still follows MVC separation, but it ended up with more interface boundaries and service classes than the original design plan.

- `SimulationController` is the top-level controller/orchestrator. It owns global UI wiring, slot count changes, load/save flows, and global updates such as applying neighbor and edge choices to loaded simulations.
- `WiringManager` keeps the main controller thin by attaching all global button behavior in one place.
- `SimulationSlot` is the unit of multi-simulation orchestration. Each slot owns one `SimulationPanel`, one `SimulationPresenter`, and one stored `SimulationConfig` for reset/save behavior.
- `SimulationPresenter` is a lightweight presenter between a render-only panel and a `SimulationDriver`. It refreshes the UI after stepping and tracks population history for the graph dialog.
- `SimulationDriver` and `DefaultSimulationDriver` control execution concerns only: start, pause, speed, direction, forward/backward stepping, and step callbacks.
- `SimulationModel` and its implementation `Simulation` represent one running simulation. They own the current grid, the rule, generation count, undo history, and click-to-toggle behavior.
- The grid layer is split into `ViewGrid`, `ReadOnlyGrid`, `ControllerGrid`, `RuleGrid`, and `Grid`, with `ArrayGrid` as the concrete implementation. This was one of the main refactorings from the original plan. Different parts of the program now depend on the smallest grid API they need.
- `GameRule` defines simulation transition logic and state decoding from config integers. Concrete subclasses such as `ConwayGameOfLifeRule`, `WaTorRule`, `SegregationRule`, `PercolationRule`, `SpreadingOfFireRule`, `FallingSandRule`, `Langton1984Rule`, `LangtonSDSRRule`, `SugarScapeRule`, and `SugarScape2Rule` implement the actual automata.
- `CellState` implementations encode each simulation's state values and click-cycle behavior. This replaced the earlier cell-centered plan with a lighter state-centered model.
- `NeighborStrategy` and `GridEdge` together define neighborhood shape and edge handling. Strategies define offsets, while edges resolve out-of-bounds positions.
- `SimulationConfig` is the immutable snapshot of one simulation's configuration. `SimulationState` is the immutable snapshot of the full program state, including active simulations and preferences.
- `ConfigLoader` and `ConfigSaver` handle one simulation file. `SimulationLoader` and `SimulationSaver` handle full session persistence, including user preferences and registered color maps.
- `SimulationView`, `SimulationPanel`, and `GridView` make up the visible UI. `SimulationView` is the composition root for the main application layout, while each `SimulationPanel` is a lightweight panel for one slot in the dashboard.


## Assumptions that Affect the Design

#### Features Affected by Assumptions

- The controller assumes slot mutation and UI-triggered actions happen on the JavaFX application thread. This affects global controls, slot resizing, loading, and applying runtime neighborhood or edge changes.
- The design assumes cell clicking only cycles the visible simulation state and does not preserve extra per-cell data. This makes click editing simple and generic, but in simulations with richer cell data such as agents or age-like state, toggling a cell resets that extra data.
- The driver assumes the click/update/render sequence completes between scheduled ticks rather than using explicit synchronization. This keeps the runtime simpler, but it affects the guarantees around editing cells while a simulation is running.
- The configuration system assumes XML input is rectangular, valid, and includes fully qualified rule class names. Optional neighbor-strategy and grid-edge class names must also be valid if present. This assumption makes the factory very open for extension, but malformed config metadata fails at load time.
- The view assumes required resource-bundle keys, stylesheets, and a live JavaFX scene/window exist before dialogs and theme changes are used. Dynamic language and theme features depend on those resources being present and correctly named.
- Global controls are assumed to apply uniformly to all currently loaded simulations. That simplifies the dashboard interaction model, but it means speed, direction, outline toggles, and similar controls are intentionally not per-panel features.
- The current dashboard assumes a capped number of simultaneous simulations for readability and performance. This affects how multi-simulation features are presented and why slot management is centralized.


## Significant differences from Original Plan

- The original plan centered around a `Cell` abstraction that stored current and next state and asked rules for the next value. The implemented design removed that layer and instead stores `CellState` objects directly in `ArrayGrid`. Rules now compute an entire next grid from a `RuleGrid`.
- In the original plan, `GameRules` owned the neighbor strategy and the grid asked the rules for it when stepping. In the final implementation, the grid owns the current `NeighborStrategy`, and that strategy owns a `GridEdge`. This change made runtime neighborhood and edge swapping much easier.
- The original plan treated state/color mapping as part of the rule layer. In the implementation, the model only exposes integer state values through `ViewGrid`, and colors are managed externally through `ColorMapProvider` and the view. That keeps display concerns out of the simulation model.
- The original plan described a mostly single-simulation UI with a central grid view and one control panel. The implemented project expanded into a multi-simulation dashboard with up to multiple slots, each with its own panel, renderer, and presenter, while still sharing one set of global controls.
- The controller layer became more decomposed than originally planned. Instead of one controller handling everything directly, the final design uses `WiringManager`, `SlotManager`, dialog/persistence services, error handling, and factories for neighborhood and edge updates.
- The config format also evolved. Instead of only storing simulation type and parameters in a simple XML schema, configs now store the concrete rule class and can optionally store neighbor-strategy and grid-edge classes. Full session files also store application preferences and simulation color maps.
- The final model added features that were not central to the original plan, especially step-back history, runtime direction switching, dynamic cell editing, runtime neighborhood changes, runtime edge changes, and full-program save/restore.


## New Features HowTo

#### Easy to Add Features

Adding a new simulation type:

1. Create a new `CellState` implementation or enum in `src/main/java/cellsociety/Model/CellState` that defines the integer encoding and `getNextInCycle()`.
2. Create a new `GameRule` subclass in `src/main/java/cellsociety/Model/GameRules` that implements `getNextGrid`, `getDefaultNeighborStrategy`, and `fromInt`.
3. Add one or more XML configs under `data/configs/...` using the new rule class name in the `<simulation class="...">` attribute.
4. Add or register a color map for the new simulation type if the default colors are not sufficient.

Adding a new neighborhood shape:

1. Create a new `NeighborStrategy` implementation in `src/main/java/cellsociety/Model/NeighborStrategy`.
2. If it should be selectable from the UI, update `NeighborStrategyFactory` and the corresponding resource-path/constants used by the choice box.
3. If it should be the default for a simulation, return it from that simulation's `GameRule.getDefaultNeighborStrategy()`.

Adding a new edge behavior:

1. Create a new `GridEdge` implementation in `src/main/java/cellsociety/Model/GridEdge`.
2. Register it in `GridEdgeFactory` so the controller can apply it at runtime.
3. Optionally reference it in XML configs through the `gridEdge` attribute so simulations load with that edge policy already selected.

Adding a new grid renderer:

1. Implement `GridView` in `src/main/java/cellsociety/view/gridviews`.
2. Update `SimulationPanel.setGrid` to instantiate the new renderer, or add a small renderer-selection layer if multiple implementations should coexist.
3. Keep the renderer dependent only on `ViewGrid` and external color maps so the MVC separation remains intact.

Adding new persistence-backed UI preferences:

1. Extend `SimulationPreferences`, `SimulationLoader`, and `SimulationSaver`.
2. Apply the preference through the controller/service layer after session load.
3. Keep the preference outside the simulation model unless it changes simulation behavior directly.

#### Other Features not yet Done

* The main feature not implemented was different cell shapes and different views. Both of these are possible but would have required significant refactoring in order to support extensibility of these features with good design.
  * Different view types are partially supported by the existing `GridView` abstraction. In principle, we could add new renderers by creating new `GridView` implementations. In practice, the current interface and panel code are still fairly tied to the idea of rendering a rectangular grid of cells, so supporting very different visualizations cleanly would likely require some refactoring of that API.
  * Different cell shapes such as hexagons or triangles were more difficult because the design is strongly built around row-column grids and offset-based neighbor lookup. Our `NeighborStrategy` implementations work by returning relative coordinate offsets, which works well for square grids but does not generalize cleanly to hexagonal or triangular tilings where valid neighbors may depend on row parity, orientation, or a different coordinate system entirely.
  * Supporting those shapes well would likely require redesigning both the model and the view. On the model side, we would probably want a more general adjacency representation than fixed offsets over a rectangular array. On the view side, `GridPaneView` assumes rectangular cells and row-column placement, so non-rectangular tilings would need a different renderer and likely a less grid-specific rendering contract.
