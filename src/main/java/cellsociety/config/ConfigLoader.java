package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import static cellsociety.config.ConfigXmlConstants.*;

public class ConfigLoader {

  private final ConfigGridParser gridParser = new ConfigGridParser();

  public SimulationConfig loadConfig(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {

    Document xmlDocument = DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse(xmlFile);

    String simulationType = getSimulationType(xmlDocument);

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
      throw new IllegalArgumentException("Missing <" + tag + "> element");
    }
    return element;
  }

  private String getSimulationType(Document xmlDocument) {
    Element root = xmlDocument.getDocumentElement();

    Element simulation;
    if (root != null && root.getTagName().equals(CONFIG_SIMULATION)) {
      simulation = root;
    } else {
      simulation = getFirstElementByTag(xmlDocument, CONFIG_SIMULATION);
    }

    String typeAttr = simulation.getAttribute(CONFIG_SIM_TYPE_ATTR);
    if (!typeAttr.isBlank()) {
      return typeAttr.trim();
    }

    // if no attributes try plaintext
    String text = simulation.getTextContent();
    if (text != null && !text.isBlank()) {
      return text.trim();
    }

    throw new IllegalArgumentException("Simulation type not specified");
  }

  private Element getMetadata(Document xmlDocument) {
    return getFirstElementByTag(xmlDocument, CONFIG_METADATA);
  }

  private String getChildText(Element parent, String tagName) {
    Element child = (Element) parent.getElementsByTagName(tagName).item(0);
    if (child == null) return "";
    return child.getTextContent().trim();
  }


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
        throw new IllegalArgumentException("<param> missing 'name' attribute");
      }
      if (value.isEmpty()) {
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
      throw new IllegalArgumentException(
          "Invalid numeric value for parameter '" + name + "': " + value
      );
    }
  }
}
