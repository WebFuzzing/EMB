/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * An analysis of a phone number verification attempt that includes information available only after an attempt has
 * concluded, including the price to send a verification code and the sender's view of the MCC/MNC with which the phone
 * number is associated.
 *
 * @param price the price, if available, for sending a verification code to the destination phone number
 * @param mcc the mobile country code (MCC), if available, associated with the destination phone number
 * @param mnc the mobile network code (MNC), if available, associated with the destination phone number
 */
public record AttemptAnalysis(Optional<Money> price,
                              Optional<String> mcc,
                              Optional<String> mnc) {

}
