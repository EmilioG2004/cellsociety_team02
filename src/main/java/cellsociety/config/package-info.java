/**
 * XML configuration API for Cell Society simulations.
 *
 * <h2>Design Goals</h2>
 * <ul>
 *   <li><b>Hide XML details</b>: only this package knows the XML format; no other package
 *       ever touches DOM nodes, tag name strings, or XML attributes directly (DESIGN-10).</li>
 *   <li><b>Stable APIs</b>: the View and Model interact exclusively with the loader/saver APIs
 *       and the immutable data objects ({@link cellsociety.config.SimulationConfig},
 *       {@link cellsociety.config.SimulationState}, {@link cellsociety.config.SimulationPreferences}).
 *       Internal XML constants are package-private (DESIGN-18).</li>
 *   <li><b>Enable reflection</b>: rule-class and strategy-class names are stored as Strings so
 *       factories can instantiate them via reflection with no hardcoded conditional maps
 *       (DESIGN-20).</li>
 *   <li><b>Model?View separation</b>: this package contains no JavaFX imports; color values
 *       are stored as portable hex strings (e.g. {@code "#RRGGBB"}) and resolved to UI types
 *       only by the View layer (DESIGN-09).</li>
 *   <li><b>Explicit immutability</b>: all returned data objects ({@code SimulationConfig},
 *       {@code SimulationState}, {@code SimulationPreferences}) are {@code final} with no
 *       setters and defensive copies of mutable fields (DESIGN-17).</li>
 * </ul>
 *
 * <h2>Design Patterns</h2>
 * <ul>
 *   <li><b>Strategy</b>: {@link cellsociety.config.ConfigGridParser} is a strategy object
 *       injected into {@link cellsociety.config.ConfigLoader}, isolating grid-parsing
 *       logic so alternative parsers can be substituted (DESIGN-19).</li>
 *   <li><b>Façade</b>: {@link cellsociety.config.ConfigLoader} and
 *       {@link cellsociety.config.SimulationLoader} act as façades that hide DOM parsing,
 *       validation, and object construction behind a single method call.</li>
 *   <li><b>Registry</b>: {@link cellsociety.config.ColorMapProvider} is a stateful registry
 *       with only static methods, providing a single shared source of truth for color
 *       mappings across the application.</li>
 * </ul>
 *
 * <h2>Contracts</h2>
 * <ul>
 *   <li><b>Validation + exceptions</b>: every loader validates required fields and throws
 *       {@link java.lang.IllegalArgumentException} (or propagates {@link org.xml.sax.SAXException}
 *       / {@link java.io.IOException}) with clear messages on malformed input. Callers are
 *       responsible for surfacing these to users (DESIGN-14).</li>
 *   <li><b>No null signaling</b>: {@code null} is never returned as an error signal; all
 *       error flows use exceptions (DESIGN-14).</li>
 *   <li><b>Encapsulation of internals</b>: returned objects expose no mutable internal
 *       representations (e.g. grid arrays are deep-copied on read) (DESIGN-10).</li>
 *   <li><b>Portability</b>: color maps use hex strings, not JavaFX {@code Color} objects,
 *       so this package has no UI-framework dependency (DESIGN-09).</li>
 *   <li><b>Logging</b>: all exceptions are logged via Log4j2 before being thrown;
 *       no {@code System.out.println} or {@code printStackTrace} calls exist in this
 *       package (DESIGN-21).</li>
 * </ul>
 *
 * <h2>Public API Summary</h2>
 * <ul>
 *   <li>{@link cellsociety.config.ConfigLoader} /
 *       {@link cellsociety.config.ConfigSaver}: read/write a single {@code <simulation>}
 *       config file, producing/consuming a {@link cellsociety.config.SimulationConfig}.</li>
 *   <li>{@link cellsociety.config.ConfigGridParser}: parses the {@code <states>},
 *       {@code <randomByCount>}, and {@code <pattern>} sections of a grid element into an
 *       {@code int[][]} initial state matrix.</li>
 *   <li>{@link cellsociety.config.SimulationLoader} /
 *       {@link cellsociety.config.SimulationSaver}: read/write a full session file
 *       ({@code <simulationState>}), producing/consuming a
 *       {@link cellsociety.config.SimulationState} that bundles preferences and all open
 *       simulations.</li>
 *   <li>{@link cellsociety.config.ColorMapProvider}: registry mapping simulation types to
 *       {@code state ? hexColor} tables; used by the View layer to render cells.</li>
 * </ul>
 *
 * @author Ixa Sigler
 */
package cellsociety.config;