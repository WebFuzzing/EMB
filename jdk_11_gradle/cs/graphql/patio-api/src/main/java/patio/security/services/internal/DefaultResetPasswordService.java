/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.security.services.internal;

import io.micronaut.context.annotation.Value;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.URLResolverService;
import patio.security.services.ResetPasswordService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Business logic to handle user password operations
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultResetPasswordService implements ResetPasswordService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultResetPasswordService.class);

  private final transient String resetPasswordUrl;
  private final transient UserRepository userRepository;
  private final transient Auth0CryptoService cryptoService;
  private final transient EmailComposerService emailComposerService;
  private final transient EmailService emailService;
  private final transient URLResolverService urlResolverService;

  /**
   * Initializes service by using the required services
   *
   * @param resetPasswordUrl to get the link from configuration
   * @param userRepository an instance of {@link UserRepository}
   * @param auth0CryptoService an instance of {@link Auth0CryptoService}
   * @param emailComposerService service to compose the {@link Email} notifications
   * @param emailService to be able to send notifications to group members
   * @param urlResolverService to resolve possible link urls for emails
   */
  public DefaultResetPasswordService(
      @Value("${front.urls.change-password:none}") String resetPasswordUrl,
      UserRepository userRepository,
      Auth0CryptoService auth0CryptoService,
      EmailComposerService emailComposerService,
      EmailService emailService,
      URLResolverService urlResolverService) {
    this.resetPasswordUrl = resetPasswordUrl;
    this.userRepository = userRepository;
    this.cryptoService = auth0CryptoService;
    this.emailComposerService = emailComposerService;
    this.emailService = emailService;
    this.urlResolverService = urlResolverService;
  }

  @Override
  public void resetPasswordRequest(String userEmail) {
    final String randomOTP = cryptoService.hash(RandomStringUtils.randomAlphanumeric(17));
    final Optional<User> user = userRepository.findByEmail(userEmail);

    user.ifPresent(
        (u) -> {
          LOG.info(String.format("Notifying user %s to reset her password", u.getEmail()));

          setOTPForUser(randomOTP, u);
          Email resettingEmail = composeResettingEmail(u);
          emailService.send(resettingEmail);
        });
  }

  private void setOTPForUser(String randomToken, User user) {
    user.setOtp(randomToken);
    user.setOtpCreationDateTime(OffsetDateTime.now());
    userRepository.save(user);
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private Email composeResettingEmail(User user) {
    String emailRecipient = user.getEmail();
    String emailSubject = emailComposerService.getMessage("resetPassword.subject");
    String emailMainMessage = emailComposerService.getMessage("resetPassword.main");
    String emailBodyTemplate = emailComposerService.getMessage("resetPassword.bodyTemplate");

    Map<String, Object> greetingMessageVars = Map.of("username", user.getName());
    String greetingsMessage =
        emailComposerService.getMessage("resetPassword.greetings", greetingMessageVars);
    String thanksMessage = emailComposerService.getMessage("resetPassword.thanks");
    String notRequestedMessage = emailComposerService.getMessage("resetPassword.notRequested");
    String patioTeamMessage = emailComposerService.getMessage("resetPassword.patioTeam");

    Map<String, Object> emailBodyVars = new HashMap<>();
    emailBodyVars.put("subject", emailSubject);
    emailBodyVars.put("greetings", greetingsMessage);
    emailBodyVars.put("main", emailMainMessage);
    emailBodyVars.put("link", this.getChangePasswordLink(user.getOtp()));
    emailBodyVars.put("notRequested", notRequestedMessage);
    emailBodyVars.put("thanks", thanksMessage);
    emailBodyVars.put("patioTeam", patioTeamMessage);

    return emailComposerService.composeEmail(
        emailRecipient, emailSubject, emailBodyTemplate, emailBodyVars);
  }

  private String getChangePasswordLink(String userOTP) {
    return urlResolverService.resolve(this.resetPasswordUrl, userOTP);
  }
}
