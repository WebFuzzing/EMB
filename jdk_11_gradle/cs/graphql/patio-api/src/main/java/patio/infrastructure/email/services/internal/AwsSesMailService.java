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
package patio.infrastructure.email.services.internal;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;

/**
 * Sends emails using AWS infrastructure
 *
 * @since 0.1.0
 */
@Singleton
@Primary
@SuppressWarnings("all")
public class AwsSesMailService implements EmailService {
  private static final Logger LOG = LoggerFactory.getLogger(AwsSesMailService.class);

  private final String awsRegion;

  private final String sourceEmail;

  private final boolean emailEnabled;

  private final AWSCredentialsProvider credentialsProvider;

  /**
   * Initializes email service
   *
   * @param credentialsProvider authentication credentials
   * @param emailEnabled whether sending emails should be enabled or not
   * @param awsRegion aws region
   * @param sourceEmail source email
   * @since 0.1.0
   */
  public AwsSesMailService(
      AWSCredentialsProvider credentialsProvider,
      @Value("${aws.mail.enabled}") boolean emailEnabled,
      @Value("${aws.mail.region:none}") String awsRegion,
      @Value("${aws.mail.sourceemail:none}") String sourceEmail) {
    this.credentialsProvider = credentialsProvider;
    this.emailEnabled = emailEnabled;
    this.awsRegion = awsRegion;
    this.sourceEmail = sourceEmail;
  }

  private Body bodyOfEmail(Email email) {
    if (email.getHtmlBody() != null && !email.getHtmlBody().isEmpty()) {
      Content htmlBody = new Content().withData(email.getHtmlBody());
      return new Body().withHtml(htmlBody);
    }
    if (email.getTextBody() != null && !email.getTextBody().isEmpty()) {
      Content textBody = new Content().withData(email.getTextBody());
      return new Body().withHtml(textBody);
    }
    return new Body();
  }

  @Override
  public void send(Email email) {
    if (this.emailEnabled) {
      sendEmail(email);
    } else {
      LOG.info("Sending email is disabled");
    }
  }

  private void sendEmail(Email email) {
    Destination destination = new Destination().withToAddresses(email.getRecipient());
    if (email.getCc() != null) {
      destination = destination.withCcAddresses(email.getCc());
    }
    if (email.getBcc() != null) {
      destination = destination.withBccAddresses(email.getBcc());
    }
    Content subject = new Content().withData(email.getSubject());
    Body body = bodyOfEmail(email);
    Message message = new Message().withSubject(subject).withBody(body);

    SendEmailRequest request =
        new SendEmailRequest()
            .withSource(sourceEmail)
            .withDestination(destination)
            .withMessage(message);

    if (email.getReplyTo() != null) {
      request = request.withReplyToAddresses();
    }

    try {
      if (LOG.isInfoEnabled()) {
        LOG.info("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
      }

      AmazonSimpleEmailService client =
          AmazonSimpleEmailServiceClientBuilder.standard()
              .withCredentials(credentialsProvider)
              .withRegion(awsRegion)
              .build();

      SendEmailResult sendEmailResult = client.sendEmail(request);

      if (LOG.isInfoEnabled()) {
        LOG.info("Email sent! {}", sendEmailResult.toString());
      }
    } catch (Exception ex) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("The email was not sent.");
        LOG.warn("Error message: {}", ex.getMessage());
      }
    }
  }
}
