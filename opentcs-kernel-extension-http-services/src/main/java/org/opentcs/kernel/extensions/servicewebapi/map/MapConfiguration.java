// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import static java.util.Objects.requireNonNull;

/** Public, non-secret map configuration. */
public record MapConfiguration(
    MapProvider provider,
    String tileUrl,
    String attribution,
    int minZoom,
    int maxZoom,
    double defaultLatitude,
    double defaultLongitude,
    int defaultZoom,
    boolean offlineEnabled
) {
  public MapConfiguration {
    requireNonNull(provider, "provider");
    requireNonNull(tileUrl, "tileUrl");
    requireNonNull(attribution, "attribution");
    if (attribution.isBlank()) {
      throw new IllegalArgumentException("Tile attribution must not be blank.");
    }
    if (minZoom < 0 || maxZoom > 24 || minZoom > maxZoom) {
      throw new IllegalArgumentException("Invalid zoom range.");
    }
    if (defaultZoom < minZoom || defaultZoom > maxZoom) {
      throw new IllegalArgumentException("Default zoom is outside the configured range.");
    }
    GpsFix.validateCoordinate(defaultLatitude, defaultLongitude);
    if (provider == MapProvider.OSM_OFFLINE && !offlineEnabled) {
      throw new IllegalArgumentException("OSM_OFFLINE requires offlineEnabled.");
    }
  }
}
