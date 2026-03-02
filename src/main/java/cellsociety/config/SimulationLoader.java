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

  public SimulationPreferences loadPreferences(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {

    Document doc = parse(xmlFile);

    Element root = doc.getDocumentElement();
    if (root == null || !CONFIG_PREFERENCES.equals(root.getTagName())) {
      throw new IllegalArgumentException("Expected root <" + CONFIG_PREFERENCES + "> in " + xmlFile.getName());
    }

    return parsePreferencesElement(root);
  }

  public SimulationState loadState(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {

    Document doc = parse(xmlFile);

    Element root = doc.getDocumentElement();
    if (root == null || !CONFIG_SIMULATION_STATE.equals(root.getTagName())) {
      throw new IllegalArgumentException("Expected root <" + CONFIG_SIMULATION_STATE + "> in " + xmlFile.getName());
    }

    Element prefsEl = getFirstChildElementByTag(root, CONFIG_PREFERENCES);
    SimulationPreferences prefs = parsePreferencesElement(prefsEl);

    Element simsEl = getFirstChildElementByTag(root, CONFIG_SIMULATIONS);
    NodeList simNodes = simsEl.getElementsByTagName(CONFIG_SIMULATION);

    List<SimulationConfig> configs = new ArrayList<>();
    for (int i = 0; i < simNodes.getLength(); i++) {
      Element simEl = (Element) simNodes.item(i);
      configs.add(configLoader.loadConfig(simEl)); // <-- NO duplicated simulation parsing
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

    Map<Integer, String> colorMap = parseColorMap(preferencesEl);

    return new SimulationPreferences(theme, language, speed, colorMap);
  }

  private Map<Integer, String> parseColorMap(Element preferencesEl) {
    Map<Integer, String> map = new HashMap<>();

    Element colorMapEl = getOptionalFirstChildElementByTag(preferencesEl, CONFIG_COLOR_MAP);
    if (colorMapEl == null) return map;

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