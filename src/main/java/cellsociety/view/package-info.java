/**
 * View-layer components for the Cellular Automata simulator.
 *
 * <p>Purpose: provides JavaFX UI elements that render simulations, expose controls, and
 * present dialogs/splashes without embedding simulation logic.</p>
 *
 * <p>Assumptions: the JavaFX application thread is initialized before any view is created,
 * and controller code is responsible for supplying valid models and callbacks.</p>
 *
 * <p>Dependencies: JavaFX UI toolkit, {@code cellsociety.Model} read-only grid access,
 * and {@code cellsociety.controller} presenters/controllers.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ResourceBundle bundle = ResourceBundle.getBundle("cellsociety.languages.English");
 * SimulationView view = new SimulationView(bundle);
 * Scene scene = new Scene(view.getRoot());
 * stage.setScene(scene);
 * }</pre>
 *
 * @author Emilio Garcia
 * @author Michael Lei
 */
package cellsociety.view;
