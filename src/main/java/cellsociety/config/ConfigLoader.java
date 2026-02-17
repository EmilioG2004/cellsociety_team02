package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import static cellsociety.config.ConfigXmlConstants.*;

public class ConfigLoader {

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

    int[] gridSize = getGridSize(xmlDocument);
    int rows = gridSize[0];
    int cols = gridSize[1];

    int[][] states = getInitialStates(xmlDocument, rows, cols);

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

    return new int[] { rows, cols };
  }

  // check for explicit states; if not provided, then check if counts for random grid are given
  private int[][] getInitialStates(Document xmlDocument, int rows, int cols) {
    Element grid = getFirstElementByTag(xmlDocument, CONFIG_GRID);

    Element statesElement = (Element) grid.getElementsByTagName(CONFIG_STATES).item(0);
    if (statesElement != null) {
      return getStates(statesElement, rows, cols);
    }

    return getRandomStatesByCount(xmlDocument, rows, cols);
  }

  private int[][] getStates(Element statesElement, int rows, int cols) {
    String rawText = statesElement.getTextContent();
    List<String> lines = nonEmptyLines(rawText);
    return iterateThroughRows(lines, rows, cols);
  }

  private int[][] getRandomStatesByCount(Document xmlDocument, int rows, int cols) {
    Element grid = getFirstElementByTag(xmlDocument, CONFIG_GRID);
    Element randomElement = (Element) grid.getElementsByTagName(CONFIG_RANDOM_BY_COUNT).item(0);

    if (randomElement == null) {
      throw new IllegalArgumentException(
          "Missing <" + CONFIG_STATES + "> and no <" + CONFIG_RANDOM_BY_COUNT + "> provided."
      );
    }

    Map<Integer, Integer> counts = parseStateCounts(randomElement);

    int totalCells = rows * cols;
    fillRandomStateMapRemainder(counts, totalCells);

    return buildGridFromStateCounts(counts, rows, cols);
  }

  private Map<Integer, Integer> parseStateCounts(Element randomElement) {
    Map<Integer, Integer> counts = new HashMap<>();
    var stateNodes = randomElement.getElementsByTagName(CONFIG_RANDOM_STATE);

    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateEl = (Element) stateNodes.item(i);

      int id = parseNonNegativeInt(stateEl, CONFIG_ID_ATTR);
      int count = parseNonNegativeInt(stateEl, CONFIG_COUNT_ATTR);

      if (counts.containsKey(id)) {
        throw new IllegalArgumentException("Duplicate state id: " + id);
      }

      counts.put(id, count);
    }

    return counts;
  }

  private int parseNonNegativeInt(Element el, String attr) {
    String raw = el.getAttribute(attr).trim();
    if (raw.isEmpty()) {
      throw new IllegalArgumentException(
          "<state> missing '" + attr + "' attribute"
      );
    }

    try {
      int value = Integer.parseInt(raw);
      if (value < 0) throw new IllegalArgumentException();
      return value;
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Invalid non-negative integer for '" + attr + "': " + raw
      );
    }
  }

  private void fillRandomStateMapRemainder(Map<Integer, Integer> counts, int totalCells) {
    int sum = 0;
    for (int c : counts.values()) sum += c;

    if (sum > totalCells) {
      throw new IllegalArgumentException(
          "Specified state counts (" + sum +
              ") exceed total grid cells (" + totalCells + ")"
      );
    }

    if (sum < totalCells) {
      counts.put(0,
          counts.getOrDefault(0, 0) + (totalCells - sum));
    }
  }

  private int[][] buildGridFromStateCounts(Map<Integer, Integer> counts, int rows, int cols) {
    int totalCells = rows * cols;

    List<Integer> values = new ArrayList<>(totalCells);

    // add all the intended cells to values list
    for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
      int stateId = entry.getKey();
      int count = entry.getValue();

      for (int i = 0; i < count; i++) {
        values.add(stateId);
      }
    }

    if (values.size() != totalCells) {
      throw new IllegalArgumentException(
          "State counts do not match grid size. Expected " +
              totalCells + " but got " + values.size()
      );
    }

    Collections.shuffle(values);

    int[][] grid = new int[rows][cols];
    int index = 0;

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        grid[r][c] = values.get(index++);
      }
    }

    return grid;
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
