package cellsociety.config;

import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static cellsociety.config.ConfigXmlConstants.*;

/**
 * Writes a single {@link SimulationConfig} to an XML file.
 * Output root is one <simulation> element (no SimulationState, no settings, no multi-sim list).
 * @author Ixa Sigler
 */
public class ConfigSaver implements XmlSaver<SimulationConfig> {

  private static final Logger LOG = LogManager.getLogger(ConfigSaver.class);

  /**
   * Writes one simulation config to an XML file.
   * This is typically used by export/save features to persist the current simulation setup.
   */
  public void saveConfig(SimulationConfig config, File outFile) {
    if (config == null) {
      LOG.error("config cannot be null");
      throw new IllegalArgumentException("config cannot be null");
    }
    if (outFile == null) {
      LOG.error("outFile cannot be null");
      throw new IllegalArgumentException("outFile cannot be null");
    }

    validateConfig(config);

    try {
      // Create a new DOM document and build the <simulation> element tree inside it
      Document doc = newDocument();
      Element simulationEl = buildSimulationElement(doc, config);
      doc.appendChild(simulationEl);

      // Serialize the DOM tree to disk
      writeXml(doc, outFile);
    } catch (Exception e) {
      LOG.error("Failed to save config to {}", outFile.getPath(), e);
      throw new RuntimeException("Failed to save config to " + outFile.getPath(), e);
    }
  }

  /**
   * Implements {@link XmlSaver#save(Object, File)} by delegating to {@link #saveConfig(SimulationConfig, File)}.
   * Preserves the existing method name for teammates while satisfying the interface contract.
   */
  @Override
  public void save(SimulationConfig config, File outFile) {
    saveConfig(config, outFile);
  }

  /** Creates a blank XML DOM document used as the output container. */
  private Document newDocument() throws Exception {
    return DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .newDocument();
  }

  /**
   * Builds the full <simulation> element representing the configuration.
   * Exposed so other systems can embed this element into larger XML structures if needed.
   */
  public Element buildSimulationElement(Document doc, SimulationConfig config) {
    Element simulationEl = doc.createElement(CONFIG_SIMULATION);

    // Required attributes describing the simulation implementation
    simulationEl.setAttribute(CONFIG_SIM_TYPE_ATTR, trimmed(config.getSimulationType()));
    simulationEl.setAttribute(CONFIG_SIM_CLASS_ATTR, trimmed(config.getRuleClass()));

    // Optional attributes that customize grid behavior
    if (config.getNeighborStrategyClass() != null && !config.getNeighborStrategyClass().isBlank()) {
      simulationEl.setAttribute(CONFIG_NEIGHBOR_STRATEGY_ATTR, trimmed(config.getNeighborStrategyClass()));
    }
    if (config.getGridEdgeClass() != null && !config.getGridEdgeClass().isBlank()) {
      simulationEl.setAttribute(CONFIG_GRID_EDGE_ATTR, trimmed(config.getGridEdgeClass()));
    }

    simulationEl.appendChild(buildMetadataElement(doc, config));
    simulationEl.appendChild(buildGridElement(doc, config));
    appendParametersElementIfAny(doc, simulationEl, config);

    return simulationEl;
  }

  /** Creates the <metadata> block containing title, author, and description. */
  private Element buildMetadataElement(Document doc, SimulationConfig config) {
    Element metadataEl = doc.createElement(CONFIG_METADATA);
    metadataEl.appendChild(textElement(doc, CONFIG_TITLE, config.getTitle()));
    metadataEl.appendChild(textElement(doc, CONFIG_AUTHOR, config.getAuthor()));
    metadataEl.appendChild(textElement(doc, CONFIG_DESCRIPTION, config.getDescription()));
    return metadataEl;
  }

  /**
   * Creates the <grid> element including size attributes and the serialized state grid.
   */
  private Element buildGridElement(Document doc, SimulationConfig config) {
    Element gridEl = doc.createElement(CONFIG_GRID);
    gridEl.setAttribute(CONFIG_ROWS, Integer.toString(config.getRows()));
    gridEl.setAttribute(CONFIG_COLUMNS, Integer.toString(config.getCols()));

    Element statesEl = doc.createElement(CONFIG_STATES);

    // Store the grid as a newline-separated CSV block (same format the loader expects)
    statesEl.setTextContent(toMultilineCsv(
        config.getInitialStates(),
        config.getRows(),
        config.getCols()
    ));

    gridEl.appendChild(statesEl);
    return gridEl;
  }

