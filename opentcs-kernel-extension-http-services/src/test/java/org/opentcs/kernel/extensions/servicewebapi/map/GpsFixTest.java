// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class GpsFixTest {
  @Test
  void normalizesHeading() {
    assertThat(new GpsFix(24.995, 55.04, -1.0, 0.0, 2.0, Instant.now()).headingDegrees())
        .isEqualTo(359.0);
    assertThat(GpsFix.normalizeHeading(721.5)).isEqualTo(1.5);
  }

  @Test
  void rejectsMalformedFixes() {
    assertThatIllegalArgumentException().isThrownBy(
        () -> new GpsFix(91, 55, null, null, null, Instant.now())
    );
    assertThatIllegalArgumentException().isThrownBy(
        () -> new GpsFix(0, 0, null, null, null, Instant.now())
    );
    assertThatIllegalArgumentException().isThrownBy(
        () -> new GpsFix(24, 55, null, -0.1, null, Instant.now())
    );
  }
}
