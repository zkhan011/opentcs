// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Thread-safe last-known-position store with duplicate and out-of-order protection. */
public class VehicleMapStateStore {
  private final ConcurrentMap<String, State> states = new ConcurrentHashMap<>();
  private final TelemetryConfiguration configuration;
  private final Clock clock;

  public VehicleMapStateStore(TelemetryConfiguration configuration, Clock clock) {
    this.configuration = requireNonNull(configuration, "configuration");
    this.clock = requireNonNull(clock, "clock");
  }

  /**
   * Stores a fix if it is newer than the last accepted fix.
   *
   * @return {@code true} only when state changed
   */
  public boolean accept(String vehicleId, GpsFix fix, LogicalPosition logicalPosition) {
    requireNonNull(vehicleId, "vehicleId");
    requireNonNull(fix, "fix");
    if (vehicleId.isBlank()) {
      throw new IllegalArgumentException("vehicleId must not be blank.");
    }
    boolean[] accepted = {false};
    states.compute(vehicleId, (ignored, previous) -> {
      if (previous != null && !fix.timestamp().isAfter(previous.fix().timestamp())) {
        return previous;
      }
      accepted[0] = true;
      return new State(
          fix, logicalPosition != null ? logicalPosition
              : previous == null ? null : previous.logicalPosition()
      );
    });
    return accepted[0];
  }

  public Optional<VehicleMapSnapshot> get(String vehicleId) {
    State state = states.get(vehicleId);
    return state == null ? Optional.empty() : Optional.of(toSnapshot(vehicleId, state));
  }

  public Collection<VehicleMapSnapshot> getAll() {
    return states.entrySet().stream()
        .map(entry -> toSnapshot(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(VehicleMapSnapshot::vehicleId))
        .toList();
  }

  private VehicleMapSnapshot toSnapshot(String vehicleId, State state) {
    Duration age = Duration.between(state.fix().timestamp(), clock.instant());
    boolean connected = age.compareTo(configuration.offlineAfter()) < 0;
    boolean stale = age.compareTo(configuration.staleAfter()) >= 0;
    LogicalPosition logical = state.logicalPosition();
    return new VehicleMapSnapshot(
        vehicleId, vehicleId, null, state.fix().latitude(), state.fix().longitude(),
        state.fix().headingDegrees(), state.fix().speedKmh(), state.fix().accuracyMeters(),
        connected ? "UNKNOWN" : "OFFLINE", null,
        logical == null ? null : logical.pointName(), logical == null ? null : logical.pathName(),
        logical == null ? null : logical.confidence(),
        logical == null ? null : logical.distanceFromPathMeters(),
        logical == null ? null : logical.lastMatchedTime(),
        null, null, null, null, null, state.fix().timestamp(), stale, connected
    );
  }

  private record State(GpsFix fix, LogicalPosition logicalPosition) {
  }
}
