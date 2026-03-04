package cellsociety.config;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cellsociety.config.ConfigXmlConstants.*;

/**
 * Saves either:
 *  - {@link SimulationPreferences} (preferences-only file), or
 *  - {@link SimulationState} (preferences + active simulations).
 *
 * Reuses {@link ConfigSaver} for writing individual <simulation> elements so the simulation XML
 * shape stays consistent across the codebase. Color maps are serialized from {@link ColorMapProvider}
 * so saved files reflect the currently-registered rendering scheme.
 * @author Ixa Sigler
 */
public class SimulationSaver {

  private static final Logger LOG = LogManager.getLogger(SimulationSaver.class);

  private final ConfigSaver configSaver = new ConfigSaver();

  /**
   * Writes a preferences-only file (theme/language/speed + any registered color maps).
   * Useful for persisting UI settings even when no simulations are currently loaded.
   */
  public void savePreferences(SimulationPreferences prefs, File outFile) {
    if (prefs == null) {
      LOG.error("prefs cannot be null");
      throw new IllegalArgumentException("prefs cannot be null");
    }
    if (outFile == null) {
      LOG.error("outFile cannot be null");
      throw new IllegalArgumentException("outFile cannot be null");
    }

    validatePreferences(prefs);

    try {
      Document doc = newDocument();
      Element root = buildPreferencesElement(doc, prefs);
      doc.appendChild(root);
      writeXml(doc, outFile);
    } catch (Exception e) {
      LOG.error("Failed to save preferences to {}", outFile.getPath(), e);
      throw new RuntimeException("Failed to save preferences to " + outFile.getPath(), e);
    }
  }

  /**
   * Writes a full session file: preferences plus the list of active simulations.
   * This is used by "Save Session" behavior to restore both UI settings and the open simulations.
   */
  public void saveState(SimulationState state, File outFile) {
    if (state == null) {
      LOG.error("state cannot be null");
      throw new IllegalArgumentException("state cannot be null");
    }
    if (outFile == null) {
      LOG.error("outFile cannot be null");
      throw new IllegalArgumentException("outFile cannot be null");
    }

    validatePreferences(state.getPreferences());

    List<SimulationConfig> configs = state.getActiveSimulations();
    if (configs == null) {
      LOG.error("activeSimulations cannot be null");
      throw new IllegalArgumentException("activeSimulations cannot be null");
    }

    try {
      Document doc = newDocument();
      Element root = doc.createElement(CONFIG_SIMULATION_STATE);

      // preferences section
      root.appendChild(buildPreferencesElement(doc, state.getPreferences()));

      // simulations section (each config becomes one <simulation>)
      Element simsEl = doc.createElement(CONFIG_SIMULATIONS);

      for (SimulationConfig cfg : configs) {
        if (cfg == null) {
          LOG.error("activeSimulations contains null config");
          throw new IllegalArgumentException("activeSimulations contains null config");
        }

        // Delegate per-simulation serialization to ConfigSaver to avoid duplicated XML logic
        Element simElement = configSaver.buildSimulationElement(doc, cfg);
        simsEl.appendChild(simElement);
      }

      root.appendChild(simsEl);
      doc.appendChild(root);

      writeXml(doc, outFile);

    } catch (Exception e) {
      LOG.error("Failed to save simulation state to {}", outFile.getPath(), e);
      throw new RuntimeException("Failed to save simulation state to " + outFile.getPath(), e);
    }
  }

  /**
   * Builds the <preferences> element for either a preferences-only file
   * or the preferences section inside a full session save.
   */
  private Element buildPreferencesElement(Document doc, SimulationPreferences prefs) {
    Element root = doc.createElement(CONFIG_PREFERENCES);

    root.appendChild(textElement(doc, CONFIG_THEME,
        String.valueOf(prefs.getTheme())));

    root.appendChild(textElement(doc, CONFIG_SIMULATION_SPEED,
        String.valueOf(prefs.getSimulationSpeed())));

    root.appendChild(textElement(doc, CONFIG_LANGUAGE,
        prefs.getLanguage()));

    // Persist whatever color maps are currently registered so the session reproduces the same look.
    appendColorMapsIfAny(doc, root, ColorMapProvider.getAllColorMaps());

    return root;
  }

