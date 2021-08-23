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
package patio.voting.services.internal;

import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.group.domain.Group;
import patio.group.repositories.GroupRepository;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.URLResolverService;
import patio.user.domain.User;
import patio.voting.domain.Voting;
import patio.voting.repositories.VotingRepository;
import patio.voting.services.VotingScheduling;
import patio.voting.services.VotingStatsService;

/**
 * Default implementation to create new voting and send notifications to their members
 *
 * @since 0.1.0
 */
@Singleton
public class VotingSchedulingService implements VotingScheduling {

  private static final Logger LOG = LoggerFactory.getLogger(VotingSchedulingService.class);

  private final transient String votingUrl;
  private final transient GroupRepository groupRepository;
  private final transient VotingRepository votingRepository;
  private final transient VotingStatsService votingStatsService;
  private final transient EmailComposerService emailComposerService;
  private final transient EmailService emailService;
  private final transient URLResolverService urlResolverService;

  /**
   * Requires the {@link DefaultVotingService} to get group voting information and {@link
   * EmailService} to be able to send notification to group members
   *
   * @param votingUrl to get the link from configuration
   * @param groupRepository to be able to get group details
   * @param votingRepository to be able to create a new {@link Voting}
   * @param votingStatsService to be able to create a new {@link VotingStatsService}
   * @param emailComposerService service to compose the {@link Email} notifications
   * @param emailService to be able to send notifications to group members
   * @param urlResolverService to resolve possible link urls for emails
   * @since 0.1.0
   */
  public VotingSchedulingService(
      @Value("${front.urls.voting:none}") String votingUrl,
      GroupRepository groupRepository,
      VotingRepository votingRepository,
      VotingStatsService votingStatsService,
      EmailComposerService emailComposerService,
      EmailService emailService,
      URLResolverService urlResolverService) {
    this.votingUrl = votingUrl;
    this.groupRepository = groupRepository;
    this.votingRepository = votingRepository;
    this.votingStatsService = votingStatsService;
    this.emailComposerService = emailComposerService;
    this.emailService = emailService;
    this.urlResolverService = urlResolverService;
  }

  @Override
  @Scheduled(fixedRate = "30s", initialDelay = "30s")
  public void scheduleVoting() {
    checkVoting();
  }

  @Transactional
  /* default */ void checkVoting() {
    LOG.info("checking voting creation");
    this.findAllToCreateVotingFrom().map(this::createVoting).forEach(this::notifyMembers);
    this.findAllToExpireVotingFrom().forEach(this::expireVoting);
  }

  private Stream<Voting> findAllToExpireVotingFrom() {
    return groupRepository.findAllExpiredVotingsByTime(OffsetDateTime.now());
  }

  private Stream<Group> findAllToCreateVotingFrom() {
    OffsetDateTime now = OffsetDateTime.now();
    DayOfWeek dayOfWeek = now.getDayOfWeek();

    Stream<Group> eligibleGroups =
        groupRepository.findAllGroupsInVotingDayAndInVotingPeriod(dayOfWeek.toString(), now);

    List<Group> groupsWithVoting =
        groupRepository.findAllGroupsWithVotingInCurrentVotingPeriod().collect(Collectors.toList());

    return eligibleGroups.filter(Predicate.not(groupsWithVoting::contains));
  }

  private Voting createVoting(Group group) {
    LOG.info(String.format("creating new voting for group %s", group.getId()));

    Voting voting =
        Voting.newBuilder()
            .with(v -> v.setGroup(group))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now()))
            .build();

    Voting savedVoting = votingRepository.save(voting);

    votingStatsService.createVotingStat(savedVoting);

    LOG.info(String.format("created voting %s", savedVoting.getId()));
    return voting;
  }

  private void expireVoting(Voting voting) {
    voting.setExpired(true);
    votingRepository.save(voting);
  }

  private void notifyMembers(Voting voting) {
    Group group = voting.getGroup();

    LOG.info(String.format("notifying members or group %s", group.getId()));

    group.getUsers().stream()
        .map(ug -> composeEmail(ug.getUser(), ug.getGroup(), voting))
        .forEach(emailService::send);
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private Email composeEmail(User user, Group group, Voting voting) {
    String emailBodyTemplate = emailComposerService.getMessage("voting.bodyTemplate");
    String emailRecipient = user.getEmail();

    Map<String, Object> subjectMessageVars =
        Map.of(
            "today", emailComposerService.getTodayMessage(),
            "groupName", group.getName());
    String emailSubject = emailComposerService.getMessage("voting.subject", subjectMessageVars);

    Map<String, Object> greetingMessageVars = Map.of("username", user.getName());
    String greetingsMessage =
        emailComposerService.getMessage("voting.greetings", greetingMessageVars);
    String thanksMessage = emailComposerService.getMessage("voting.thanks");
    String disclaimerMessage = emailComposerService.getMessage("voting.disclaimer");
    String todayMessage = emailComposerService.getMessage("voting.today", subjectMessageVars);
    String questionMessage = emailComposerService.getMessage("voting.question", subjectMessageVars);

    Map<String, Object> emailBodyVars = new HashMap<>();
    emailBodyVars.put("question", questionMessage);
    emailBodyVars.put("today", todayMessage);
    emailBodyVars.put("greetings", greetingsMessage);
    emailBodyVars.put("groupName", group.getName());
    emailBodyVars.put("thanks", thanksMessage);
    emailBodyVars.put("disclaimer", disclaimerMessage);
    emailBodyVars.put("link", getVotingLink(group.getId(), voting.getId()));
    emailBodyVars.put("frontUrl", urlResolverService.resolve(""));

    return emailComposerService.composeEmail(
        emailRecipient, emailSubject, emailBodyTemplate, emailBodyVars);
  }

  private String getVotingLink(UUID groupId, UUID votingId) {
    return urlResolverService.resolve(this.votingUrl, groupId, votingId);
  }
}
