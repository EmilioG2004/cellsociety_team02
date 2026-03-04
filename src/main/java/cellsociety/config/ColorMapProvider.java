package cellsociety.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides color mappings for simulations.
 * Maps simulationType -> (state -> colorString), with a global default fallback map.
 * @author Ixa Sigler
 */
public final class ColorMapProvider {

  public static final String DEFAULT_KEY = "__DEFAULT__";

  private static final Map<Integer, String> DEFAULT_MAP = new HashMap<>();

  private static final Map<String, Map<Integer, String>> COLOR_MAPS = new HashMap<>();

  private static final int MAX_STATE = 32;

  private static final String[] CONTRAST_PALETTE = {
      "#E6194B", // red
      "#3CB44B", // green
      "#4363D8", // blue
      "#FFE119", // yellow
      "#F58231", // orange
      "#911EB4", // purple
      "#46F0F0", // cyan
      "#F032E6", // magenta
      "#BCF60C", // lime
      "#FABEBE", // pink
      "#008080", // teal
      "#E6BEFF", // lavender
      "#9A6324", // brown
      "#FFFAC8", // beige
      "#800000", // maroon
      "#AAFFC3"  // mint
  };

  static {
    DEFAULT_MAP.put(0, "#FFFFFF");
    for (int i = 1; i < MAX_STATE; i++) {
      DEFAULT_MAP.put(i, CONTRAST_PALETTE[i % CONTRAST_PALETTE.length]);
    }
  }

  /**
   * Returns the color map used for a given simulation type.
   * Visualization and UI layers call this to determine how cell states
   * should be rendered. If no custom mapping exists, the default map is returned.
   *
   * @param simulationType the simulation identifier (e.g., "GameOfLife")
   * @return an unmodifiable state -> color mapping
   */
  public static Map<Integer, String> getColorMap(String simulationType) {
    Map<Integer, String> map = (simulationType == null) ? null : COLOR_MAPS.get(simulationType);
    return (map != null)
        ? Collections.unmodifiableMap(map)
        : Collections.unmodifiableMap(DEFAULT_MAP);
  }

  /**
   * Registers or replaces the full color map for a simulation type.
   * Used by configuration loaders or preference systems when installing
   * color schemes defined in external configuration files.
   *
   * @param simulationType the simulation identifier
   * @param colorMap mapping of state -> color strings
   * @throws IllegalArgumentException if either argument is null
   */
  public static void addOrReplaceColorMap(String simulationType, Map<Integer, String> colorMap) {
    if (simulationType == null || colorMap == null) {
      throw new IllegalArgumentException("simulationType and colorMap cannot be null");
    }

    if (DEFAULT_KEY.equals(simulationType)) {
      DEFAULT_MAP.clear();
      DEFAULT_MAP.putAll(colorMap);
      return;
    }

    COLOR_MAPS.put(simulationType, new HashMap<>(colorMap));
  }

  /**
   * Updates the color associated with a single state for a simulation.
   * Allows configuration tools or UI settings to adjust colors without
   * redefining the entire map.
   *
   * @param simulationType the simulation identifier
   * @param state the cell state whose color should be updated
   * @param color the hex color string (e.g., "#FF0000")
   * @throws IllegalArgumentException if simulationType or color is null
   */
  public static void setStateColor(String simulationType, int state, String color) {
    if (simulationType == null || color == null) {
      throw new IllegalArgumentException("simulationType and color cannot be null");
    }

    if (DEFAULT_KEY.equals(simulationType)) {
      DEFAULT_MAP.put(state, color);
      return;
    }

    Map<Integer, String> map =
        COLOR_MAPS.computeIfAbsent(simulationType, k -> new HashMap<>(DEFAULT_MAP));

    map.put(state, color);
  }

  /**
   * Returns a read-only snapshot of all registered color maps.
   * Useful for exporting configuration, debugging, or inspecting
   * the current color setup across simulations.
   *
   * @return an unmodifiable mapping of simulation types to color maps
   */
  public static Map<String, Map<Integer, String>> getAllColorMaps() {
    Map<String, Map<Integer, String>> snapshot = new HashMap<>();

    snapshot.put(DEFAULT_KEY,
        Collections.unmodifiableMap(new HashMap<>(DEFAULT_MAP)));

    for (Map.Entry<String, Map<Integer, String>> e : COLOR_MAPS.entrySet()) {
      snapshot.put(e.getKey(),
          Collections.unmodifiableMap(new HashMap<>(e.getValue())));
    }

    return Collections.unmodifiableMap(snapshot);
  }

  /**
   * Returns the set of simulation types that have custom color maps.
   * UI tools and configuration systems may use this to list available
   * simulation-specific color schemes.
   *
   * @return an unmodifiable set of simulation identifiers
   */
  public static Set<String> getSimulationTypes() {
    return Collections.unmodifiableSet(COLOR_MAPS.keySet());
  }
}