// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class VehicleMapStateStoreTest {
  private static final Instant NOW = Instant.parse("2026-08-31T10:30:00Z");

  @Test
  void ignoresDuplicatesAndOlderFixes() {
    VehicleMapStateStore store = storeAt(NOW);
    GpsFix newest = fix(NOW.minusSeconds(1), 24.995);

    assertThat(store.accept("ITV-1001", newest, null)).isTrue();
    assertThat(store.accept("ITV-1001", newest, null)).isFalse();
    assertThat(store.accept("ITV-1001", fix(NOW.minusSeconds(2), 24.0), null)).isFalse();
    assertThat(store.get("ITV-1001").orElseThrow().latitude()).isEqualTo(24.995);
  }

  @Test
  void derivesStaleAndOfflineWithoutDeletingPosition() {
    VehicleMapStateStore staleStore = storeAt(NOW);
    staleStore.accept("ITV-1002", fix(NOW.minusSeconds(31), 24.995), null);
    assertThat(staleStore.get("ITV-1002").orElseThrow())
        .returns(true, VehicleMapSnapshot::stale)
        .returns(true, VehicleMapSnapshot::connected);

    VehicleMapStateStore offlineStore = storeAt(NOW);
    offlineStore.accept("ITV-1003", fix(NOW.minusSeconds(121), 24.996), null);
    assertThat(offlineStore.get("ITV-1003").orElseThrow())
        .returns(true, VehicleMapSnapshot::stale)
        .returns(false, VehicleMapSnapshot::connected)
        .returns(24.996, VehicleMapSnapshot::latitude);
  }

  private VehicleMapStateStore storeAt(Instant instant) {
    return new VehicleMapStateStore(
        new TelemetryConfiguration(Duration.ofSeconds(30), Duration.ofSeconds(120)),
        Clock.fixed(instant, ZoneOffset.UTC)
    );
  }

  private GpsFix fix(Instant timestamp, double latitude) {
    return new GpsFix(latitude, 55.04, 90.0, 18.4, 2.5, timestamp);
  }
}
