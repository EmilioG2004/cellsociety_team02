package cellsociety.config;

import java.io.File;

/**
 * Abstraction for writing a configuration object to an XML file
 *
 * <p>Callers depend on this interface rather than a concrete class, making it
 * possible to swap implementations (e.g. substitute a mock saver in tests)
 * without changing any calling code
 *
 * <p>Known implementations:
 * <ul>
 *   <li>{@link ConfigSaver} ? writes a {@link SimulationConfig} as a {@code <simulation>} file</li>
 *   <li>{@link SimulationSaver} ? writes a {@link SimulationState} as a {@code <simulationState>} file</li>
 * </ul>
 *
 * @param <T> the configuration object type consumed by this saver
 * @author Ixa Sigler
 */
public interface XmlSaver<T> {

  /**
   * Writes the given configuration object to the specified XML file.
   *
   * @param config  the configuration object to serialize; must not be {@code null}
   * @param outFile the destination file; must not be {@code null}
   * @throws IllegalArgumentException if {@code config} or {@code outFile} is {@code null},
   *                                  or if the config contains invalid data
   * @throws RuntimeException         if the file cannot be written
   */
  void save(T config, File outFile);
}