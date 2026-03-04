package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static cellsociety.config.ConfigXmlConstants.*;

public class SimulationLoader {

  private final ConfigLoader configLoader = new ConfigLoader();

  public SimulationState loadState(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {

    Document doc = parse(xmlFile);

    Element root = doc.getDocumentElement();
    if (root == null || !CONFIG_SIMULATION_STATE.equals(root.getTagName())) {
      throw new IllegalArgumentException("Expected root <" + CONFIG_SIMULATION_STATE + "> in " + xmlFile.getName());
    }

    Element prefsEl = getFirstChildElementByTag(root, CONFIG_PREFERENCES);
    SimulationPreferences prefs = parsePreferencesElement(prefsEl);

    Element simsEl = getOptionalFirstChildElementByTag(root, CONFIG_SIMULATIONS);
    NodeList simNodes = (simsEl == null) ? null : simsEl.getElementsByTagName(CONFIG_SIMULATION);

    List<SimulationConfig> configs = new ArrayList<>();
    if (simNodes != null) {
      for (int i = 0; i < simNodes.getLength(); i++) {
        configs.add(configLoader.loadConfig((Element) simNodes.item(i)));
      }
    }

    return new SimulationState(configs, prefs);
  }

  private SimulationPreferences parsePreferencesElement(Element preferencesEl) {
    Integer theme = tryParseInt(getChildText(preferencesEl, CONFIG_THEME), CONFIG_THEME);
    double speed = tryParseDouble(getChildText(preferencesEl, CONFIG_SIMULATION_SPEED), CONFIG_SIMULATION_SPEED);
    String language = getChildText(preferencesEl, CONFIG_LANGUAGE);

    if (language.isBlank()) {
      throw new IllegalArgumentException("<" + CONFIG_LANGUAGE + "> cannot be blank");
    }
    if (speed <= 0) {
      throw new IllegalArgumentException("<" + CONFIG_SIMULATION_SPEED + "> must be > 0");
    }

    // parse many color maps keyed by simulation type (including default), then register in ColorMapProvider
    Map<String, Map<Integer, String>> colorMapsByType = parseColorMaps(preferencesEl);
    registerColorMaps(colorMapsByType);

    // preferences stores all maps
    return new SimulationPreferences(theme, language, speed);
  }

  /**
   * Parses multiple color maps.
   *
   * Expected structure:
   * <colorMaps>
   *   <colorMap type="__DEFAULT__">
   *     <colorEntry state="0" value="#FFFFFF"/>
   *   </colorMap>
   * </colorMaps>
   */
  private Map<String, Map<Integer, String>> parseColorMaps(Element preferencesEl) {
    Map<String, Map<Integer, String>> maps = new HashMap<>();

    Element colorMapsEl = getOptionalFirstChildElementByTag(preferencesEl, CONFIG_COLOR_MAPS);
    if (colorMapsEl != null) {
      NodeList colorMapNodes = colorMapsEl.getElementsByTagName(CONFIG_COLOR_MAP);
      for (int i = 0; i < colorMapNodes.getLength(); i++) {
        Element colorMapEl = (Element) colorMapNodes.item(i);

        String type = colorMapEl.getAttribute(CONFIG_COLOR_MAP_TYPE_ATTR).trim();
        if (type.isEmpty()) {
          throw new IllegalArgumentException("<" + CONFIG_COLOR_MAP + "> missing '" + CONFIG_COLOR_MAP_TYPE_ATTR + "'");
        }

        maps.put(type, parseSingleColorMap(colorMapEl));
      }
      return maps;
    }

    // if only one map, use default
    Element singleOldColorMapEl = getOptionalFirstChildElementByTag(preferencesEl, CONFIG_COLOR_MAP);
    if (singleOldColorMapEl != null) {
      maps.put(ColorMapProvider.DEFAULT_KEY, parseSingleColorMap(singleOldColorMapEl));
    }

    return maps;
  }

  /** Parses one <colorMap> element into state -> colorString. */
  private Map<Integer, String> parseSingleColorMap(Element colorMapEl) {
    Map<Integer, String> map = new HashMap<>();

    NodeList entries = colorMapEl.getElementsByTagName(CONFIG_COLOR_ENTRY);
    for (int i = 0; i < entries.getLength(); i++) {
      Element entry = (Element) entries.item(i);

      String stateStr = entry.getAttribute(CONFIG_COLOR_STATE_ATTR).trim();
      String colorStr = entry.getAttribute(CONFIG_COLOR_VALUE_ATTR).trim();

      if (stateStr.isEmpty()) {
        throw new IllegalArgumentException("<" + CONFIG_COLOR_ENTRY + "> missing '" + CONFIG_COLOR_STATE_ATTR + "'");
      }
      if (colorStr.isEmpty()) {
        throw new IllegalArgumentException("<" + CONFIG_COLOR_ENTRY + "> missing '" + CONFIG_COLOR_VALUE_ATTR + "'");
      }

      int state = tryParseInt(stateStr, CONFIG_COLOR_STATE_ATTR);
      map.put(state, colorStr);
    }

    return map;
  }

  private void registerColorMaps(Map<String, Map<Integer, String>> mapsByType) {
    for (Map.Entry<String, Map<Integer, String>> e : mapsByType.entrySet()) {
      String simType = e.getKey();
      Map<Integer, String> raw = e.getValue();

      Map<Integer, String> fxMap = new HashMap<>();
      for (Map.Entry<Integer, String> entry : raw.entrySet()) {
        try {
          fxMap.put(entry.getKey(), entry.getValue());
        } catch (IllegalArgumentException ex) {
          throw new IllegalArgumentException(
              "Invalid color value '" + entry.getValue() + "' for state " + entry.getKey()
                  + " in colorMap type='" + simType + "'"
          );
        }
      }

      // replace any existing map for that type (including DEFAULT_KEY)
      ColorMapProvider.addOrReplaceColorMap(simType, fxMap);
    }
  }

  private Document parse(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {
    return DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse(xmlFile);
  }

  private Element getFirstChildElementByTag(Element parent, String tag) {
    Element el = getOptionalFirstChildElementByTag(parent, tag);
    if (el == null) throw new IllegalArgumentException("Missing <" + tag + "> element");
    return el;
  }

  private Element getOptionalFirstChildElementByTag(Element parent, String tag) {
    NodeList nodes = parent.getElementsByTagName(tag);
    if (nodes == null || nodes.getLength() == 0) return null;
    return (Element) nodes.item(0);
  }

  private String getChildText(Element parent, String tagName) {
    NodeList nodes = parent.getElementsByTagName(tagName);
    if (nodes == null || nodes.getLength() == 0) return "";
    Element child = (Element) nodes.item(0);
    return child == null ? "" : child.getTextContent().trim();
  }

  private int tryParseInt(String text, String fieldName) {
    if (text == null || text.trim().isEmpty()) {
      throw new IllegalArgumentException("Missing/blank integer for " + fieldName);
    }
    try {
      return Integer.parseInt(text.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid integer for " + fieldName + ": " + text);
    }
  }

  private double tryParseDouble(String text, String fieldName) {
    if (text == null || text.trim().isEmpty()) {
      throw new IllegalArgumentException("Missing/blank number for " + fieldName);
    }
    try {
      return Double.parseDouble(text.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number for " + fieldName + ": " + text);
    }
  }
}
