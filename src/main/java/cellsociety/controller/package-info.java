/**
 * Controller and presenter classes that coordinate UI events with simulation logic.
 *
 * <p>Purpose: translate user actions into model updates and refresh the view; provide
 * specialized presenters for multi-simulation dashboards.</p>
 *
 * <p>Assumptions: views are fully constructed before wiring, and simulations are loaded
 * via valid configuration files.</p>
 *
 * <p>Dependencies: {@code cellsociety.view} components, {@code cellsociety.Model} simulation
 * drivers and factories, and JavaFX threading utilities.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimulationView view = new SimulationView(bundle);
 * Scene scene = new Scene(view.getRoot());
 * SimulationController controller = new SimulationController(view, scene);
 * }</pre>
 *
 * @author Emilio Garcia
 */
package cellsociety.controller;
