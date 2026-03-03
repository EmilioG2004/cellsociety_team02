/**
 * Model API for cellular automata simulations.
 *
 * <h2>Logical parts and their APIs</h2>
 *
 * <ul>
 *   <li><b>Grid</b> ({@link cellsociety.Model.Grid}) – Storage and neighbor lookup.
 *       View uses {@link cellsociety.Model.Grid.ViewGrid}; rules use
 *       {@link cellsociety.Model.Grid.ReadOnlyGrid} / {@link cellsociety.Model.Grid.Grid}.</li>
 *   <li><b>Simulation</b> ({@link cellsociety.Model.Simulation}) – One run: grid + rule, step/undo.
 *       API: {@link cellsociety.Model.Simulation.model.SimulationModel}; creation:
 *       {@link cellsociety.Model.Simulation.factory.SimulationFactory}; control:
 *       {@link cellsociety.Model.Simulation.driver.SimulationDriver}.</li>
 *   <li><b>Game rules</b> ({@link cellsociety.Model.GameRules}) – Transition logic.
 *       {@link cellsociety.Model.GameRules.GameRule} defines the contract; grid creation uses
 *       {@link cellsociety.Model.GameRules.CellFactory} for state creation.</li>
 *   <li><b>Cell state</b> ({@link cellsociety.Model.CellState}) – Per-cell state.
 *       {@link cellsociety.Model.CellState.CellState} is the marker interface; rules define concrete states.</li>
 *   <li><b>Neighbor strategy</b> ({@link cellsociety.Model.NeighborStrategy}) – Who counts as a neighbor.
 *       {@link cellsociety.Model.NeighborStrategy.NeighborStrategy} defines the contract; grid holds one strategy.</li>
 * </ul>
 *
 * <p>External code (controller, view) should depend on these interfaces and the factory; concrete
 * grid/simulation types are implementation details.</p>
 */
package cellsociety.Model;
