/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * A quantity of money in a single currency.
 *
 * @param amount the magnitude of this monetary quantity
 * @param currency the currency of this monetary quantity
 */
public record Money(BigDecimal amount, Currency currency) {

  public Money add(final Money other) {
    if (!currency.equals(other.currency)) {
      throw new IllegalArgumentException(String.format("Currencies (%s) and (%s) do not match", currency, other.currency));
    }

    return new Money(amount.add(other.amount), currency);
  }
}
