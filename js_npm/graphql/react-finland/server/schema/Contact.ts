import flatMap from "lodash/flatMap";
import uniqBy from "lodash/uniqBy";
import { Field, ObjectType } from "type-graphql";
import { Conference } from "./Conference";
import { Image } from "./Image";
import Keyword from "./keywords";
import { Location } from "./Location";
import { resolveSessions } from "./Schedule";
import { Session, SessionType } from "./Session";
import { Social } from "./Social";

export enum ContactType {
  PRESS = "PRESS",
  SPEAKER = "SPEAKER",
  TALK = "TALK",
  LIGHTNING_TALK = "LIGHTNING_TALK",
  KEYNOTE = "KEYNOTE",
  WORKSHOP = "WORKSHOP",
  WORKSHOP_HOST = "WORKSHOP_HOST",
  ORGANIZER = "ORGANIZER",
  SPONSOR = "SPONSOR",
  GOLD_SPONSOR = "GOLD_SPONSOR",
  SILVER_SPONSOR = "SILVER_SPONSOR",
  BRONZE_SPONSOR = "BRONZE_SPONSOR",
  PLATFORM_SPONSOR = "PLATFORM_SPONSOR",
  PARTNER = "PARTNER",
  ATTENDEE = "ATTENDEE",
}

@ObjectType()
export class Contact {
  // Needed for badges
  @Field((_) => String)
  public firstName?: string;

  // Needed for badges
  @Field((_) => String)
  public lastName?: string;

  // Combine with first/lastName somehow? Problematic for companies.
  @Field((_) => String)
  public name!: string;

  @Field((_) => String)
  public about!: string;

  @Field((_) => String, { nullable: true })
  public aboutShort?: string;

  // TODO: This might become a Contact reference eventually
  @Field((_) => String, { nullable: true })
  public company?: string;

  @Field((_) => Image)
  public image!: Image;

  @Field((_) => [ContactType])
  public type!: ContactType[];

  @Field((_) => Social)
  public social!: Social;

  @Field((_) => [String], { nullable: true })
  public keywords?: Keyword[];

  @Field((_) => Location)
  public location!: Location;

  @Field((_) => [Session], { nullable: true })
  public talks?: Session[];

  @Field((_) => [Session], { nullable: true })
  public workshops?: Session[];

  @Field((_) => Boolean, { nullable: true })
  public noPhotography?: boolean;
}

// TODO: Maybe this should become a static method of Conference
export function getSessionSpeakers(
  conference: Conference,
  sessions?: Session[]
): Contact[] {
  const talks = resolveSessions(conference.schedules, [
    SessionType.LIGHTNING_TALK,
    SessionType.TALK,
    SessionType.KEYNOTE,
  ]);
  const workshops = resolveSessions(conference.schedules, [
    SessionType.WORKSHOP,
  ]);
  const speakers = uniqBy(
    flatMap(sessions, (session) =>
      (session.people || []).concat(
        session.sessions
          ? flatMap(session.sessions, (session) => session.people || [])
          : []
      )
    ),
    "name"
  );

  return speakers.map((speaker) => ({
    ...speaker,
    talks: talks
      ? talks.filter(
          ({ people }) =>
            people && people.find((person) => person.name === speaker.name)
        )
      : [],
    workshops: workshops
      ? workshops.filter(
          ({ people }) =>
            people && people.find((person) => person.name === speaker.name)
        )
      : [],
  }));
}
