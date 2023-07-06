package org.signal.registration.util;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Optional;

public class PhoneNumbers {

  private PhoneNumbers() {}

  /**
   * Return the upper case region code for the phone number, or XX if the country code is missing or invalid
   *
   * @param phoneNumber
   * @return the region code
   */
  public static String regionCodeUpper(Phonenumber.PhoneNumber phoneNumber) {
    return Optional.ofNullable(PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber))
        .map(String::toUpperCase)
        .orElse("XX");
  }

}
