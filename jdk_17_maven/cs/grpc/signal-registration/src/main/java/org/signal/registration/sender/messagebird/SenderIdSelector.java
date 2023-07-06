package org.signal.registration.sender.messagebird;

import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;
import org.signal.registration.util.PhoneNumbers;

@Singleton
public class SenderIdSelector {
  // Map from region to senderId to use for that region
  private final Map<String, String> regionSenderIds;
  private String defaultSenderId;

  public SenderIdSelector(final MessageBirdSenderConfiguration configuration) {
    this.regionSenderIds = configuration.regionSenderIds()
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            e -> e.getKey().toUpperCase(),
            Map.Entry::getValue
        ));
    this.defaultSenderId = configuration.defaultSenderId();
  }


  /**
   * Get a senderId to use
   *
   * @param phoneNumber target phone number to send to
   * @return the sender for a message to phoneNumber (e.g. an alphanumeric sender id, a phone number)
   */
  public String getSenderId(final Phonenumber.PhoneNumber phoneNumber) {
    final String region = PhoneNumbers.regionCodeUpper(phoneNumber);
    return regionSenderIds.getOrDefault(region, defaultSenderId);
  }

}
