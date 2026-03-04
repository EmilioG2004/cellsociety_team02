/**
 * View utility services for language and theme resolution.
 *
 * Design goals:
 * Centralize mapping logic for presentation preferences.
 * Keep preference conversion logic reusable and independent from dialog widgets.
 *
 * Contracts:
 * Utility classes return canonical choices, bundle mappings, and stylesheet mappings.
 * Unknown values are resolved using safe defaults to preserve runtime behavior.
 *
 * @author Emilio Garcia
 * @author Team 02
 */
package cellsociety.view.utils;
