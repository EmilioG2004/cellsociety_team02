package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import static cellsociety.config.ConfigXmlConstants.*;


public class ConfigLoader {

  public SimulationConfig loadConfig(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
    Document xmlDocument = DocumentBuilderFactory
      .newInstance()
      .newDocumentBuilder()
      .parse(xmlFile);

    String simulationType = getSimulationType(xmlDocument);

    Element metadata = getMetadata(xmlDocument);
    String title = getChildText(metadata, CONFIG_TITLE);
    String author = getChildText(metadata, CONFIG_AUTHOR);
    String description = getChildText(metadata, CONFIG_DESCRIPTION);

    int[] gridSize = getGridSize(xmlDocument);
    int rows = gridSize[0];
    int cols = gridSize[1];

    int[][] states = getStates(xmlDocument, rows, cols);

    HashMap<String, String> parameters = getParameters(xmlDocument);

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
    Element element = (Element)root.getElementsByTagName(tag).item(0);
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

  private int[] getGridSize(Document xmlDocument) {
    Element grid = getFirstElementByTag(xmlDocument, CONFIG_GRID);

    String rowsStr = grid.getAttribute(CONFIG_ROWS);
    String colsStr = grid.getAttribute(CONFIG_COLUMNS);
    if (rowsStr.isBlank() || colsStr.isBlank()) {
      throw new IllegalArgumentException("Missing grid rows/cols attributes");
    }

    int rows = Integer.parseInt(rowsStr);
    int cols = Integer.parseInt(colsStr);
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Grid rows/cols must be positive");
    }

    return new int[]{rows, cols};
  }

  private int[][] getStates(Document xmlDocument, int rows, int cols) {
    Element grid = getFirstElementByTag(xmlDocument, CONFIG_GRID);

    Element statesElement = (Element) grid.getElementsByTagName(CONFIG_STATES).item(0);
    if (statesElement == null) {
      throw new IllegalArgumentException("Missing <" + CONFIG_STATES + "> element");
    }

    String rawText = statesElement.getTextContent();
    List<String> lines = nonEmptyLines(rawText);
    return iterateThroughRows(lines, rows, cols);
  }

  private List<String> nonEmptyLines(String rawText) {
    List<String> rows = new ArrayList<>();
    for (String line : rawText.split("\\R")) {
      line = line.trim();
      if (!line.isEmpty()) rows.add(line);
    }
    return rows;
  }

  private int[][] iterateThroughRows(List<String> lines, int rows, int cols) {
    if (lines.size() != rows) {
      throw new IllegalArgumentException(
          "Expected " + rows + " rows in <" + CONFIG_STATES + ">, but got " + lines.size());
    }

    int[][] states = new int[rows][cols];
    for (int r = 0; r < rows; r++) {
      states[r] = parseRow(lines.get(r), cols, r);
    }
    return states;
  }

  private int[] parseRow(String line, int cols, int currentRow) {
    String[] tokens = line.split(",");
    validateTokens(tokens, cols, currentRow);

    int[] row = new int[cols];
    for (int c = 0; c < cols; c++) {
      try {
        row[c] = Integer.parseInt(tokens[c].trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Invalid state value at row " + currentRow + ", col " + c);
      }
    }
    return row;
  }

  private void validateTokens(String[] tokens, int cols, int currentRow) {
    if (tokens.length != cols) {
      throw new IllegalArgumentException(
          "Row " + currentRow + " has " + tokens.length +
              " columns, expected " + cols);
    }
  }

  private HashMap<String, String> getParameters(Document xmlDocument) {
    HashMap<String, String> params = new HashMap<>();

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

      params.put(name, value);
    }

    return params;
  }

}
