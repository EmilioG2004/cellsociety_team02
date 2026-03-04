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

  public static void addLanguage(String languageChoice, String bundleBaseName,
      String localizedNameKey) {
    BUNDLE_BY_LANGUAGE.put(languageChoice, bundleBaseName);
    LABEL_KEY_BY_LANGUAGE.put(languageChoice, localizedNameKey);
  }

  public List<String> getLanguageChoices() {
    return List.copyOf(BUNDLE_BY_LANGUAGE.keySet());
  }

  public String getDefaultLanguage() {
    return DEFAULT_LANGUAGE;
  }

  public ResourceBundle resolveBundle(String languageChoice) {
    String bundleName = BUNDLE_BY_LANGUAGE.getOrDefault(
        languageChoice,
        BUNDLE_BY_LANGUAGE.get(DEFAULT_LANGUAGE)
    );
    return ResourceBundle.getBundle(bundleName);
  }

  public String toDisplayName(String languageChoice, ResourceBundle uiBundle) {
    String labelKey = LABEL_KEY_BY_LANGUAGE.get(languageChoice);
    if (labelKey == null) {
      return languageChoice;
    }
    return uiBundle.getString(labelKey);
  }
}
