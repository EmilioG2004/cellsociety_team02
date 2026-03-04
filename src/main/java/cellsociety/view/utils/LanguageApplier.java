package cellsociety.view.utils;

import static cellsociety.view.constants.ResourcePaths.BUNDLE_ENGLISH;
import static cellsociety.view.constants.ResourcePaths.BUNDLE_PORTUGUESE;
import static cellsociety.view.constants.ResourcePaths.BUNDLE_SPANISH;
import static cellsociety.view.constants.ResourcePaths.LANGUAGE_ENGLISH;
import static cellsociety.view.constants.ResourcePaths.LANGUAGE_PORTUGUESE;
import static cellsociety.view.constants.ResourcePaths.LANGUAGE_SPANISH;

import cellsociety.view.constants.BundleKeys;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Centralized language mapping and bundle resolution.
 *
 * Purpose: map user-facing language choices to bundle basenames and localized labels.
 * Assumptions: default language and bundle names remain available at runtime.
 * Dependencies: {@link cellsociety.view.constants.ResourcePaths} and {@link ResourceBundle}.
 *
 * @author Emilio Garcia
 * @author Team 02
 */
public class LanguageApplier {

  private static final Map<String, String> BUNDLE_BY_LANGUAGE = new LinkedHashMap<>();
  private static final Map<String, String> LABEL_KEY_BY_LANGUAGE = new LinkedHashMap<>();
  private static final String DEFAULT_LANGUAGE = LANGUAGE_ENGLISH;

  static {
    addLanguage(LANGUAGE_ENGLISH, BUNDLE_ENGLISH, BundleKeys.LANGUAGE_ENGLISH);
    addLanguage(LANGUAGE_SPANISH, BUNDLE_SPANISH, BundleKeys.LANGUAGE_SPANISH);
    addLanguage(LANGUAGE_PORTUGUESE, BUNDLE_PORTUGUESE, BundleKeys.LANGUAGE_PORTUGUESE);
  }

  /**
   * Registers or replaces one supported language mapping.
   *
   * @param languageChoice canonical language identifier shown in settings
   * @param bundleBaseName bundle basename used by {@link ResourceBundle#getBundle(String)}
   * @param localizedNameKey key used to localize this language name in UI
   */
  public static void addLanguage(String languageChoice, String bundleBaseName,
      String localizedNameKey) {
    BUNDLE_BY_LANGUAGE.put(languageChoice, bundleBaseName);
    LABEL_KEY_BY_LANGUAGE.put(languageChoice, localizedNameKey);
  }

  /**
   * Returns all supported language choices.
   *
   * @return immutable list of canonical language identifiers
   */
  public List<String> getLanguageChoices() {
    return List.copyOf(BUNDLE_BY_LANGUAGE.keySet());
  }

  /**
   * Returns the default language choice.
   *
   * @return canonical default language identifier
   */
  public String getDefaultLanguage() {
    return DEFAULT_LANGUAGE;
  }

  /**
   * Resolves the bundle for a language choice.
   *
   * @param languageChoice canonical language identifier
   * @return resolved bundle, or default-language bundle if the choice is unknown
   */
  public ResourceBundle resolveBundle(String languageChoice) {
    String bundleName = BUNDLE_BY_LANGUAGE.getOrDefault(
        languageChoice,
        BUNDLE_BY_LANGUAGE.get(DEFAULT_LANGUAGE)
    );
    return ResourceBundle.getBundle(bundleName);
  }

  /**
   * Converts a canonical language choice to a localized display label.
   *
   * @param languageChoice canonical language identifier
   * @param uiBundle currently active UI bundle
   * @return localized display label when available, otherwise the raw choice value
   */
  public String toDisplayName(String languageChoice, ResourceBundle uiBundle) {
    String labelKey = LABEL_KEY_BY_LANGUAGE.get(languageChoice);
    if (labelKey == null) {
      return languageChoice;
    }
    return uiBundle.getString(labelKey);
  }
}
