import map from "lodash/map";
import { Field, ID, ObjectType } from "type-graphql";
import conferences from "../conferences";
import { Contact, ContactType } from "./Contact";
import { Location } from "./Location";
import { Schedule } from "./Schedule";
import { Session } from "./Session";

@ObjectType()
export class Conference {
  @Field((_) => ID)
  public id!: string;

  @Field((_) => String)
  public series!: string;

  @Field((_) => String)
  public name!: string;

  @Field((_) => Contact)
  public organizer!: Contact;

  @Field((_) => String)
  public year!: string;

  @Field((_) => String)
  public startDate!: string;

  @Field((_) => String)
  public endDate!: string;

  @Field((_) => String)
  public slogan!: string;

  @Field((_) => String)
  public websiteUrl!: string;

  @Field((_) => [Location], { nullable: true })
  public locations?: Location[];

  @Field((_) => [Contact])
  public organizers!: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public mcs?: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public partners?: Contact[];

  @Field((_) => [Contact])
  public sponsors!: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public goldSponsors!: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public silverSponsors!: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public bronzeSponsors!: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public platformSponsors!: Contact[];

  @Field((_) => [Schedule])
  public schedules!: Schedule[];

  @Field((_) => [Contact], { nullable: true })
  public allSpeakers?: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public speakers?: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public keynoteSpeakers?: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public fullTalkSpeakers?: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public lightningTalkSpeakers?: Contact[];

  @Field((_) => [Contact], { nullable: true })
  public workshopInstructors?: Contact[];

  @Field((_) => [Session], { nullable: true })
  public talks?: Session[];

  @Field((_) => [Session], { nullable: true })
  public workshops?: Session[];

  @Field((_) => [Contact])
  public attendees?: Contact[];
}

// TODO: Maybe this should become a static method of Conference
export function getConference(id: string): Conference {
  if (conferences[id]) {
    const conference = conferences[id];

    return {
      ...conference,
      sponsors: attachSponsorTypes(conference, conference.sponsors),
    };
  } else {
    throw new Error("Unknown conference");
  }
}

// TODO: Extract sponsor levels to this code won't be needed anymore
function attachSponsorTypes(
  conference: Conference,
  sponsors: Conference["sponsors"]
) {
  const {
    goldSponsors,
    silverSponsors,
    bronzeSponsors,
    platformSponsors,
    partners,
  } = conference;

  return map(sponsors, ({ name, type, ...rest }) => ({
    ...rest,
    name,
    type: type
      .concat(
        isSponsor(goldSponsors, name, ContactType.GOLD_SPONSOR),
        isSponsor(silverSponsors, name, ContactType.SILVER_SPONSOR),
        isSponsor(bronzeSponsors, name, ContactType.BRONZE_SPONSOR),
        isSponsor(platformSponsors, name, ContactType.PLATFORM_SPONSOR),
        isSponsor(partners, name, ContactType.PARTNER)
      )
      .filter(Boolean),
  }));
}

function isSponsor(sponsors, sponsorName, contactType) {
  return sponsors.find(({ name }) => sponsorName === name) ? contactType : null;
}
