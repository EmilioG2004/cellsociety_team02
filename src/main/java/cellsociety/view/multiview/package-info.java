/**
 * Multi-simulation panel and rendering contracts.
 *
 * Design goals:
 * Support multiple concurrent simulation views through small, composable renderer APIs.
 * Keep panel UI independent from controller orchestration and simulation model internals.
 *
 * Contracts:
 * Renderers expose node-based embedding points and deterministic rendering methods.
 * Controllers provide callbacks for interactions such as load, save, and metadata requests.
 *
 * @author Emilio Garcia
 * @author Team 02
 */
package cellsociety.view.multiview;
