/**
 * XML configuration API for Cell Society simulations.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li><b>Hide XML details</b>: only this package knows the XML format; other code never touches DOM or tags.</li>
 *   <li><b>Stable APIs</b>: View/Model use loader/saver APIs and config/state objects, not XML elements or grid internals.</li>
 *   <li><b>Enable reflection</b>: class names are stored as Strings so factories can instantiate via reflection (no hardcoded maps).</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li><b>Validation + exceptions</b>: loaders validate required fields and throw exceptions with clear messages on malformed input.</li>
 *   <li><b>No null signaling</b>: errors use exception flows; callers decide how to report them to users.</li>
 *   <li><b>Encapsulation</b>: returned objects do not expose mutable internal representations (e.g., grid backing structures).</li>
 *   <li><b>Portability</b>: preferences like color maps are stored as strings (e.g., {@code "#RRGGBB"}), not UI types.</li>
 * </ul>
 *
 * <h2>API</h2>
 * <ul>
 *   <li>{@link cellsociety.config.ConfigLoader}/{@link cellsociety.config.ConfigSaver}: read/write single {@code <simulation>} files.</li>
 *   <li>{@link cellsociety.config.ConfigGridParser}: builds initial grid states from {@code <states>}, {@code <randomByCount>}, and {@code <pattern>}.</li>
 *   <li>{@link cellsociety.config.SimulationLoader}/{@link cellsociety.config.SimulationSaver}: read/write {@code <simulationState>} session files.</li>
 * </ul>
 *
 * @author Ixa Sigler
 */
package cellsociety.config;