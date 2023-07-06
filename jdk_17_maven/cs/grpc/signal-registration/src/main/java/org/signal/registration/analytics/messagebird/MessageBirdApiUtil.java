package org.signal.registration.analytics.messagebird;

import com.messagebird.objects.MessageResponse;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.signal.registration.analytics.AttemptAnalysis;
import org.signal.registration.analytics.Money;

class MessageBirdApiUtil {

  private MessageBirdApiUtil() {
  }

  static Optional<AttemptAnalysis> extractAttemptAnalysis(final MessageResponse.Recipients recipients) {
    final Optional<Money> maybePrice = recipients.getItems().stream()
        .map(MessageResponse.Items::getPrice)
        .filter(price -> price != null && StringUtils.isNotBlank(price.getCurrency()))
        .map(price -> new Money(price.getAmountDecimal(), Currency.getInstance(price.getCurrency().toUpperCase(Locale.ROOT))))
        .reduce(Money::add);

    final Optional<String> maybeMcc =
        recipients.getItems().stream().map(MessageResponse.Items::getMcc).filter(StringUtils::isNotBlank).findFirst();

    final Optional<String> maybeMnc =
        recipients.getItems().stream().map(MessageResponse.Items::getMnc).filter(StringUtils::isNotBlank).findFirst();

    return maybePrice.map(price -> new AttemptAnalysis(maybePrice, maybeMcc, maybeMnc));
  }
}
