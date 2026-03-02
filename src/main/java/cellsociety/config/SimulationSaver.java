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

import static cellsociety.config.ConfigXmlConstants.*;

/**
 * General saver for:
 *  - SimulationPreferences (preferences file)
 *  - SimulationState (preferences + active simulations)
 *
 * Delegates simulation XML building to ConfigSaver.
 * @author Ixa Sigler
 */
public class SimulationSaver {

  private final ConfigSaver configSaver = new ConfigSaver();

  /** Save only preferences to a file. */
  public void savePreferences(SimulationPreferences prefs, File outFile) {
    if (prefs == null) throw new IllegalArgumentException("prefs cannot be null");
    if (outFile == null) throw new IllegalArgumentException("outFile cannot be null");

    validatePreferences(prefs);

    try {
      Document doc = newDocument();
      Element root = buildPreferencesElement(doc, prefs);
      doc.appendChild(root);
      writeXml(doc, outFile);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save preferences to " + outFile.getPath(), e);
    }
  }

  /** Save full simulation state (preferences + all active simulations). */
  public void saveState(SimulationState state, File outFile) {
    if (state == null) throw new IllegalArgumentException("state cannot be null");
    if (outFile == null) throw new IllegalArgumentException("outFile cannot be null");

    validatePreferences(state.getPreferences());

    List<SimulationConfig> configs = state.getActiveSimulations();
    if (configs == null) throw new IllegalArgumentException("activeSimulations cannot be null");

    try {
      Document doc = newDocument();
      Element root = doc.createElement(CONFIG_SIMULATION_STATE);

      // preferences section
      root.appendChild(buildPreferencesElement(doc, state.getPreferences()));

      // simulations section
      Element simsEl = doc.createElement(CONFIG_SIMULATIONS);

      for (SimulationConfig cfg : configs) {
        if (cfg == null) {
          throw new IllegalArgumentException("activeSimulations contains null config");
        }

        // Reuse ConfigSaver to generate <simulation> element
        Element simElement = configSaver.buildSimulationElement(doc, cfg);
        simsEl.appendChild(simElement);
      }

      root.appendChild(simsEl);
      doc.appendChild(root);

      writeXml(doc, outFile);

    } catch (Exception e) {
      throw new RuntimeException("Failed to save simulation state to " + outFile.getPath(), e);
    }
  }


  private Element buildPreferencesElement(Document doc, SimulationPreferences prefs) {
    Element root = doc.createElement(CONFIG_PREFERENCES);

    root.appendChild(textElement(doc, CONFIG_THEME,
        String.valueOf(prefs.getTheme())));

    root.appendChild(textElement(doc, CONFIG_SIMULATION_SPEED,
        String.valueOf(prefs.getSimulationSpeed())));

    root.appendChild(textElement(doc, CONFIG_LANGUAGE,
        prefs.getLanguage()));

    appendColorMapIfAny(doc, root, prefs.getColorMap());

    return root;
  }

  private void appendColorMapIfAny(Document doc,
      Element preferencesEl,
      Map<Integer, String> colorMap) {
    if (colorMap == null || colorMap.isEmpty()) return;

    Element colorMapEl = doc.createElement(CONFIG_COLOR_MAP);

    for (Map.Entry<Integer, String> entry : colorMap.entrySet()) {
      if (entry.getKey() == null)
        throw new IllegalArgumentException("colorMap contains null key");

      String color = trimmed(entry.getValue());
      if (color.isEmpty())
        throw new IllegalArgumentException("colorMap has blank value for key " + entry.getKey());

      Element entryEl = doc.createElement(CONFIG_COLOR_ENTRY);
      entryEl.setAttribute(CONFIG_COLOR_STATE_ATTR,
          String.valueOf(entry.getKey()));
      entryEl.setAttribute(CONFIG_COLOR_VALUE_ATTR, color);

      colorMapEl.appendChild(entryEl);
    }

    preferencesEl.appendChild(colorMapEl);
  }

  private void validatePreferences(SimulationPreferences prefs) {
    if (prefs.getTheme() == null)
      throw new IllegalArgumentException("theme cannot be null");

    if (trimmed(prefs.getLanguage()).isEmpty())
      throw new IllegalArgumentException("language is blank");

    if (prefs.getSimulationSpeed() <= 0)
      throw new IllegalArgumentException("simulationSpeed must be > 0");
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