package cellsociety.config;

import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static cellsociety.config.ConfigXmlConstants.*;

/**
 * Writes a single {@link SimulationConfig} to an XML file.
 * Output root is one <simulation> element (no SimulationState, no settings, no multi-sim list).
 * @author Ixa Sigler
 */
public class ConfigSaver {

  /** Save exactly one simulation config to exactly one file. */
  public void saveConfig(SimulationConfig config, File outFile) {
    if (config == null) throw new IllegalArgumentException("config cannot be null");
    if (outFile == null) throw new IllegalArgumentException("outFile cannot be null");

    validateConfig(config);

    try {
      Document doc = newDocument();
      Element simulationEl = buildSimulationElement(doc, config);
      doc.appendChild(simulationEl);
      writeXml(doc, outFile);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save config to " + outFile.getPath(), e);
    }
  }

  private Document newDocument() throws Exception {
    return DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .newDocument();
  }

  public Element buildSimulationElement(Document doc, SimulationConfig config) {
    Element simulationEl = doc.createElement(CONFIG_SIMULATION);
    simulationEl.setAttribute(CONFIG_SIM_TYPE_ATTR, trimmed(config.getSimulationType()));
    simulationEl.setAttribute(CONFIG_SIM_CLASS_ATTR, trimmed(config.getRuleClass()));

    simulationEl.appendChild(buildMetadataElement(doc, config));
    simulationEl.appendChild(buildGridElement(doc, config));
    appendParametersElementIfAny(doc, simulationEl, config);

    return simulationEl;
  }

  private Element buildMetadataElement(Document doc, SimulationConfig config) {
    Element metadataEl = doc.createElement(CONFIG_METADATA);
    metadataEl.appendChild(textElement(doc, CONFIG_TITLE, config.getTitle()));
    metadataEl.appendChild(textElement(doc, CONFIG_AUTHOR, config.getAuthor()));
    metadataEl.appendChild(textElement(doc, CONFIG_DESCRIPTION, config.getDescription()));
    return metadataEl;
  }

  private Element buildGridElement(Document doc, SimulationConfig config) {
    Element gridEl = doc.createElement(CONFIG_GRID);
    gridEl.setAttribute(CONFIG_ROWS, Integer.toString(config.getRows()));
    gridEl.setAttribute(CONFIG_COLUMNS, Integer.toString(config.getCols()));

    Element statesEl = doc.createElement(CONFIG_STATES);
    statesEl.setTextContent(toMultilineCsv(config.getInitialStates(), config.getRows(), config.getCols()));
    gridEl.appendChild(statesEl);

    return gridEl;
  }

  private void appendParametersElementIfAny(Document doc, Element simulationEl, SimulationConfig config) {
    Map<String, Double> params = config.getParameters();
    if (params == null || params.isEmpty()) return;

    Element parametersEl = doc.createElement(CONFIG_PARAMETERS);
    for (Map.Entry<String, Double> entry : params.entrySet()) {
      parametersEl.appendChild(buildParamElement(doc, entry.getKey(), String.valueOf(entry.getValue())));
    }
    simulationEl.appendChild(parametersEl);
  }

  private Element buildParamElement(Document doc, String name, String value) {
    String n = trimmed(name);
    String v = trimmed(value);

    if (n.isEmpty()) throw new IllegalArgumentException("Parameter has blank name");
    if (v.isEmpty()) throw new IllegalArgumentException("Parameter '" + n + "' has blank value");

    Element paramEl = doc.createElement(CONFIG_PARAM);
    paramEl.setAttribute(CONFIG_PARAM_NAME_ATTR, n);
    paramEl.setAttribute(CONFIG_PARAM_VALUE_ATTR, v);
    return paramEl;
  }

  private Element textElement(Document doc, String tag, String text) {
    Element el = doc.createElement(tag);
    el.setTextContent(trimmed(text));
    return el;
  }

  private void writeXml(Document doc, File outFile) throws Exception {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(outFile));
  }

  private void validateConfig(SimulationConfig config) {
    if (trimmed(config.getSimulationType()).isEmpty())
      throw new IllegalArgumentException("simulation type is blank");
    if (trimmed(config.getRuleClass()).isEmpty())
      throw new IllegalArgumentException("rule class is blank");

    int rows = config.getRows();
    int cols = config.getCols();
    if (rows <= 0 || cols <= 0)
      throw new IllegalArgumentException("rows/cols must be positive");

    int[][] states = config.getInitialStates();
    if (states == null || states.length != rows)
      throw new IllegalArgumentException("initialStates row count does not match rows");

    for (int r = 0; r < rows; r++) {
      if (states[r] == null || states[r].length != cols)
        throw new IllegalArgumentException("initialStates col count does not match cols at row " + r);
    }
  }

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

  private String trimmed(String s) {
    return s == null ? "" : s.trim();
  }
}