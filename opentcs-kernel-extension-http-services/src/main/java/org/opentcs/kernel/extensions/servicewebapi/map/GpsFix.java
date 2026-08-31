// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import java.time.Duration;
import java.time.Instant;

/** A validated physical position reported by a vehicle telemetry adapter. */
public record GpsFix(
    double latitude,
    double longitude,
    Double headingDegrees,
    Double speedKmh,
    Double accuracyMeters,
    Instant timestamp
) {
  private static final Duration MAX_FUTURE_SKEW = Duration.ofMinutes(2);

  public GpsFix {
    validateCoordinate(latitude, longitude);
    if (latitude == 0.0 && longitude == 0.0) {
      throw new IllegalArgumentException("The null-island coordinate is not a valid GPS fix.");
    }
    if (headingDegrees != null) {
      if (!Double.isFinite(headingDegrees)) {
        throw new IllegalArgumentException("Heading must be finite.");
      }
      headingDegrees = normalizeHeading(headingDegrees);
    }
    if (speedKmh != null && (!Double.isFinite(speedKmh) || speedKmh < 0)) {
      throw new IllegalArgumentException("Speed must be finite and non-negative.");
    }
    if (accuracyMeters != null && (!Double.isFinite(accuracyMeters) || accuracyMeters < 0)) {
      throw new IllegalArgumentException("Accuracy must be finite and non-negative.");
    }
    if (timestamp == null || timestamp.isAfter(Instant.now().plus(MAX_FUTURE_SKEW))) {
      throw new IllegalArgumentException(
          "Telemetry timestamp is missing or too far in the future."
      );
    }
  }

  public static double normalizeHeading(double heading) {
    return (heading % 360.0 + 360.0) % 360.0;
  }

  static void validateCoordinate(double latitude, double longitude) {
    if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude is outside [-90, 90].");
    }
    if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude is outside [-180, 180].");
    }
  }
}
