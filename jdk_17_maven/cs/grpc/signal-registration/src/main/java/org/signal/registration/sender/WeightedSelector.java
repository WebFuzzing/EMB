package org.signal.registration.sender;

import com.google.common.annotations.VisibleForTesting;
import com.google.i18n.phonenumbers.Phonenumber;
import io.micronaut.context.annotation.EachBean;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Pair;
import org.signal.registration.sender.fictitious.FictitiousNumberVerificationCodeSender;
import org.signal.registration.sender.prescribed.PrescribedVerificationCodeSender;
import org.signal.registration.util.PhoneNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nullable;

/**
 * Selects a sender for a specific {@link MessageTransport}
 * <p>
 * Attempts to find the sender according to these prioritized rules: 1. If we should always use a specific sender for
 * the phone number, use that 2. If we should always use a specific sender for the region, use that 3. Do weighted
 * random selection, and if the selection supports the request, use that 4. Iterate through fallbacks and select the
 * first sender that supports the request 5. Use the first fallback sender
 */
@EachBean(WeightedSelectorConfiguration.class)
@Singleton
public class WeightedSelector {
  private static final Logger logger = LoggerFactory.getLogger(WeightedSelector.class);

  private final MessageTransport transport;
  private final List<VerificationCodeSender> fallbackSenders;
  private final Optional<EnumeratedDistribution<VerificationCodeSender>> defaultDist;
  private final Map<String, EnumeratedDistribution<VerificationCodeSender>> regionalDist;
  private final Map<String, VerificationCodeSender> regionOverrides;
  private final Map<String, VerificationCodeSender> senders;

  public WeightedSelector(
      final WeightedSelectorConfiguration config,
      final List<VerificationCodeSender> verificationCodeSenders) {
    this(RANDOM, config, verificationCodeSenders);
  }

  @VisibleForTesting
  WeightedSelector(
      final RandomGenerator random,
      final WeightedSelectorConfiguration config,
      final List<VerificationCodeSender> verificationCodeSenders) {
    logger.info("Configuring WeightedSelector for transport {} with {}", config.transport(), config);

    this.transport = config.transport();

    // used to lookup senders from configuration strings
    senders = verificationCodeSenders
        .stream()
        .filter(sender -> !(sender instanceof FictitiousNumberVerificationCodeSender || sender instanceof PrescribedVerificationCodeSender))
        .collect(Collectors.toMap(VerificationCodeSender::getName, Function.identity()));

    this.fallbackSenders = config.fallbackSenders().stream().map(s -> parseSender(senders, s)).toList();
    this.defaultDist = Optional.of(config.defaultWeights())
        .filter(weights -> !weights.isEmpty())
        .map(weights -> parseDistribution(random, senders, weights));

    this.regionalDist = config.regionWeights().entrySet().stream().collect(Collectors.toMap(
        e -> e.getKey().toUpperCase(),
        weights -> parseDistribution(random, senders, weights.getValue())
    ));

    this.regionOverrides = config.regionOverrides().entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey().toUpperCase(),
            e -> parseSender(senders, e.getValue())));
  }

  private static final RandomGenerator RANDOM = new AbstractRandomGenerator() {

    @Override
    public void setSeed(final long seed) {
      ThreadLocalRandom.current().setSeed(seed);
    }

    @Override
    public double nextDouble() {
      return ThreadLocalRandom.current().nextDouble();
    }
  };

  public VerificationCodeSender chooseVerificationCodeSender(
      final Phonenumber.PhoneNumber phoneNumber,
      final List<Locale.LanguageRange> languageRanges,
      final ClientType clientType,
      final @Nullable String preferredSender) {

    if (preferredSender != null && senders.containsKey(preferredSender)) {
      return this.senders.get(preferredSender);
    }

    // check for region based overrides
    final String region = PhoneNumbers.regionCodeUpper(phoneNumber);
    if (this.regionOverrides.containsKey(region)) {
      return this.regionOverrides.get(region);
    }

    // make a weighted selection if we have one configured
    final Optional<VerificationCodeSender> weightedSelection = Optional
        .ofNullable(this.regionalDist.get(region)).or(() -> this.defaultDist)
        .map(EnumeratedDistribution::sample);

    return Stream
        // [selected sender, fallbackSenders...]
        .concat(weightedSelection.stream(), this.fallbackSenders.stream())
        // get first sender that supports the destination
        .filter(s -> s.supportsDestination(this.transport, phoneNumber, languageRanges, clientType)).findFirst()
        // or, if none support the destination, the first fallbackSender
        .orElse(this.fallbackSenders.get(0));

  }

  public MessageTransport getTransport() {
    return transport;
  }

  /**
   * Map a sender string to a particular injected VerificationCodeSender
   *
   * @throws IllegalStateException if no matching sender is found
   */
  private static VerificationCodeSender parseSender(
      final Map<String, VerificationCodeSender> senders,
      final String senderName) throws IllegalStateException {

    final VerificationCodeSender sender = senders.get(senderName);
    if (sender == null) {
      throw new IllegalStateException("Invalid configuration: unknown sender name " + senderName);
    }
    return sender;

  }

  /**
   * Build an {@link EnumeratedDistribution} of senders from a weighted configuration
   *
   * @throws IllegalStateException if a sender string doesn't correspond to any provided sender
   */
  private static EnumeratedDistribution<VerificationCodeSender> parseDistribution(
      final RandomGenerator random,
      final Map<String, VerificationCodeSender> senders,
      final Map<String, Integer> weights) throws IllegalStateException {
    final List<Pair<VerificationCodeSender, Double>> pmf = weights
        .entrySet()
        .stream()
        // drop any senders with 0 weight
        .filter(e -> e.getValue() > 0)
        .map(e -> Pair.create(parseSender(senders, e.getKey()), e.getValue().doubleValue()))
        .toList();
    return new EnumeratedDistribution<>(random, pmf);
  }
}
