// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import java.time.Duration;

/** Time limits used to derive connection state without discarding the last valid fix. */
public record TelemetryConfiguration(Duration staleAfter, Duration offlineAfter) {
  public TelemetryConfiguration {
    if (staleAfter == null || staleAfter.isNegative() || staleAfter.isZero()) {
      throw new IllegalArgumentException("staleAfter must be positive.");
    }
    if (offlineAfter == null || offlineAfter.compareTo(staleAfter) <= 0) {
      throw new IllegalArgumentException("offlineAfter must be greater than staleAfter.");
    }
  }
}
