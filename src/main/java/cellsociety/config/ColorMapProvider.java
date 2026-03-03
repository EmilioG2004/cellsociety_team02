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

  static {
    // hsv rainbow stored as hex strings
    for (int i = 0; i < MAX_STATE; i++) {
      double t = (double) i / (MAX_STATE - 1);
      double hue = 300 * t;
      DEFAULT_MAP.put(i, hsbToHex(hue, 0.9, 0.9));
    }
  }

  public static Map<Integer, String> getColorMap(String simulationType) {
    Map<Integer, String> map = (simulationType == null) ? null : COLOR_MAPS.get(simulationType);
    return (map != null)
        ? Collections.unmodifiableMap(map)
        : Collections.unmodifiableMap(DEFAULT_MAP);
  }

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

  /** set a state -> color mapping for a specific simulation type */
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

  public static Set<String> getSimulationTypes() {
    return Collections.unmodifiableSet(COLOR_MAPS.keySet());
  }

  private static String hsbToHex(double h, double s, double b) {
    int rgb = java.awt.Color.HSBtoRGB((float) (h / 360.0), (float) s, (float) b);
    int r = (rgb >> 16) & 0xFF;
    int g = (rgb >> 8) & 0xFF;
    int bl = rgb & 0xFF;
    return String.format("#%02X%02X%02X", r, g, bl);
  }
}