package cellsociety.config;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Abstraction for loading a configuration object from an XML file
 *
 * <p>Callers depend on this interface rather than a concrete class, making it
 * possible to swap implementations (e.g. substitute a mock loader in tests)
 * without changing any calling code
 *
 * <p>Known implementations:
 * <ul>
 *   <li>{@link ConfigLoader} ? loads a single {@code <simulation>} file into a {@link SimulationConfig}</li>
 *   <li>{@link SimulationLoader} ? loads a {@code <simulationState>} session into a {@link SimulationState}</li>
 * </ul>
 *
 * @param <T> the configuration object type produced by this loader
 * @author Ixa Sigler
 */
public interface XmlLoader<T> {

  /**
   * Loads a configuration object from the given XML file.
   *
   * @param file the XML file to read; must not be {@code null}
   * @return the parsed configuration object; never {@code null}
   * @throws IllegalArgumentException     if required fields are missing or malformed
   * @throws ParserConfigurationException if the XML parser cannot be configured
   * @throws IOException                  if the file cannot be read
   * @throws SAXException                 if the file is not valid XML
   */
  T load(File file) throws ParserConfigurationException, IOException, SAXException;
}