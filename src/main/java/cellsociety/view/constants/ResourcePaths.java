package cellsociety.view.constants;

/**
 * Centralized resource paths and bundle names.
 */
public final class ResourcePaths {
  private ResourcePaths() {}

  // Language names used by settings and wiring
  public static final String LANGUAGE_ENGLISH = "English";
  public static final String LANGUAGE_SPANISH = "Spanish";
  public static final String LANGUAGE_PORTUGUESE = "Portuguese";

  // Theme names used by settings and view switching
  public static final String THEME_LIGHT = "Light";
  public static final String THEME_DARK = "Dark";
  public static final String THEME_DUKE = "Duke";

  // Language bundles
  public static final String BUNDLE_ENGLISH = "cellsociety.Languages.English";
  public static final String BUNDLE_SPANISH = "cellsociety.Languages.Spanish";
  public static final String BUNDLE_PORTUGUESE = "cellsociety.Languages.Portuguese";
  public static final String BUNDLE_FOLDER = "cellsociety.Languages.";

  // Internal bundles
  public static final String BUNDLE_VERSION = "cellsociety.Version";

  // Stylesheets
  public static final String CSS_BASE = "/cellsociety/stylesheets/base.css";
  public static final String CSS_LIGHT = "/cellsociety/stylesheets/light.css";
  public static final String CSS_DARK = "/cellsociety/stylesheets/dark.css";
  public static final String CSS_DUKE = "/cellsociety/stylesheets/duke.css";

  // Neighbor Strategies

  public static final String MOORE = "Moore";
  public static final String VON_NEUMANN = "VonNeumann";
  public static final String TOROIDAL = "Toroidal";
}
