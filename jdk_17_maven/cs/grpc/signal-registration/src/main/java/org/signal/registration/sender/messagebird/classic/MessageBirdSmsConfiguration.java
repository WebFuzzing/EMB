package org.signal.registration.sender.messagebird.classic;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.bind.annotation.Bindable;
import java.time.Duration;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @param originator The originating number or sender id for MessageBird SMS attempts
 * @param sessionTtl How long verification sessions are valid for
 */
@Context
@ConfigurationProperties("messagebird.sms")
public record MessageBirdSmsConfiguration(
    @Bindable(defaultValue = "PT10M") @NotNull Duration sessionTtl) {

}
