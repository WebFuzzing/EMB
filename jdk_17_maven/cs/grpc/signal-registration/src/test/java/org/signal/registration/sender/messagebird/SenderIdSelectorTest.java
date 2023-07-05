package org.signal.registration.sender.messagebird;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Map;

public class SenderIdSelectorTest {

  private static final Phonenumber.PhoneNumber MX = PhoneNumberUtil.getInstance().getExampleNumber("MX");
  private static final Phonenumber.PhoneNumber US = PhoneNumberUtil.getInstance().getExampleNumber("US");

  @Test
  public void defaultSender() {
    final SenderIdSelector selector = new SenderIdSelector(
        new MessageBirdSenderConfiguration("default", Map.of("mx", "mx-sender")));
    Assertions.assertEquals("default", selector.getSenderId(US));
  }

  @Test
  public void regionOverride() {
    final SenderIdSelector selector = new SenderIdSelector(
        new MessageBirdSenderConfiguration("default", Map.of("mx", "mx-sender")));
    Assertions.assertEquals("mx-sender", selector.getSenderId(MX));

  }

}
