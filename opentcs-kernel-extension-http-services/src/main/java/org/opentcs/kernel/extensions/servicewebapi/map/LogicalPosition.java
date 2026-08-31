// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.map;

import java.time.Instant;

/** A map match for display and logical-position input; never a command acknowledgement. */
public record LogicalPosition(
    String pointName,
    String pathName,
    Double confidence,
    Double distanceFromPathMeters,
    Instant lastMatchedTime
) {
}
