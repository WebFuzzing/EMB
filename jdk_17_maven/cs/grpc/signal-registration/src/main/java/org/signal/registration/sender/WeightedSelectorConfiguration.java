package org.signal.registration.sender;

import com.google.i18n.phonenumbers.Phonenumber;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Sender configuration for a single transport {@link WeightedSelector}
 *
 * @param transport       The message transport that this configuration applies to
 * @param fallbackSenders An ordered list of senders to fall back on. The first sender that supports the request will be
 *                        used, or the first sender if no sender supports the request.
 * @param defaultWeights  The proportion of each service type to use
 * @param regionWeights   Override of weights by region
 * @param regionOverrides Map from region to service that should always be used for that region
 */
@EachProperty("selection")
public record WeightedSelectorConfiguration(
    @Parameter MessageTransport transport,
    @NotEmpty List<@NotBlank String> fallbackSenders,
    Map<@NotBlank String, Integer> defaultWeights,
    Map<@NotBlank String, Map<@NotBlank String, Integer>> regionWeights,
    Map<@NotBlank String, @NotBlank String> regionOverrides
) {

}
