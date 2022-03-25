import kebabCase from "just-kebab-case";
import * as path from "path";
import {
  Arg,
  Ctx,
  FieldResolver,
  ID,
  Query,
  Resolver,
  Root,
} from "type-graphql";
import conferences from "../../conferences";
import { Conference, getConference } from "../Conference";
import { Contact, getSessionSpeakers } from "../Contact";
import { IContext } from "../Context";
import { resolveSessions } from "../Schedule";
import { Series } from "../Series";
import { Session, SessionType } from "../Session";
import series from "./conferenceSeries";
import loadAttendees from "./load-attendees";

@Resolver((_) => Conference)
class ConferenceResolver {
  @Query((_) => Conference)
  public conference(@Arg("id", (_) => ID) id: string) {
    return getConference(id);
  }

  @Query((_) => [Conference])
  public conferences() {
    return Object.values(conferences);
  }

  @Query((_) => [Conference], {
    deprecationReason: "Use `conferences` instead",
  })
  public allConferences() {
    return Object.values(conferences);
  }

  @FieldResolver((_) => Series)
  public series(@Root() conference: Conference) {
    const id = conference.id;

    for (const seriesId of Object.keys(series)) {
      const oneSeries = series[seriesId];
      if (oneSeries.conferences.indexOf(id) !== -1) {
        return oneSeries;
      }
    }

    return;
  }

  @FieldResolver((_) => [Contact])
  public allSpeakers(@Root() conference: Conference) {
    const talks = resolveSessions(conference.schedules, [
      SessionType.TALK,
      SessionType.KEYNOTE,
      SessionType.LIGHTNING_TALK,
      SessionType.PANEL,
      SessionType.WORKSHOP,
    ]);

    return getSessionSpeakers(conference, talks);
  }

  @FieldResolver((_) => [Contact])
  public speakers(@Root() conference: Conference) {
    const talks = resolveSessions(conference.schedules, [SessionType.TALK]);

    return getSessionSpeakers(conference, talks);
  }

  @FieldResolver((_) => [Contact])
  public async attendees(@Root() conference: Conference, @Ctx() ctx: IContext) {
    const speakers = getSessionSpeakers(
      conference,
      resolveSessions(conference.schedules, [
        SessionType.KEYNOTE,
        SessionType.LIGHTNING_TALK,
        SessionType.TALK,
        SessionType.WORKSHOP,
      ])
    );

    return conference.organizers.concat(
      speakers,
      await loadAttendees(
        conference,
        `${path.join(
          ctx.projectRoot,
          "attendees",
          kebabCase(conference.name)
        )}.csv`
      )
    );
  }

  @FieldResolver((_) => [Session], {
    deprecationReason:
      "Use `keynotes`,s `fullTalks` and `lightningTalks` instead",
  })
  public talks(@Root() conference: Conference) {
    return resolveSessions(conference.schedules, [
      SessionType.LIGHTNING_TALK,
      SessionType.TALK,
      SessionType.KEYNOTE,
    ]);
  }

  @FieldResolver((_) => [Session])
  public keynotes(@Root() conference: Conference) {
    return resolveSessions(conference.schedules, [SessionType.KEYNOTE]);
  }

  @FieldResolver((_) => [Session])
  public keynoteSpeakers(@Root() conference: Conference) {
    const talks = resolveSessions(conference.schedules, [SessionType.KEYNOTE]);

    return getSessionSpeakers(conference, talks);
  }

  @FieldResolver((_) => [Session])
  public fullTalks(@Root() conference: Conference) {
    return resolveSessions(conference.schedules, [SessionType.TALK]);
  }

  @FieldResolver((_) => [Session])
  public fullTalkSpeakers(@Root() conference: Conference) {
    const talks = resolveSessions(conference.schedules, [SessionType.TALK]);

    return getSessionSpeakers(conference, talks);
  }

  @FieldResolver((_) => [Session])
  public lightningTalks(@Root() conference: Conference) {
    return resolveSessions(conference.schedules, [SessionType.LIGHTNING_TALK]);
  }

  @FieldResolver((_) => [Session])
  public lightningTalkSpeakers(@Root() conference: Conference) {
    const talks = resolveSessions(conference.schedules, [
      SessionType.LIGHTNING_TALK,
    ]);

    return getSessionSpeakers(conference, talks);
  }

  @FieldResolver((_) => [Session])
  public workshops(@Root() conference: Conference) {
    return resolveSessions(conference.schedules, [SessionType.WORKSHOP]);
  }

  @FieldResolver((_) => [Session])
  public workshopInstructors(@Root() conference: Conference) {
    const talks = resolveSessions(conference.schedules, [SessionType.WORKSHOP]);

    return getSessionSpeakers(conference, talks);
  }
}

export default ConferenceResolver;