  /**
   * Adds the <parameters> block if the simulation defines runtime parameters.
   */
  private void appendParametersElementIfAny(Document doc, Element simulationEl, SimulationConfig config) {
    Map<String, Double> params = config.getParameters();
    if (params == null || params.isEmpty()) return;

    Element parametersEl = doc.createElement(CONFIG_PARAMETERS);

    for (Map.Entry<String, Double> entry : params.entrySet()) {
      parametersEl.appendChild(
          buildParamElement(doc, entry.getKey(), String.valueOf(entry.getValue()))
      );
    }

    simulationEl.appendChild(parametersEl);
  }

  /** Builds a single <param name="..." value="..."/> entry. */
  private Element buildParamElement(Document doc, String name, String value) {
    String n = trimmed(name);
    String v = trimmed(value);

    if (n.isEmpty()) {
      LOG.error("Parameter has blank name");
      throw new IllegalArgumentException("Parameter has blank name");
    }
    if (v.isEmpty()) {
      LOG.error("Parameter '{}' has blank value", n);
      throw new IllegalArgumentException("Parameter '" + n + "' has blank value");
    }

    Element paramEl = doc.createElement(CONFIG_PARAM);
    paramEl.setAttribute(CONFIG_PARAM_NAME_ATTR, n);
    paramEl.setAttribute(CONFIG_PARAM_VALUE_ATTR, v);
    return paramEl;
  }

  /** Utility for creating simple text child elements. */
  private Element textElement(Document doc, String tag, String text) {
    Element el = doc.createElement(tag);
    el.setTextContent(trimmed(text));
    return el;
  }

  /**
   * Serializes the DOM tree to disk using a Transformer.
   * INDENT is enabled so the resulting XML is human-readable.
   */
  private void writeXml(Document doc, File outFile) throws Exception {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(outFile));
  }

  /**
   * Validates that the config is structurally consistent before writing.
   * Prevents producing malformed XML (e.g., mismatched grid sizes).
   */
  private void validateConfig(SimulationConfig config) {
    if (trimmed(config.getSimulationType()).isEmpty()) {
      LOG.error("simulation type is blank");
      throw new IllegalArgumentException("simulation type is blank");
    }

    if (trimmed(config.getRuleClass()).isEmpty()) {
      LOG.error("rule class is blank");
      throw new IllegalArgumentException("rule class is blank");
    }

    int rows = config.getRows();
    int cols = config.getCols();
    if (rows <= 0 || cols <= 0) {
      LOG.error("rows/cols must be positive (rows={}, cols={})", rows, cols);
      throw new IllegalArgumentException("rows/cols must be positive");
    }

    int[][] states = config.getInitialStates();
    if (states == null || states.length != rows) {
      LOG.error("initialStates row count does not match rows (rows={}, actual={})",
          rows, (states == null ? "null" : states.length));
      throw new IllegalArgumentException("initialStates row count does not match rows");
    }

    for (int r = 0; r < rows; r++) {
      if (states[r] == null || states[r].length != cols) {
        LOG.error("initialStates col count does not match cols at row {} (cols={}, actual={})",
            r, cols, (states[r] == null ? "null" : states[r].length));
        throw new IllegalArgumentException(
            "initialStates col count does not match cols at row " + r
        );
      }
    }
  }

  /**
   * Converts the grid to the CSV format used in config files:
   * one row per line with comma-separated state values.
   */
  private String toMultilineCsv(int[][] states, int rows, int cols) {
    StringBuilder sb = new StringBuilder();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (c > 0) sb.append(",");
        sb.append(states[r][c]);
      }
      if (r < rows - 1) sb.append("\n");
    }

    return sb.toString();
  }

  /** Null-safe trim helper used when writing XML values. */
  private String trimmed(String s) {
    return s == null ? "" : s.trim();
  }
}