  /**
   * Writes:
   * <colorMaps>
   *   <colorMap type="...">
   *     <colorEntry state="0" value="#RRGGBB"/>
   *   </colorMap>
   * </colorMaps>
   *
   * Color values are stored as strings so the saved format stays UI-agnostic, but are expected
   * to be valid JavaFX colors (validated on load).
   */
  private void appendColorMapsIfAny(
      Document doc,
      Element preferencesEl,
      Map<String, Map<Integer, String>> colorMapsByType
  ) {
    if (colorMapsByType == null || colorMapsByType.isEmpty()) return;

    Element colorMapsEl = doc.createElement(CONFIG_COLOR_MAPS);

    for (Map.Entry<String, Map<Integer, String>> typeEntry : colorMapsByType.entrySet()) {
      String type = trimmed(typeEntry.getKey());
      if (type.isEmpty()) {
        LOG.error("ColorMapProvider contains blank simulationType key");
        throw new IllegalArgumentException("ColorMapProvider contains blank simulationType key");
      }

      Map<Integer, String> map = typeEntry.getValue();
      if (map == null || map.isEmpty()) continue;

      colorMapsEl.appendChild(buildColorMapElement(doc, type, map));
    }

    // Only include <colorMaps> if at least one <colorMap> was written.
    if (colorMapsEl.getChildNodes().getLength() > 0) {
      preferencesEl.appendChild(colorMapsEl);
    }
  }

  /** Builds one <colorMap type="..."> block containing many <colorEntry> nodes. */
  private Element buildColorMapElement(Document doc, String type, Map<Integer, String> map) {
    Element colorMapEl = doc.createElement(CONFIG_COLOR_MAP);
    colorMapEl.setAttribute(CONFIG_COLOR_MAP_TYPE_ATTR, type);

    for (Map.Entry<Integer, String> entry : map.entrySet()) {
      Integer state = entry.getKey();
      if (state == null) {
        LOG.error("color map for type '{}' contains null state key", type);
        throw new IllegalArgumentException("color map for type '" + type + "' contains null state key");
      }

      String c = entry.getValue();
      if (c == null) {
        LOG.error("color map for type '{}' has null color for state {}", type, state);
        throw new IllegalArgumentException("color map for type '" + type + "' has null color for state " + state);
      }

      Element entryEl = doc.createElement(CONFIG_COLOR_ENTRY);
      entryEl.setAttribute(CONFIG_COLOR_STATE_ATTR, String.valueOf(state));
      entryEl.setAttribute(CONFIG_COLOR_VALUE_ATTR, c);

      colorMapEl.appendChild(entryEl);
    }

    return colorMapEl;
  }

  private void validatePreferences(SimulationPreferences prefs) {
    if (prefs.getTheme() == null) {
      LOG.error("theme cannot be null");
      throw new IllegalArgumentException("theme cannot be null");
    }

    if (trimmed(prefs.getLanguage()).isEmpty()) {
      LOG.error("language is blank");
      throw new IllegalArgumentException("language is blank");
    }

    if (prefs.getSimulationSpeed() <= 0) {
      LOG.error("simulationSpeed must be > 0");
      throw new IllegalArgumentException("simulationSpeed must be > 0");
    }
  }

  private Document newDocument() throws Exception {
    return DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .newDocument();
  }

  private Element textElement(Document doc, String tag, String text) {
    Element el = doc.createElement(tag);
    el.setTextContent(trimmed(text));
    return el;
  }

  private void writeXml(Document doc, File outFile) throws Exception {
    Transformer transformer =
        TransformerFactory.newInstance().newTransformer();

    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc),
        new StreamResult(outFile));
  }

  private String trimmed(String s) {
    return s == null ? "" : s.trim();
  }
}