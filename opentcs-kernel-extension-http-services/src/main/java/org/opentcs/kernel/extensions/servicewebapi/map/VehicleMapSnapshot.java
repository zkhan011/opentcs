// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import java.time.Instant;

/** Complete physical and openTCS-logical state rendered by the plant overview. */
public record VehicleMapSnapshot(
    String vehicleId, String displayName, String vehicleType,
    double latitude, double longitude, Double headingDegrees, Double speedKmh,
    Double gpsAccuracyMeters, String operationalStatus, String availability,
    String matchedPoint, String matchedPath, Double mapMatchConfidence,
    Double distanceFromMatchedPathMeters, Instant lastSuccessfullyMatchedTime,
    String transportOrder, String driveOrder, String destination,
    Double fuelPercentage, String maintenanceStatus, Instant telemetryTimestamp,
    boolean stale, boolean connected
) {
}
