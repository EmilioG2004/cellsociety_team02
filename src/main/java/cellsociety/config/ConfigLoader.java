package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static cellsociety.config.ConfigXmlConstants.*;

/**
 * Loads simulation configuration from XML files. Expects a root or nested {@code <simulation>} element
 * with {@code type} and {@code class} attributes, plus {@code <metadata>}, {@code <grid>}, and optional {@code <parameters>}.
 * @author Ixa Sigler
 */
public class ConfigLoader {

  private static final Logger LOG = LogManager.getLogger(ConfigLoader.class);

  private final ConfigGridParser gridParser = new ConfigGridParser();

  /**
   * Loads a simulation config from an XML file on disk.
   * Used by the app when selecting configs from the filesystem.
   */
  public SimulationConfig loadConfig(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {

    try {
      Document xmlDocument = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .parse(xmlFile);

      return loadConfig(xmlDocument);

    } catch (SAXException | IOException e) {
      LOG.error("Failed to parse XML file '{}': {}", xmlFile.getPath(), e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Loads a simulation config from an existing <simulation> element.
   * Useful when configs are embedded inside a larger document or already parsed upstream.
   */
  public SimulationConfig loadConfig(Element simulationElement)
      throws ParserConfigurationException {

    if (simulationElement == null) {
      LOG.error("simulationElement cannot be null");
      throw new IllegalArgumentException("simulationElement cannot be null");
    }
    if (!CONFIG_SIMULATION.equals(simulationElement.getTagName())) {
      LOG.error("Expected <{}> element but got <{}>", CONFIG_SIMULATION, simulationElement.getTagName());
      throw new IllegalArgumentException(
          "Expected <" + CONFIG_SIMULATION + "> element but got <" + simulationElement.getTagName() + ">");
    }

    Document doc = DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .newDocument();

    Element imported = (Element) doc.importNode(simulationElement, true);
    doc.appendChild(imported);

    return loadConfig(doc);
  }

  private SimulationConfig loadConfig(Document xmlDocument) {
    SimulationTypeAndClass simulationTypeAndClass = getSimulationType(xmlDocument);
    String simulationType = simulationTypeAndClass.simulationType();
    String ruleClass = simulationTypeAndClass.ruleClass();
    String neighborStrategyClass = simulationTypeAndClass.neighborStrategyClass();
    String gridEdgeClass = simulationTypeAndClass.gridEdgeClass();

    Element metadata = getMetadata(xmlDocument);
    String title = getChildText(metadata, CONFIG_TITLE);
    String author = getChildText(metadata, CONFIG_AUTHOR);
    String description = getChildText(metadata, CONFIG_DESCRIPTION);

    int[] gridSize = gridParser.parseGridSize(xmlDocument);
    int rows = gridSize[0];
    int cols = gridSize[1];

    int[][] states = gridParser.parseInitialStates(xmlDocument, rows, cols);

    HashMap<String, Double> parameters = getParameters(xmlDocument);

    return new SimulationConfig(
        simulationType,
        ruleClass,
        neighborStrategyClass,
        gridEdgeClass,
        title,
        author,
        description,
        rows,
        cols,
        states,
        parameters
    );
  }

  private Element getFirstElementByTag(Document xmlDocument, String tag) {
    Element root = xmlDocument.getDocumentElement();
    Element element = (Element) root.getElementsByTagName(tag).item(0);
    if (element == null) {
      LOG.error("Missing <{}> element", tag);
      throw new IllegalArgumentException("Missing <" + tag + "> element");
    }
    return element;
  }

  private SimulationTypeAndClass getSimulationType(Document xmlDocument) {
    Element root = xmlDocument.getDocumentElement();
    Element simulation;
    if (root != null && root.getTagName().equals(CONFIG_SIMULATION)) {
      simulation = root;
    } else {
      simulation = getFirstElementByTag(xmlDocument, CONFIG_SIMULATION);
    }

    String typeAttr = simulation.getAttribute(CONFIG_SIM_TYPE_ATTR).trim();
    String classAttr = simulation.getAttribute(CONFIG_SIM_CLASS_ATTR).trim();
    if (typeAttr.isEmpty() || classAttr.isEmpty()) {
      LOG.error("Simulation element must have both 'type' and 'class' attributes (type='{}', class='{}')",
          typeAttr, classAttr);
      throw new IllegalArgumentException(
          "Simulation element must have both 'type' and 'class' attributes (e.g. type=\"GameOfLife\" class=\"cellsociety.Model.GameRules.ConwayGameOfLifeRule\")");
    }

    String neighborStrategyAttr = simulation.getAttribute(CONFIG_NEIGHBOR_STRATEGY_ATTR).trim();
    String gridEdgeAttr = simulation.getAttribute(CONFIG_GRID_EDGE_ATTR).trim();
    return new SimulationTypeAndClass(
        typeAttr,
        classAttr,
        neighborStrategyAttr.isEmpty() ? null : neighborStrategyAttr,
        gridEdgeAttr.isEmpty() ? null : gridEdgeAttr
    );
  }

  private Element getMetadata(Document xmlDocument) {
    return getFirstElementByTag(xmlDocument, CONFIG_METADATA);
  }

  private String getChildText(Element parent, String tagName) {
    Element child = (Element) parent.getElementsByTagName(tagName).item(0);
    if (child == null) return "";
    return child.getTextContent().trim();
  }

  /**
   * Parsed identity + wiring info from the <simulation> tag.
   * Exposed as a simple carrier type for code that wants to reuse this parsing result.
   */
  public record SimulationTypeAndClass(
      String simulationType,
      String ruleClass,
      String neighborStrategyClass,
      String gridEdgeClass
  ) {}

  private HashMap<String, Double> getParameters(Document xmlDocument) {
    HashMap<String, Double> params = new HashMap<>();

    Element root = xmlDocument.getDocumentElement();
    if (root == null) return params;

    Element parametersElement =
        (Element) root.getElementsByTagName(CONFIG_PARAMETERS).item(0);
    if (parametersElement == null) return params;

    var paramNodes = parametersElement.getElementsByTagName(CONFIG_PARAM);
    for (int i = 0; i < paramNodes.getLength(); i++) {
      Element paramElement = (Element) paramNodes.item(i);

      String name = paramElement.getAttribute(CONFIG_PARAM_NAME_ATTR).trim();
      String value = paramElement.getAttribute(CONFIG_PARAM_VALUE_ATTR).trim();

      if (name.isEmpty()) {
        LOG.error("<param> missing 'name' attribute");
        throw new IllegalArgumentException("<param> missing 'name' attribute");
      }
      if (value.isEmpty()) {
        LOG.error("Parameter '{}' missing 'value' attribute", name);
        throw new IllegalArgumentException(
            "Parameter '" + name + "' missing 'value' attribute");
      }

      params.put(name, tryParseParameterValue(value, name));
    }

    return params;
  }

  private double tryParseParameterValue(String value, String name) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      LOG.error("Invalid numeric value for parameter '{}': {}", name, value, e);
      throw new IllegalArgumentException(
          "Invalid numeric value for parameter '" + name + "': " + value
      );
    }
  }
}