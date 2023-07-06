/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import org.signal.registration.analytics.AttemptPendingAnalysis;

/**
 * An "attempt analyzed" event is triggered when additional details about a phone number verification attempt are
 * available.
 *
 * @param attemptPendingAnalysis the original attempt pending analysis
 * @param attemptAnalysis an analysis of the attempt
 */
public record AttemptAnalyzedEvent(AttemptPendingAnalysis attemptPendingAnalysis,
                                   AttemptAnalysis attemptAnalysis) {
}
