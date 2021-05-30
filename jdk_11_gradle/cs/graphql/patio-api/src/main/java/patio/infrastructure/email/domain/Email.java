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
package patio.infrastructure.email.domain;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import patio.common.domain.utils.Builder;

/**
 * Represents the model of a given email
 *
 * @since 0.1.0
 */
public final class Email {
  @NotNull @NotBlank private String recipient;

  @NotNull @NotBlank private String subject;

  @SuppressWarnings("PMD.ShortVariable")
  private List<String> cc = new ArrayList<>();

  private List<String> bcc = new ArrayList<>();
  private String htmlBody;
  private String textBody;
  private String replyTo;

  private Email() {
    /* empty */
  }

  /**
   * Creates a builder to build instances of type {@link Email}
   *
   * @return a {@link Builder} that creates instances of type {@link Email}
   * @since 0.1.0
   */
  public static Builder<Email> builder() {
    return Builder.build(Email::new);
  }

  /**
   * Returns the email recipient
   *
   * @return the email recipient
   * @since 0.1.0
   */
  public String getRecipient() {
    return recipient;
  }

  /**
   * Sets the email recipient
   *
   * @param recipient the email recipient
   * @since 0.1.0
   */
  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  /**
   * Returns the email subject
   *
   * @return the email subject
   * @since 0.1.0
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the email subject
   *
   * @param subject the email subject
   * @since 0.1.0
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Returns the email cc
   *
   * @return the email cc
   * @since 0.1.0
   */
  public List<String> getCc() {
    return cc;
  }

  /**
   * Sets the email cc
   *
   * @param cc the email cc
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.ShortVariable")
  public void setCc(List<String> cc) {
    this.cc = cc;
  }

  /**
   * Returns the email bcc
   *
   * @return the email bcc
   * @since 0.1.0
   */
  public List<String> getBcc() {
    return bcc;
  }

  /**
   * Sets the email bcc
   *
   * @param bcc the email bcc
   * @since 0.1.0
   */
  public void setBcc(List<String> bcc) {
    this.bcc = bcc;
  }

  /**
   * Returns the email html body
   *
   * @return the email html body
   * @since 0.1.0
   */
  public String getHtmlBody() {
    return htmlBody;
  }

  /**
   * Sets the html body
   *
   * @param htmlBody the email html body
   * @since 0.1.0
   */
  public void setHtmlBody(String htmlBody) {
    this.htmlBody = htmlBody;
  }

  /**
   * Returns the email body as text
   *
   * @return the email text body
   * @since 0.1.0
   */
  public String getTextBody() {
    return textBody;
  }

  /**
   * Sets the body text
   *
   * @param textBody the body text
   * @since 0.1.0
   */
  public void setTextBody(String textBody) {
    this.textBody = textBody;
  }

  /**
   * Returns the email replyTo property
   *
   * @return the reply to property
   * @since 0.1.0
   */
  public String getReplyTo() {
    return replyTo;
  }

  /**
   * Sets the replyTo property
   *
   * @param replyTo the email replyTo property
   * @since 0.1.0
   */
  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }
}
